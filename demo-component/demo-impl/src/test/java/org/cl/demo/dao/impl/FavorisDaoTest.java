package org.cl.demo.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.cl.demo.dao.FavorisDao;
import org.cl.demo.dao.TagDao;
import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class FavorisDaoTest {

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Autowired
	private FavorisDao favorisDao;

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Autowired
	private TagDao tagDao;

	private void createUsers() {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin("user1");
		utilisateur.setMotDePasse("mdp1");
		utilisateur.setDateNaissance(new Date());
		utilisateurDao.createUtilisateur(utilisateur);

		utilisateur = new Utilisateur();
		utilisateur.setLogin("user2");
		utilisateur.setMotDePasse("mdp2");
		utilisateur.setDateNaissance(new Date());
		utilisateurDao.createUtilisateur(utilisateur);

		utilisateur = new Utilisateur();
		utilisateur.setLogin("user3");
		utilisateur.setMotDePasse("mdp3");
		utilisateur.setDateNaissance(new Date());
		utilisateurDao.createUtilisateur(utilisateur);

		em.flush();
		em.clear();
	}

	private void createTags() {
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

		em.flush();
		em.clear();
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createVoidfavoris1() throws Exception {
		createUsers();

		Utilisateur utilisateur = utilisateurDao.findUserByLogin("user1");

		Favoris favoris = new Favoris();
		// utilisateur.addFavoris(favoris);
		favoris.setProprietaire(utilisateur);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url");

		favorisDao.create(favoris);

		em.flush();
		em.clear();
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createVoidfavoris2() throws Exception {

		Favoris favoris = new Favoris();
		favoris.setNom("nom");
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url");

		favorisDao.create(favoris);

		em.flush();
		em.clear();
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createVoidfavoris3() throws Exception {
		createUsers();

		Utilisateur utilisateur = utilisateurDao.findUserByLogin("user1");

		Favoris favoris = new Favoris();
		favoris.setNom("nom");
		// utilisateur.addFavoris(favoris);
		favoris.setProprietaire(utilisateur);
		favoris.setUrl("url");

		favorisDao.create(favoris);

		em.flush();
		em.clear();
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createVoidfavoris4() throws Exception {
		createUsers();

		Utilisateur utilisateur = utilisateurDao.findUserByLogin("user1");

		Favoris favoris = new Favoris();
		favoris.setNom("nom");
		// utilisateur.addFavoris(favoris);
		favoris.setProprietaire(utilisateur);
		favoris.setType(FavorisType.PRIVE);

		favorisDao.create(favoris);

		em.flush();
		em.clear();
	}

	@Test(expected = PersistenceException.class)
	@Transactional
	public void createAlreadyExistingfavoris() throws Exception {
		createUsers();

		Utilisateur utilisateur = utilisateurDao.findUserByLogin("user1");

		Favoris favoris = new Favoris();
		favoris.setNom("nom");
		// utilisateur.addFavoris(favoris);
		favoris.setProprietaire(utilisateur);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url");

		favorisDao.create(favoris);

		em.flush();

		favoris = new Favoris();
		favoris.setNom("nom");
		// utilisateur.addFavoris(favoris);
		favoris.setProprietaire(utilisateur);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url2");

		favorisDao.create(favoris);

		em.flush();

	}

	private void createTestDatas() throws Exception {
		createUsers();
		createTags();

		Utilisateur user1 = utilisateurDao.findUserByLogin("user1");
		Utilisateur user2 = utilisateurDao.findUserByLogin("user2");

		List<Tag> tags = tagDao.findAllTags();

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		// user1.addFavoris(favoris);
		favoris.setProprietaire(user1);
		favoris.setType(FavorisType.PUBLIC);
		favoris.setUrl("url1");
		favoris.addTag(tags.get(0));
		favoris.addTag(tags.get(1));

		favorisDao.create(favoris);

		favoris = new Favoris();
		favoris.setNom("fav2");
		// user1.addFavoris(favoris);
		favoris.setProprietaire(user1);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url2");
		favoris.addTag(tags.get(2));
		favoris.addTag(tags.get(3));

		favorisDao.create(favoris);

		favoris = new Favoris();
		favoris.setNom("fav1");
		// user2.addFavoris(favoris);
		favoris.setProprietaire(user2);
		favoris.setType(FavorisType.PUBLIC);
		favoris.setUrl("url3");
		favoris.addTag(tags.get(2));
		favoris.addTag(tags.get(3));

		favorisDao.create(favoris);

		favoris = new Favoris();
		favoris.setNom("fav2");
		// user2.addFavoris(favoris);
		favoris.setProprietaire(user2);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url4");
		favoris.addTag(tags.get(0));
		favoris.addTag(tags.get(3));

		favorisDao.create(favoris);

		favoris = new Favoris();
		favoris.setNom("fav3");
		// user2.addFavoris(favoris);
		favoris.setProprietaire(user2);
		favoris.setType(FavorisType.PRIVE);
		favoris.setUrl("url5");
		favoris.addTag(tags.get(0));
		favoris.addTag(tags.get(3));

		favorisDao.create(favoris);

		em.flush();
		em.clear();

	}

	@Test
	@Transactional
	public void listUserFavs() throws Exception {
		createTestDatas();

		Utilisateur utilisateur1 = utilisateurDao.findUserByLogin("user1");
		Utilisateur utilisateur2 = utilisateurDao.findUserByLogin("user2");
		Utilisateur utilisateur3 = utilisateurDao.findUserByLogin("user3");

		List<Favoris> favoris = favorisDao.listFavoris(utilisateur1);
		// fav de user1 + 1 public
		assertEquals(3, favoris.size());

		favoris = favorisDao.listFavoris(utilisateur2);
		// fav de user2 + 1 public
		assertEquals(4, favoris.size());

		favoris = favorisDao.listFavoris(utilisateur3);
		// fav : 2 public
		assertEquals(2, favoris.size());

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void listNullUserFavs() throws Exception {
		createTestDatas();

		List<Favoris> favoris = favorisDao.listFavoris(null);

	}

	@Test
	@Transactional
	public void listPublicFavs() throws Exception {
		createTestDatas();

		List<Favoris> favoris = favorisDao.listPublicFavoris();
		assertEquals(2, favoris.size());

	}

	@Test
	@Transactional
	public void listEmptyPublicFavs() {

		List<Favoris> favoris = favorisDao.listPublicFavoris();
		assertEquals(0, favoris.size());

	}

	@Test
	@Transactional
	public void listFavsBy() throws Exception {
		createTestDatas();

		Utilisateur utilisateur1 = utilisateurDao.findUserByLogin("user1");
		Utilisateur utilisateur2 = utilisateurDao.findUserByLogin("user2");
		Utilisateur utilisateur3 = utilisateurDao.findUserByLogin("user3");

		Tag tag1 = tagDao.findTagByName("tag1");
		Tag tag2 = tagDao.findTagByName("tag2");
		Tag tag3 = tagDao.findTagByName("tag3");
		Tag tag4 = tagDao.findTagByName("tag4");
		Tag tag5 = tagDao.findTagByName("tag5");

		List<Favoris> favoris = favorisDao.listFavorisBy(utilisateur1, "fav1", null);
		assertEquals(2, favoris.size());
		assertEquals("fav1", favoris.get(0).getNom());
		assertEquals("fav1", favoris.get(1).getNom());
		assertNotSame(favoris.get(0), favoris.get(1));

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav2", null);
		assertEquals(1, favoris.size());
		assertEquals("fav2", favoris.get(0).getNom());
		assertEquals(utilisateur1, favoris.get(0).getProprietaire());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav3", null);
		assertEquals(0, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav4", null);
		assertEquals(0, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(null, "fav1", null);
		assertEquals(2, favoris.size());
		assertEquals("fav1", favoris.get(0).getNom());
		assertEquals("fav1", favoris.get(1).getNom());
		assertNotSame(favoris.get(0), favoris.get(1));

		favoris = favorisDao.listFavorisBy(null, "fav2", null);
		assertEquals(0, favoris.size());

		favoris = favorisDao.listFavorisBy(null, "fav3", null);
		assertEquals(0, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(utilisateur1, null, tag1);
		assertEquals(1, favoris.size());
		assertEquals("fav1", favoris.get(0).getNom());
		assertEquals(utilisateur1, favoris.get(0).getProprietaire());

		favoris = favorisDao.listFavorisBy(utilisateur2, null, tag2);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur2, null, tag4);
		assertEquals(3, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(null, null, tag1);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, null, tag2);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, null, tag3);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, null, tag4);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, null, tag5);
		assertEquals(0, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(utilisateur3, "fav4", null);
		assertEquals(0, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur3, null, tag5);
		assertEquals(0, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur3, "fav4", tag5);
		assertEquals(0, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav1", tag1);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav1", tag2);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav2", tag2);
		assertEquals(0, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav1", tag3);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(utilisateur1, "fav1", tag4);
		assertEquals(1, favoris.size());

		//

		favoris = favorisDao.listFavorisBy(null, "fav1", tag1);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, "fav1", tag2);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, "fav1", tag3);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, "fav1", tag4);
		assertEquals(1, favoris.size());

		favoris = favorisDao.listFavorisBy(null, "fav2", tag1);
		assertEquals(0, favoris.size());

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void listFavsByNullParams1() throws Exception {
		createTestDatas();

		Utilisateur utilisateur1 = utilisateurDao.findUserByLogin("user1");

		List<Favoris> favoris = favorisDao.listFavorisBy(utilisateur1, null, null);
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void listFavsByNullParams2() throws Exception {
		createTestDatas();

		Utilisateur utilisateur1 = utilisateurDao.findUserByLogin("user1");

		List<Favoris> favoris = favorisDao.listFavorisBy(null, null, null);
	}

}
