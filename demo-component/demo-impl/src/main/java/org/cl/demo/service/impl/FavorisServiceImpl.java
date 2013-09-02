package org.cl.demo.service.impl;

import java.util.Date;
import java.util.List;

import org.cl.demo.client.contenu.IControlRestClient;
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

import com.google.common.base.Throwables;

@Component
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
@Transactional(rollbackFor = { MetierException.class })
public class FavorisServiceImpl implements FavorisService {

	@Autowired
	private FavorisDao favorisDao;

	@Autowired
	private UtilisateurDao utilisateurDao;

	@Autowired
	private IControlRestClient controlRestClient;

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

			if (!controlRestClient.isUrlAutorise(favoris.getUrl(), utilisateur.getDateNaissance())) {
				throw new MetierException("Cette URL est interdite pour votre age.");
			}

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
	public Favoris modifierFavoris(Utilisateur utilisateur, Favoris favoris, String newUrl, FavorisType favorisType)
			throws MetierException {

		if (utilisateur == null) {
			throw new MetierException("utilisateur null");
		}

		if (favoris == null) {
			throw new MetierException("favoris null");
		}

		if (!favoris.getProprietaire().equals(utilisateur)) {
			throw new MetierException("Ce favoris n'appartient pas à l'utilisateur");
		}

		if (!controlRestClient.isUrlAutorise(newUrl, utilisateur.getDateNaissance())) {
			throw new MetierException("Cette URL est interdite pour votre age.");
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
				return filtrerFavorisPourUtilisateur(favorisDao.listFavoris(utilisateur),utilisateur);
			} catch (MetierException e) {
				throw Throwables.propagate(e);
			}
		} else {
			return filtrerFavorisPourUtilisateur(favorisDao.listPublicFavoris(), null);
		}
	}

	@Override
	public List<Favoris> listerFavorisBy(Utilisateur utilisateur, String nom, Tag tag) throws MetierException {
		return filtrerFavorisPourUtilisateur(favorisDao.listFavorisBy(utilisateur, nom, tag), utilisateur);
	}

	private List<Favoris> filtrerFavorisPourUtilisateur(List<Favoris> favoriss, Utilisateur utilisateur) {

		Date dateNaissance = null;

		if (utilisateur != null) {
			dateNaissance = utilisateur.getDateNaissance();
		}

		return controlRestClient.filterUrlsAutorise(favoriss, dateNaissance);
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

	public IControlRestClient getControlRestClient() {
		return controlRestClient;
	}

	public void setControlRestClient(IControlRestClient controlRestClient) {
		this.controlRestClient = controlRestClient;
	}

}
