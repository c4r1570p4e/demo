package org.cl.demo.dao.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Utilisateur;
import org.junit.Before;
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

	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	private static final String USER3 = "user3";
	private static final String USER4 = "user4";
	private static final String USER5 = "user5";

	private static final String MDP1 = "mdp1";
	private static final String MDP2 = "mdp2";
	private static final String MDP3 = "mdp3";
	private static final String MDP4 = "mdp4";

	private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;

	@Before
	public void init() throws Exception {
		date1 = DF.parse("15/02/1978");
		date2 = DF.parse("31/03/1985");
		date3 = DF.parse("12/11/1935");
		date4 = DF.parse("07/06/1965");

	}

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Test
	@Transactional
	public void createUser() {
		Utilisateur utilisateur = createTestUser(USER1, MDP1, date1);

		Utilisateur utilisateur2 = utilisateurDao.findById(utilisateur.getId());
		assertEquals(utilisateur, utilisateur2);

		em.clear();

		Utilisateur utilisateur3 = utilisateurDao.findById(utilisateur.getId());
		assertEquals(utilisateur.getId(), utilisateur3.getId());
	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createEmptyUser1() {
		createTestUser(null, MDP1, date1);

	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createEmptyUser2() {
		createTestUser(USER1, null, date1);

	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void createEmptyUser3() {
		createTestUser(USER1, MDP1, null);

	}
	
	@Test(expected = PersistenceException.class)
	@Transactional
	public void createAlreadyExistingUser() {
		createTestUser(USER1, MDP1, date1);

		em.flush();
		em.clear();

		createTestUser(USER1, MDP2, date2);

	}

	@Test
	@Transactional
	public void findUser() {
		createUtilisateurs();

		assertNotNull(utilisateurDao.findUserByLogin(USER1));
		assertNotNull(utilisateurDao.findUserByLogin(USER2));
		assertNotNull(utilisateurDao.findUserByLogin(USER3));
		assertNotNull(utilisateurDao.findUserByLogin(USER4));
		assertNull(utilisateurDao.findUserByLogin(USER5));

	}

	@Test
	@Transactional
	public void findAllUser() {
		createUtilisateurs();

		List<Utilisateur> utilisateurs = utilisateurDao.findAllUsers();
		assertEquals(utilisateurs.size(), 4);

	}

	private void createUtilisateurs() {
		createTestUser(USER1, MDP1, date1);
		createTestUser(USER2, MDP2, date2);
		createTestUser(USER3, MDP3, date3);
		createTestUser(USER4, MDP4, date4);
	}

	private Utilisateur createTestUser(String login, String mdp, Date dateNaissance) {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin(login);
		utilisateur.setMotDePasse(mdp);
		utilisateur.setDateNaissance(dateNaissance);

		utilisateur = utilisateurDao.createUtilisateur(utilisateur);

		em.flush();
		return utilisateur;
	}

}
