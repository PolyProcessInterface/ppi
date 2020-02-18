package org.sar.ppi;

/**
 * Node
 */
public class MpiNode implements Node{
	protected int id;

	public MpiNode(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

}