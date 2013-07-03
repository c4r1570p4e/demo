package org.cl.demo.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cl.demo.dao.ReattachHelper;
import org.cl.demo.exceptions.MetierException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
@Transactional(rollbackFor = { MetierException.class })
public class ReattachHelperImpl implements ReattachHelper {
	
	@PersistenceContext(unitName = "pu")
	private EntityManager entityManager;	

	@Override
	public boolean contains(Object entity) {
		return entityManager.contains(entity);
	}

	@Override
	public <T> T reattach(T t) {
		return entityManager.merge(t);
	}

}
