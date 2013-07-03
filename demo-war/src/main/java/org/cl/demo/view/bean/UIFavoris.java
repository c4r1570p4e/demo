package org.cl.demo.view.bean;

import java.io.Serializable;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.cl.demo.entity.Favoris;

public class UIFavoris implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Favoris favoris;

	private boolean proprietaire = false;

	public UIFavoris(Favoris favoris) {
		this.favoris = favoris;
		proprietaire = favoris.getProprietaire().equals(getListeFavorisBean().getUtilisateur());
	}

	private ListeFavorisBean getListeFavorisBean() {
		Application application = FacesContext.getCurrentInstance().getApplication();
		return application.evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{listeFavorisBean}", ListeFavorisBean.class);
	}

	public String modifier() {
		return getListeFavorisBean().modifier(favoris);
	}

	public String supprimer() {
		return getListeFavorisBean().supprimer(favoris);
	}

	public Favoris getFavoris() {
		return favoris;
	}

	public void setFavoris(Favoris favoris) {
		this.favoris = favoris;
	}

	public boolean isProprietaire() {
		return proprietaire;
	}

	public void setProprietaire(boolean proprietaire) {
		this.proprietaire = proprietaire;
	}

}
