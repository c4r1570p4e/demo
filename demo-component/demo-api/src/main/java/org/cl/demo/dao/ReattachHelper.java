package org.cl.demo.dao;

public interface ReattachHelper {
	
	
	public boolean contains(Object entity);
	
	public <T> T reattach(T t);
	

}
