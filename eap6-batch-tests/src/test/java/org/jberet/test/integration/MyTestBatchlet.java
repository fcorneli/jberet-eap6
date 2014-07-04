package org.jberet.test.integration;

import javax.annotation.PostConstruct;
import javax.batch.api.Batchlet;
import javax.inject.Named;

@Named
public class MyTestBatchlet implements Batchlet {

	@PostConstruct
	public void postConstruct() {
	}

	@Override
	public String process() throws Exception {
		return null;
	}

	@Override
	public void stop() throws Exception {
	}
}
