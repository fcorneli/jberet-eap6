package org.jberet.test.integration.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class MyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String value;

	@Version
	private int version;

	public MyEntity() {
		super();
	}

	public MyEntity(long id, String value) {
		this.id = id;
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
