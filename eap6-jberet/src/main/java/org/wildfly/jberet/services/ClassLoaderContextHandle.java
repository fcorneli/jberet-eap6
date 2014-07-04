package org.wildfly.jberet.services;

import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ClassLoaderContextHandle implements ContextHandle {
    private final ClassLoader classLoader;

    ClassLoaderContextHandle(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Handle setup() {
        final ClassLoader current = getContextClassLoader();
        setContextClassLoader(classLoader);
        return new Handle() {
            @Override
            public void tearDown() {
            	setContextClassLoader(current);
            }
        };
    }
    
    static ClassLoader getContextClassLoader() {
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
    
    static void setContextClassLoader(final ClassLoader classLoader) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        } else {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    Thread.currentThread().setContextClassLoader(classLoader);
                    return null;
                }
            });
        }
    }
}
