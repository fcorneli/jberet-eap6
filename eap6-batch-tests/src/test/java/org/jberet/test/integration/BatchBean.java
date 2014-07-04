package org.jberet.test.integration;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Stateless;

@Stateless
public class BatchBean {

	private JobOperator jobOperator;

	@PostConstruct
	public void postConstruct() {
		this.jobOperator = BatchRuntime.getJobOperator();
	}

	public boolean hasJobOperator() {
		return this.jobOperator != null;
	}

	public long startTestJob() {
		return this.jobOperator.start("test", null);
	}

	public Set<String> getJobNames() {
		return this.jobOperator.getJobNames();
	}
}
