package org.cl.demo.view.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
@Qualifier(value = "nouveauUtilisateurBean")
public class NouveauUtilisateurBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Utilisateur utilisateur = new Utilisateur();
	
	@NotNull(message = "Veuillez saisir le mot passe de confirmation")
	private String passwordConfirmation;

	@Autowired
	private ListeFavorisBean listeFavorisBean;

	@Autowired
	private UtilisateurService utilisateurService;

	public String creer() {

		if (!utilisateur.getMotDePasse().equals(passwordConfirmation)) {
			FacesMessage facesMessage = new FacesMessage("Password et password(confirmation) ne sont pas identiques");
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			listeFavorisBean.setUtilisateur(null);

			listeFavorisBean.initFlag();

			return "failure";
		}

		try {

			utilisateur = utilisateurService.creerUtilisateur(utilisateur);

			listeFavorisBean.setUtilisateur(utilisateur);

			listeFavorisBean.initFlag();

			listeFavorisBean.refreshList();

			return "success";

		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			listeFavorisBean.setUtilisateur(null);

			listeFavorisBean.initFlag();

			return "failure";
		}
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public ListeFavorisBean getListeFavorisBean() {
		return listeFavorisBean;
	}

	public void setListeFavorisBean(ListeFavorisBean listeFavorisBean) {
		this.listeFavorisBean = listeFavorisBean;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

}
