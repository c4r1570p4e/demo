package org.cl.demo.service;

import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;

public interface UtilisateurService {

	Utilisateur authentifier(String user, String password) throws MetierException;

	Utilisateur obtenirUtilisateurById(int userId) throws MetierException;

	Utilisateur creerUtilisateur(Utilisateur utilisateur) throws MetierException;

}
