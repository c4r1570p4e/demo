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
@Qualifier(value = "utilisateurBean")
public class UtilisateurBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Veuillez saisir un utilisateur")
	private String user;

	@NotNull(message = "Veuillez saisir un mot de passe")
	private String password;

	@Autowired
	private UtilisateurService utilisateurService;

	@Autowired
	private ListeFavorisBean listeFavorisBean;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String authentifier() {

		try {
			Utilisateur utilisateur = utilisateurService.authentifier(user, password);

			listeFavorisBean.setUtilisateur(utilisateur);

			listeFavorisBean.initFlag();

			listeFavorisBean.refreshList();

			return "success";

		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			listeFavorisBean.initFlag();

			listeFavorisBean.setUtilisateur(null);

			return "failure";
		}
	}

	public ListeFavorisBean getListeFavorisBean() {
		return listeFavorisBean;
	}

	public void setListeFavorisBean(ListeFavorisBean listeFavorisBean) {
		this.listeFavorisBean = listeFavorisBean;
	}

}
