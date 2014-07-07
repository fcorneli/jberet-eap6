package org.jberet.test.integration.chunk;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

import org.jboss.logging.Logger;

@Named
public class MyTestItemProcessor implements ItemProcessor {

	private final static Logger LOGGER = Logger
			.getLogger(MyTestItemProcessor.class);

	@Override
	public Object processItem(Object item) throws Exception {
		MyEntity myEntity = (MyEntity) item;
		LOGGER.debug("processItem: " + myEntity.getValue());
		myEntity.setValue("processed: " + myEntity.getValue());
		return myEntity;
	}
}
