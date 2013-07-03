package org.cl.demo.service.impl;

import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
@Transactional(rollbackFor = { MetierException.class })
public class UtilisateurServiceImpl implements UtilisateurService {

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Override
	public Utilisateur authentifier(String user, String password) throws MetierException {

		if (user == null) {
			throw new MetierException("nom d'utilisateur absent");
		}

		if (password == null) {
			throw new MetierException("password absent");
		}

		Utilisateur utilisateur = utilisateurDao.findUserByLogin(user);

		if (utilisateur == null) {
			throw new MetierException("Utilisateur inconnu");
		}

		if (!utilisateur.getMotDePasse().equals(password)) {
			throw new MetierException("Mauvais mot de passe");
		}

		return utilisateur;
	}

	@Override
	@Transactional
	public Utilisateur obtenirUtilisateurById(int userId) throws MetierException {
		Utilisateur utilisateur = utilisateurDao.findById(userId);

		if (utilisateur == null) {
			throw new MetierException("Utilisateur id inconnu");
		}

		return utilisateur;
	}

	@Override
	public Utilisateur creerUtilisateur(Utilisateur utilisateur) throws MetierException {
		if (utilisateur == null) {
			throw new MetierException("Utilisateur null");
		}

		if (utilisateurDao.findUserByLogin(utilisateur.getLogin()) != null) {
			throw new MetierException("Cet utilisateur existe déjà");
		}

		return utilisateurDao.createUtilisateur(utilisateur);
	}

	public UtilisateurDao getUtilisateurDao() {
		return utilisateurDao;
	}

	public void setUtilisateurDao(UtilisateurDao utilisateurDao) {
		this.utilisateurDao = utilisateurDao;
	}

}
