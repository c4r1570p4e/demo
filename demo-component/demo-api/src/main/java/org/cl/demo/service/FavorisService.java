package org.cl.demo.service;

import java.util.List;

import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;

public interface FavorisService {

	Favoris creerFavoris(Utilisateur utilisateur, Favoris favoris) throws MetierException;

	void supprimerFavoris(Utilisateur utilisateur, Favoris favoris) throws MetierException;

	Favoris modifierFavoris(Utilisateur utilisateur, Favoris favoris, String newUrl, FavorisType favorisType) throws MetierException;

	Favoris ajouterTag(Utilisateur utilisateur, Favoris favoris, Tag tag) throws MetierException;

	List<Favoris> listerFavoris(Utilisateur utilisateur);

	Favoris supprimerTag(Utilisateur utilisateur, Favoris favoris, Tag tag) throws MetierException;

	List<Favoris> listerFavorisBy(Utilisateur utilisateur, String nom, Tag tag) throws MetierException;

}
