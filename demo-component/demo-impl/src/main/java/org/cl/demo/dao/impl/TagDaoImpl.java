package org.cl.demo.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.cl.demo.dao.TagDao;
import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = { MetierException.class })
public class TagDaoImpl extends GenericDaoImpl<Tag> implements TagDao {

	private final Logger logger = LoggerFactory.getLogger(TagDaoImpl.class);

	@Override
	public List<Tag> findAllTags() {

		logger.info("findAllTags()");

		String query = "select t from Tag t order by t.nom";
		TypedQuery<Tag> tq = createQuery(query);

		return tq.getResultList();
	}

	@Override
	public Tag create(Tag tag) {

		logger.info("create(Tag tag)");

		super.persist(tag);

		return tag;
	}

	@Override
	public Tag findTagByName(String name) throws MetierException {

		logger.info("findTagByName(String name)");

		if (name == null) {
			throw new MetierException("name ne doit pas être null");
		}

		TypedQuery<Tag> tq = createQuery("select t from Tag t where t.nom = :name");

		tq.setParameter("name", name);
		try {
			return tq.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
