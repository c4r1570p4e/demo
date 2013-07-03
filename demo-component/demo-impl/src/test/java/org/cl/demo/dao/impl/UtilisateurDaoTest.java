package org.cl.demo.dao.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Utilisateur;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class UtilisateurDaoTest {

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Test
	@Transactional
	public void createUser() {
		Utilisateur utilisateur = createTestUser("user1", "mdp1");

		Utilisateur utilisateur2 = utilisateurDao.findById(utilisateur.getId());
		assertEquals(utilisateur, utilisateur2);

		em.clear();

		Utilisateur utilisateur3 = utilisateurDao.findById(utilisateur.getId());
		assertEquals(utilisateur.getId(), utilisateur3.getId());
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createEmptyUser1() {
		createTestUser(null, "mdp1");

	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createEmptyUser2() {
		createTestUser("user1", null);

	}

	@Test(expected = PersistenceException.class)
	@Transactional
	public void createAlreadyExistingUser() {
		createTestUser("user1", "mdp1");

		em.flush();
		em.clear();

		createTestUser("user1", "mdp2");

	}

	@Test
	@Transactional
	public void findUser() {
		createUtilisateurs();

		assertNotNull(utilisateurDao.findUserByLogin("user1"));
		assertNotNull(utilisateurDao.findUserByLogin("user2"));
		assertNotNull(utilisateurDao.findUserByLogin("user3"));
		assertNotNull(utilisateurDao.findUserByLogin("user4"));
		assertNull(utilisateurDao.findUserByLogin("user5"));

	}

	@Test
	@Transactional
	public void findAllUser() {
		createUtilisateurs();

		List<Utilisateur> utilisateurs = utilisateurDao.findAllUsers();
		assertEquals(utilisateurs.size(), 4);

	}

	private void createUtilisateurs() {
		Utilisateur utilisateur1 = createTestUser("user1", "mdp1");

		Utilisateur utilisateur2 = createTestUser("user2", "mdp2");

		Utilisateur utilisateur3 = createTestUser("user3", "mdp3");

		Utilisateur utilisateur4 = createTestUser("user4", "mdp4");
	}

	private Utilisateur createTestUser(String login, String mdp) {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin(login);
		utilisateur.setMotDePasse(mdp);

		utilisateur = utilisateurDao.createUtilisateur(utilisateur);

		em.flush();
		return utilisateur;
	}

}
