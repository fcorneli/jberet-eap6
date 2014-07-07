package org.jberet.test.integration.chunk;

import java.io.Serializable;
import java.util.List;

import javax.batch.api.chunk.ItemWriter;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

@Named
public class MyTestItemWriter implements ItemWriter {

	private final static Logger LOGGER = Logger
			.getLogger(MyTestItemWriter.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		LOGGER.debug("open");
	}

	@Override
	public void close() throws Exception {
		LOGGER.debug("close");
	}

	@Override
	public void writeItems(List<Object> items) throws Exception {
		LOGGER.debug("writeItem");
		for (Object item : items) {
			this.entityManager.merge(item);
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		LOGGER.debug("checkpointInfo");
		return null;
	}
}
