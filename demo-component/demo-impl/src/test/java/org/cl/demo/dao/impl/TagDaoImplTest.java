package org.cl.demo.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.cl.demo.dao.TagDao;
import org.cl.demo.entity.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class TagDaoImplTest {

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Autowired
	private TagDao tagDao;

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createVoidTag() {
		Tag tag = new Tag();

		tagDao.create(tag);

		em.flush();

	}

	@Test
	@Transactional
	public void createTag() {
		Tag tag = new Tag();
		tag.setNom("tag1");

		tag = tagDao.create(tag);

		em.flush();
		em.clear();
		
		em.getEntityManagerFactory().getCache().evictAll();
		

		Tag tag2 = tagDao.findById(tag.getId());

		assertEquals(tag.getId(), tag2.getId());

	}

	@Test(expected = PersistenceException.class)
	@Transactional
	public void createAlreadyExistingTag() {
		Tag tag = new Tag();
		tag.setNom("tag1");

		tagDao.create(tag);

		em.flush();
		em.clear();

		Tag tag2 = new Tag();
		tag2.setNom("tag1");

		tagDao.create(tag2);

		em.flush();

	}

	@Test
	@Transactional
	public void listTag() {
		List<Tag> tags = tagDao.findAllTags();

		assertNotNull(tags);
		assertEquals(tags.size(), 0);

		Tag tag = new Tag();
		tag.setNom("tag1");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag2");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag3");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag4");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag5");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag6");
		tagDao.create(tag);

		tag = new Tag();
		tag.setNom("tag7");
		tagDao.create(tag);

		tags = tagDao.findAllTags();

		assertNotNull(tags);
		assertEquals(tags.size(), 7);

	}

}
