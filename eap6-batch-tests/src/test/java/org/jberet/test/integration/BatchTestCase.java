package org.jberet.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BatchTestCase {

	@Deployment
	public static Archive<?> deploy() throws Exception {
		JavaArchive jar = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(BatchBean.class, MyTestBatchlet.class)
				.addAsManifestResource(
						BatchTestCase.class.getResource("/batch.xml"),
						"batch.xml")
				.addAsManifestResource(
						BatchTestCase.class.getResource("/test.xml"),
						"batch-jobs/test.xml")
				.addAsManifestResource(
						BatchTestCase.class.getResource("/beans.xml"),
						"beans.xml");
		return jar;
	}

	@EJB
	private BatchBean batchBean;

	@Test
	public void testBatchRuntimeAvailable() throws Exception {
		assertNotNull(this.batchBean);
		assertTrue(this.batchBean.hasJobOperator());
	}

	@Test
	public void testStartJob() throws Exception {
		long executionId = this.batchBean.startTestJob();
		Set<String> jobNames = this.batchBean.getJobNames();
		assertTrue(jobNames.contains("test"));

	}
}
