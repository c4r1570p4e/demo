package org.cl.demo.service.impl;

import java.util.List;

import org.cl.demo.dao.FavorisDao;
import org.cl.demo.dao.UtilisateurDao;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
@Transactional(rollbackFor = { MetierException.class })
public class FavorisServiceImpl implements FavorisService {

	@Autowired
	private FavorisDao favorisDao;

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Override
	public Favoris creerFavoris(Utilisateur utilisateur, Favoris favoris) throws MetierException {

		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris.getNom() == null) {
			throw new MetierException("Le nom du favoris doit être renseigné");
		} else {

			Favoris favorisFound = favorisDao.findOwnedFavoris(utilisateur, favoris.getNom());

			if (favorisFound != null) {
				throw new MetierException("Le favoris " + favoris.getNom() + " existe déjà pour cet utilisateur ");
			}

		}

		favoris.setProprietaire(utilisateur);

		return favorisDao.create(favoris);

	}

	@Override
	public void supprimerFavoris(Utilisateur utilisateur, Favoris favoris) throws MetierException {
		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("favoris null");
		}

		if (!favoris.getProprietaire().equals(utilisateur)) {
			throw new MetierException("Ce favoris n'appartient pas à cet utilisateur");
		}

		favoris.setProprietaire(null);


		favorisDao.delete(favoris);

	}

	@Override
	public Favoris modifierFavoris(Utilisateur utilisateur, Favoris favoris, String newUrl, FavorisType favorisType) throws MetierException {

		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("favoris null");
		}

		if (!favoris.getProprietaire().equals(utilisateur)) {
			throw new MetierException("Ce favoris n'appartient pas à l'utilisateur");
		}

		favoris.setUrl(newUrl);
		favoris.setType(favorisType);

		return favoris;

	}

	@Override
	public Favoris ajouterTag(Utilisateur utilisateur, Favoris favoris, Tag tag) throws MetierException {

		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("favoris null");
		}

		if (tag == null) {
			throw new MetierException("tag null");
		}

		if (!favoris.getProprietaire().equals(utilisateur)) {
			throw new MetierException("Ce favoris n'appartient pas à l'utilisateur");
		}

		favoris.addTag(tag);

		return favoris;
	}

	@Override
	public Favoris supprimerTag(Utilisateur utilisateur, Favoris favoris, Tag tag) throws MetierException {

		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("favoris null");
		}

		if (tag == null) {
			throw new MetierException("tag null");
		}

		if (!favoris.getProprietaire().equals(utilisateur)) {
			throw new MetierException("Ce favoris n'appartient pas à l'utilisateur");
		}

		if (!favoris.getTags().contains(tag)) {
			throw new MetierException("Ce tag n'est pas présent sur ce favoris");
		}

		favoris.removeTag(tag);

		return favoris;
	}

	@Override
	public List<Favoris> listerFavoris(Utilisateur utilisateur) {

		if (utilisateur != null) {
			try {
				return favorisDao.listFavoris(utilisateur);
			} catch (MetierException e) {
				e.printStackTrace();
				// erreur innatendue
				return null;
			}
		} else {
			return favorisDao.listPublicFavoris();
		}
	}

	@Override
	public List<Favoris> listerFavorisBy(Utilisateur utilisateur, String nom, Tag tag) throws MetierException {
		
		return favorisDao.listFavorisBy(utilisateur, nom, tag);
	}

	public UtilisateurDao getUtilisateurDao() {
		return utilisateurDao;
	}

	public void setUtilisateurDao(UtilisateurDao utilisateurDao) {
		this.utilisateurDao = utilisateurDao;
	}

	public FavorisDao getFavorisDao() {
		return favorisDao;
	}

	public void setFavorisDao(FavorisDao favorisDao) {
		this.favorisDao = favorisDao;
	}

}
