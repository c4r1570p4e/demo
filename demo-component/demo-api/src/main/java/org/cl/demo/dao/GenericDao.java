package org.cl.demo.dao;

public interface GenericDao<T> {

	void delete(T t);

	T findById(int id);

	T merge(T t);

	void detach(T t);

	void refresh(T t);

}
