package org.cl.demo.dao;

import java.util.List;

import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;

public interface FavorisDao extends GenericDao<Favoris> {

	List<Favoris> listFavorisBy(Utilisateur utilisateur, String nom, Tag tag) throws MetierException;

	Favoris create(Favoris favoris);

	List<Favoris> listPublicFavoris();

	List<Favoris> listFavoris(Utilisateur utilisateur) throws MetierException;
	
	Favoris findOwnedFavoris(Utilisateur utilisateur, String nom) throws MetierException;

}
