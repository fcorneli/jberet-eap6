package org.jberet.test.integration.chunk;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ChunkController {

	private final static Logger LOGGER = Logger
			.getLogger(ChunkController.class);

	private int index;

	private Integer explosionIndex;

	public void reset() {
		this.index = 0;
		this.explosionIndex = null;
	}

	public void explodeAtIndex(int explosionIndex) {
		this.explosionIndex = explosionIndex;
	}

	public void explosion() {
		this.index++;
		if (null == this.explosionIndex) {
			return;
		}
		if (this.explosionIndex == this.index) {
			this.explosionIndex = null; // one-shot
			LOGGER.debug("foobar");
			throw new FoobarException();
		}
	}
}
