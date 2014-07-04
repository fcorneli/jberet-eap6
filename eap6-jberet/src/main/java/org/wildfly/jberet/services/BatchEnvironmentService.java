/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.jberet.services;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;

import org.jberet.repository.JobRepository;
import org.jberet.spi.ArtifactFactory;
import org.jberet.spi.BatchEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.jberet.BatchEnvironmentFactory;
import org.wildfly.jberet.WildFlyArtifactFactory;
import org.wildfly.jberet._private.WildFlyBatchLogger;
import org.wildfly.jberet.services.ContextHandle.ChainedContextHandle;
import org.wildfly.jberet.services.ContextHandle.Handle;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class BatchEnvironmentService implements Service<BatchEnvironment> {

    // This can be removed after the getBatchConfigurationProperties() is removed from jBeret
    private static final Properties PROPS = new Properties();

    private final InjectedValue<BeanManager> beanManagerInjector = new InjectedValue<BeanManager>();
    private final InjectedValue<ExecutorService> executorServiceInjector = new InjectedValue<ExecutorService>();
    private final InjectedValue<TransactionManager> transactionManagerInjector = new InjectedValue<TransactionManager>();

    private final JobRepository jobRepository;
    private final ClassLoader classLoader;
    private BatchEnvironment batchEnvironment = null;

    public BatchEnvironmentService(final ClassLoader classLoader, final JobRepository jobRepository) {
        this.classLoader = classLoader;
        this.jobRepository = jobRepository;
    }

    @Override
    public synchronized void start(final StartContext context) throws StartException {
        WildFlyBatchLogger.LOGGER.debugf("Creating batch environment; %s", classLoader);
        final BatchEnvironment batchEnvironment = new WildFlyBatchEnvironment(beanManagerInjector.getOptionalValue(),
                executorServiceInjector.getValue(), transactionManagerInjector.getOptionalValue());
        // Add the service to the factory
        BatchEnvironmentFactory.getInstance().add(classLoader, batchEnvironment);
        this.batchEnvironment = batchEnvironment;
    }

    @Override
    public synchronized void stop(final StopContext context) {
        WildFlyBatchLogger.LOGGER.debugf("Removing batch environment; %s", classLoader);
        BatchEnvironmentFactory.getInstance().remove(classLoader);
        batchEnvironment = null;
    }

    @Override
    public synchronized BatchEnvironment getValue() throws IllegalStateException, IllegalArgumentException {
        return batchEnvironment;
    }

    public InjectedValue<BeanManager> getBeanManagerInjector() {
        return beanManagerInjector;
    }

    public InjectedValue<ExecutorService> getExecutorServiceInjector() {
        return executorServiceInjector;
    }

    public InjectedValue<TransactionManager> getTransactionManagerInjector() {
        return transactionManagerInjector;
    }

    private class WildFlyBatchEnvironment implements BatchEnvironment {

        private final ArtifactFactory artifactFactory;
        private final ExecutorService executorService;
        private final TransactionManager transactionManager;

        WildFlyBatchEnvironment(final BeanManager beanManager,
                                final ExecutorService executorService, final TransactionManager transactionManager) {
            artifactFactory = (beanManager == null ? null : new WildFlyArtifactFactory(beanManager));
            this.executorService = executorService;
            this.transactionManager = transactionManager;
        }

        @Override
        public ClassLoader getClassLoader() {
            return classLoader;
        }

        @Override
        public ArtifactFactory getArtifactFactory() {
            if (artifactFactory == null) {
                throw WildFlyBatchLogger.LOGGER.serviceNotInstalled("BeanManager");
            }
            return artifactFactory;
        }

        @Override
        public Future<?> submitTask(final Runnable task) {
            final ContextHandle contextHandle = createContextHandle();
            return executorService.submit(new Runnable() {
                @Override
                public void run() {
                    final Handle handle = contextHandle.setup();
                    try {
                        task.run();
                    } finally {
                        handle.tearDown();
                    }
                }
            });
        }

        @Override
        public <T> Future<T> submitTask(final Runnable task, final T result) {
            final ContextHandle contextHandle = createContextHandle();
            return executorService.submit(new Runnable() {
                @Override
                public void run() {
                    final Handle handle = contextHandle.setup();
                    try {
                        task.run();
                    } finally {
                        handle.tearDown();
                    }
                }
            }, result);
        }

        @Override
        public <T> Future<T> submitTask(final Callable<T> task) {
            final ContextHandle contextHandle = createContextHandle();
            return executorService.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    final Handle handle = contextHandle.setup();
                    try {
                        return task.call();
                    } finally {
                        handle.tearDown();
                    }
                }
            });
        }

        @Override
        public TransactionManager getTransactionManager() {
            if (transactionManager == null) {
                throw WildFlyBatchLogger.LOGGER.serviceNotInstalled("TransactionManager");
            }
            return transactionManager;
        }

        @Override
        public JobRepository getJobRepository() {
            return jobRepository;
        }

        /**
         * {@inheritDoc}
         * @deprecated this is no longer used in jBeret and will be removed
         * @return
         */
        @Override
        @Deprecated
        public Properties getBatchConfigurationProperties() {
            return PROPS;
        }

        private ContextHandle createContextHandle() {
            final ClassLoader tccl = getContextClassLoader();
            // If the TCCL is null, use the deployments ModuleClassLoader
            final ClassLoaderContextHandle classLoaderContextHandle = (tccl == null ? new ClassLoaderContextHandle(classLoader) : new ClassLoaderContextHandle(tccl));
            // Class loader handle must be first so the TCCL is set before the other handles execute
            return new ChainedContextHandle(classLoaderContextHandle, new NamespaceContextHandle(), new SecurityContextHandle());
        }
        
        private ClassLoader getContextClassLoader() {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
            } else {
                return Thread.currentThread().getContextClassLoader();
            }
        }
    }
}