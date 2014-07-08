package org.jberet.test.integration.chunk;

import java.io.Serializable;
import java.util.Iterator;

import javax.batch.api.chunk.ItemReader;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

@Named
public class MyTestItemReader implements ItemReader {

	private final static Logger LOGGER = Logger
			.getLogger(MyTestItemReader.class);

	@PersistenceContext
	private EntityManager entityManager;

	private Iterator<MyEntity> iterator;

	private int index;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		LOGGER.debug("open");
		if (null != checkpoint) {
			this.index = (Integer) checkpoint;
		} else {
			this.index = 0;
		}
		initIterator();
	}

	private void initIterator() {
		CriteriaBuilder criteriaBuilder = this.entityManager
				.getCriteriaBuilder();
		CriteriaQuery<MyEntity> criteriaQuery = criteriaBuilder
				.createQuery(MyEntity.class);
		Root<MyEntity> root = criteriaQuery.from(MyEntity.class);
		criteriaQuery.select(root);
		TypedQuery<MyEntity> typedQuery = this.entityManager
				.createQuery(criteriaQuery);
		typedQuery.setFirstResult(this.index);
		typedQuery.setMaxResults(10);
		this.iterator = typedQuery.getResultList().iterator();
	}

	@Override
	public void close() throws Exception {
		LOGGER.debug("close");
		this.iterator = null;
	}

	@Override
	public Object readItem() throws Exception {
		LOGGER.debug("readItem");
		if (this.iterator.hasNext()) {
			this.index++;
			return this.iterator.next();
		} else {
			initIterator();
			if (this.iterator.hasNext()) {
				this.index++;
				return this.iterator.next();
			}
		}
		return null;
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		LOGGER.debug("checkpointInfo");
		return this.index;
	}
}
