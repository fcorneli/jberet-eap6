package org.jberet.test.integration.chunk;

import javax.ejb.EJB;
import javax.inject.Inject;

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
				.addClasses(MyTestItemReader.class, MyTestItemProcessor.class,
						MyTestItemWriter.class, ChunkBean.class,
						MyEntity.class, ChunkController.class,
						FoobarException.class)
				.addAsManifestResource(
						BatchTestCase.class.getResource("/batch.xml"),
						"batch.xml")
				.addAsManifestResource(
						BatchTestCase.class.getResource("/test-chunk.xml"),
						"batch-jobs/test-chunk.xml")
				.addAsManifestResource(
						BatchTestCase.class.getResource("/beans.xml"),
						"beans.xml")
				.addAsManifestResource(
						BatchTestCase.class.getResource("/persistence.xml"),
						"persistence.xml");
		return jar;
	}

	@EJB
	private ChunkBean chunkBean;

	@Inject
	private ChunkController chunkController;

	@Test
	public void testChunkedBatchJob() throws Exception {
		this.chunkBean.initData();
		long executionId = this.chunkBean.startJob();
		this.chunkBean.waitForJobFinished(executionId);
		this.chunkBean.verifyProcessing();
	}

	@Test
	public void testRestart() throws Exception {
		this.chunkController.explodeAtIndex(18);
		this.chunkBean.initData();
		long executionId = this.chunkBean.startJob();
		this.chunkBean.waitForJobFinished(executionId);
		this.chunkBean.verifyProcessing();
	}
}
