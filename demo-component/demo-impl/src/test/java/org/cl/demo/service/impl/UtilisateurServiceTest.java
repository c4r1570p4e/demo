package org.cl.demo.service.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.UtilisateurService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class UtilisateurServiceTest {

	@Autowired
	private UtilisateurService utilisateurService;

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Test(expected = MetierException.class)
	@Transactional
	public void createNullUser() throws MetierException {
		utilisateurService.creerUtilisateur(null);
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void createAllReadyExistingUser() throws MetierException {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin("user1");
		utilisateur.setMotDePasse("passe");
		utilisateur.setDateNaissance(new Date());
		utilisateurService.creerUtilisateur(utilisateur);

		Utilisateur utilisateur2 = new Utilisateur();
		utilisateur2.setLogin("user1");
		utilisateur2.setMotDePasse("passe");
		utilisateur2.setDateNaissance(new Date());
		utilisateurService.creerUtilisateur(utilisateur2);
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void authentNull1() throws MetierException {

		utilisateurService.authentifier(null, "toto");
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void authentNull2() throws MetierException {

		utilisateurService.authentifier("toto", null);
	}

	private void createUsers() throws MetierException {

		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin("user1");
		utilisateur.setMotDePasse("mdp1");
		utilisateur.setDateNaissance(new Date());
		utilisateurService.creerUtilisateur(utilisateur);

		utilisateur = new Utilisateur();
		utilisateur.setLogin("user2");
		utilisateur.setMotDePasse("mdp2");
		utilisateur.setDateNaissance(new Date());		
		utilisateurService.creerUtilisateur(utilisateur);

		em.flush();
		em.clear();
	}

	@Test
	@Transactional
	public void authentOk() throws MetierException {
		this.createUsers();

		Utilisateur utilisateur = utilisateurService.authentifier("user1", "mdp1");

		assertNotNull(utilisateur);
		assertEquals("user1", utilisateur.getLogin());
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void authentKo1() throws MetierException {
		this.createUsers();

		utilisateurService.authentifier("user1", "mdp2");

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void authentKo2() throws MetierException {
		this.createUsers();

		utilisateurService.authentifier("user3", "mdp2");

	}

	@Test
	@Transactional
	public void obtenirById() throws MetierException {
		this.createUsers();

		Utilisateur utilisateur = utilisateurService.authentifier("user1", "mdp1");

		em.flush();
		em.clear();

		Utilisateur utilisateur2 = utilisateurService.obtenirUtilisateurById(utilisateur.getId());

		assertEquals(utilisateur.getLogin(), utilisateur2.getLogin());

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void obtenirByIdKo() throws MetierException {
		this.createUsers();

		utilisateurService.obtenirUtilisateurById(22);

	}

}
