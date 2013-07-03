package org.cl.demo.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.TagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class TagServiceTest {

	@Autowired
	private TagService tagService;

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Test(expected = MetierException.class)
	@Transactional
	public void testCreerTagNull() throws MetierException {
		tagService.creerTag(null);
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void testCreerTag2Fois() throws MetierException {

		Tag tag = new Tag();
		tag.setNom("nom1");

		tagService.creerTag(tag);

		em.flush();
		em.clear();

		tag = new Tag();
		tag.setNom("nom1");

		tagService.creerTag(tag);

		em.flush();
		em.clear();

	}

	public void testObtenirtags() throws MetierException {

		List<Tag> tags = tagService.obtenirTags();

		assertEquals(0, tags.size());

		Tag tag = new Tag();
		tag.setNom("tag1");
		tagService.creerTag(tag);

		tag = new Tag();
		tag.setNom("tag2");
		tagService.creerTag(tag);

		tag = new Tag();
		tag.setNom("tag3");
		tagService.creerTag(tag);

		tag = new Tag();
		tag.setNom("tag4");
		tagService.creerTag(tag);

		em.flush();
		em.clear();

		tags = tagService.obtenirTags();

		assertEquals(4, tags.size());

	}

}
