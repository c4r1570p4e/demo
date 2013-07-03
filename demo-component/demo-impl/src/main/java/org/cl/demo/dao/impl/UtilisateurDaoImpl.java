package org.cl.demo.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UtilisateurDaoImpl extends GenericDaoImpl<Utilisateur> implements UtilisateurDao {
	
	private final Logger logger = LoggerFactory.getLogger(FavorisDaoImpl.class);

	@Override
	public Utilisateur createUtilisateur(Utilisateur utilisateur) {
		
		logger.info("createUtilisateur(Utilisateur utilisateur)");
		
		return persist(utilisateur);
	}

	@Override
	public Utilisateur findUserByLogin(String login) {
		
		logger.info("findUserByLogin(String login)");
		
		String query = "select u from Utilisateur u where u.login = :login";
		TypedQuery<Utilisateur> tq = createQuery(query);
		
		tq.setParameter("login", login);

		Utilisateur utilisateur;
		try {
			utilisateur = tq.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

		return utilisateur;

	}

	@Override
	public List<Utilisateur> findAllUsers() {
		
		logger.info("findAllUsers()");
		
		String query = "select u from Utilisateur u order by u.login";
		TypedQuery<Utilisateur> tq = createQuery(query);
		
		return tq.getResultList();

	}

}
