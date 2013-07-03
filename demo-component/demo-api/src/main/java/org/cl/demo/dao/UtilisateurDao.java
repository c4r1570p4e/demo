package org.cl.demo.dao;

import java.util.List;

import org.cl.demo.entity.Utilisateur;

public interface UtilisateurDao extends GenericDao<Utilisateur> {

	Utilisateur createUtilisateur(Utilisateur utilisateur);

	Utilisateur findUserByLogin(String login);

	List<Utilisateur> findAllUsers();

}
