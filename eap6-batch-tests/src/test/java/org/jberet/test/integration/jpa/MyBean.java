package org.jberet.test.integration.jpa;

import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

@Stateless
public class MyBean {

	private final static Logger LOGGER = Logger.getLogger(MyBean.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	private MyBean myBean;

	public void createEntity(long id, String value) {
		MyEntity myEntity = new MyEntity(id, value);
		this.entityManager.persist(myEntity);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void nestedTransaction(long id) {
		MyEntity myEntity = this.entityManager.find(MyEntity.class, id);
		myEntity.setValue(UUID.randomUUID().toString());
	}

	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void testLocking(long id) {
		MyEntity myEntity = this.entityManager.find(MyEntity.class, id);
		myEntity.setValue(UUID.randomUUID().toString());
		LOGGER.debug("before nested transaction");
		this.myBean.nestedTransaction(1);
		LOGGER.debug("after nested transaction");
		try {
			this.entityManager.flush();
			LOGGER.debug("after flush");
		} catch (OptimisticLockException e) {
			LOGGER.debug("flush exception: " + e.getClass().getName());
		}
	}

	public void main(long id) {
		try {
			this.myBean.testLocking(id);
		} catch (Exception e) {
			LOGGER.debug("expected exception: " + e.getClass().getName());
		}
	}
}
