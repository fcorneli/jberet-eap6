package org.jberet.test.integration.chunk;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

@Stateless
public class ChunkBean {

	private final static Logger LOGGER = Logger.getLogger(ChunkBean.class);

	private JobOperator jobOperator;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void postConstruct() {
		this.jobOperator = BatchRuntime.getJobOperator();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void initData() {
		int count = 10;
		while (count-- != 0) {
			MyEntity myEntity = new MyEntity("value " + count);
			this.entityManager.persist(myEntity);
		}
	}

	public long startJob() {
		long executionId = this.jobOperator.start("test-chunk", null);
		return executionId;
	}

	public void waitForJobFinished(long executionId)
			throws InterruptedException {
		while (this.jobOperator.getJobExecution(executionId).getBatchStatus() != BatchStatus.COMPLETED) {
			LOGGER.debug("waiting");
			Thread.sleep(100);
		}
	}

	public void verifyProcessing() {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery<MyEntity> criteriaQuery = criteriaBuilder
				.createQuery(MyEntity.class);
		Root<MyEntity> root = criteriaQuery.from(MyEntity.class);
		criteriaQuery.select(root);
		TypedQuery<MyEntity> typedQuery = this.entityManager
				.createQuery(criteriaQuery);
		List<MyEntity> resultList = typedQuery.getResultList();
		for (MyEntity myEntity : resultList) {
			LOGGER.debug("result: " + myEntity.getValue());
		}
	}
}
