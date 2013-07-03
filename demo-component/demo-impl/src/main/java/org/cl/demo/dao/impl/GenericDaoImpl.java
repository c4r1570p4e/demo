package org.cl.demo.dao.impl;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.cl.demo.dao.GenericDao;
import org.hibernate.CacheMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {
	
	private final Logger logger = LoggerFactory.getLogger(GenericDaoImpl.class); 

	@PersistenceContext(unitName = "pu")
	private EntityManager entityManager;

	protected T persist(T t) {
		entityManager.persist(t);
		return t;
	}

	@Override
	public T merge(T t) {
		logger.info("merge(T t)");
		return entityManager.merge(t);
	}

	@Override
	public T findById(int id) {
		logger.info("findById(int id)");
		return (T) entityManager.find(getEntityClass(), id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Class<T> getEntityClass() {

		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class) parameterizedType.getActualTypeArguments()[0];

	}

	protected TypedQuery<T> createQuery(String query) {
		
		TypedQuery<T> tq = entityManager.createQuery(query, getEntityClass());
		
		tq.setHint("org.hibernate.cacheable", new Boolean("true"));
		tq.setHint("org.hibernate.cacheMode", CacheMode.NORMAL);
		
		return tq;
		
	}

	@Override
	public void delete(T t) {
		logger.info("delete(T t)");
		entityManager.remove(t);
	}

	@Override
	public void detach(T t) {
		logger.info("detach(T t)");
		entityManager.detach(t);
	}

	@Override
	public void refresh(T t) {
		logger.info("refresh(T t)");
		entityManager.refresh(t);
	}

	
}
