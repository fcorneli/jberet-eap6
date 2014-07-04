package org.jberet.test.integration.jpa;

import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.jberet.test.integration.BatchTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JPATestCase {

	private final static Logger LOGGER = Logger.getLogger(JPATestCase.class);

	@Deployment
	public static Archive<?> deploy() throws Exception {
		JavaArchive jar = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(MyEntity.class, MyBean.class)
				.addAsManifestResource(
						BatchTestCase.class.getResource("/persistence.xml"),
						"persistence.xml");
		return jar;
	}

	@EJB
	private MyBean myBean;

	@Test
	public void testJPA() {
		assertNotNull(this.myBean);
		this.myBean.createEntity(1, "hello world");
		try {
			this.myBean.main(1);
		} catch (Exception e) {
			LOGGER.debug("expected exception: " + e.getClass().getName());
		}
	}
}
