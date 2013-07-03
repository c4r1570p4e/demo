package org.cl.demo.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.cl.demo.dao.FavorisDao;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = { MetierException.class })
public class FavorisDaoImpl extends GenericDaoImpl<Favoris> implements FavorisDao {
	
	private final Logger logger = LoggerFactory.getLogger(FavorisDaoImpl.class); 

	@Override
	public Favoris create(Favoris favoris) {
		logger.info("create(Favoris favoris)");
		return persist(favoris);
	}

	@Override
	public List<Favoris> listPublicFavoris() {
		
		logger.info("listPublicFavoris()");
		
		String req = "select f from Favoris f where f.type = :type ORDER BY f.nom ASC";

		TypedQuery<Favoris> tq = createQuery(req);
		
		tq.setParameter("type", FavorisType.PUBLIC);

		return tq.getResultList();
	}

	@Override
	public List<Favoris> listFavoris(Utilisateur utilisateur) throws MetierException {

		logger.info("listFavoris(Utilisateur utilisateur)");
		
		if (utilisateur == null) {
			throw new MetierException("utilisateur est null");
		}

		String req = "select f from Favoris f where f.proprietaire = :proprio or f.type = :type ORDER BY f.nom ASC";

		TypedQuery<Favoris> tq = createQuery(req);
		
		tq.setParameter("proprio", utilisateur);
		tq.setParameter("type", FavorisType.PUBLIC);
		
		return tq.getResultList();
	}

	@Override
	public List<Favoris> listFavorisBy(Utilisateur utilisateur, String nom, Tag tag) throws MetierException {

		logger.info("listFavorisBy(Utilisateur utilisateur, String nom, Tag tag)");
		
		if (nom == null && tag == null) {
			throw new MetierException("Il faut au moins un critère de recherche");
		}

		String req = "select distinct f from Favoris f where ";

		if (utilisateur != null) {
			req += " ( f.proprietaire = :proprio or f.type = :type ) ";
		} else {
			req += " f.type = :type ";
		}

		if (nom != null) {
			req += " AND ";
			req += " f.nom = :nom ";
		}

		if (tag != null) {
			req += " AND ";
			req += " :tag MEMBER OF f.tags ";
		}

		req += "  ORDER BY f.nom ASC";

		TypedQuery<Favoris> tq = createQuery(req);

		tq.setParameter("type", FavorisType.PUBLIC);

		if (utilisateur != null) {
			tq.setParameter("proprio", utilisateur);
		}

		if (nom != null) {
			tq.setParameter("nom", nom);
		}

		if (tag != null) {
			tq.setParameter("tag", tag);
		}
		
		return tq.getResultList();

	}

	@Override
	public Favoris findOwnedFavoris(Utilisateur utilisateur, String nom) throws MetierException {
		
		logger.info("findOwnedFavoris(Utilisateur utilisateur, String nom)");
		
		if (utilisateur == null) {
			throw new MetierException("utilisateur est null");
		}

		if (nom == null) {
			throw new MetierException("nom est null");
		}

		String req = "select f from Favoris f where f.proprietaire = :proprio and f.nom = :nom ";

		TypedQuery<Favoris> tq = createQuery(req);
		
		tq.setParameter("proprio", utilisateur);
		tq.setParameter("nom", nom);

		try {
			return tq.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
