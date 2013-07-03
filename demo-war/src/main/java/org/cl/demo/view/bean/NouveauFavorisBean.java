package org.cl.demo.view.bean;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.cl.demo.dao.ReattachHelper;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
@Qualifier(value = "nouveauFavorisBean")
public class NouveauFavorisBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private ListeFavorisBean listeFavorisBean;

	@Autowired
	private FavorisService favorisService;
	
	@Autowired
	private ReattachHelper reattachHelper;	

	private Favoris favoris = new Favoris();

	public String creerFavoris() {

		try {
			Utilisateur utilisateur = reattachHelper.reattach(listeFavorisBean.getUtilisateur());

			favoris = favorisService.creerFavoris(utilisateur, favoris);
			listeFavorisBean.refreshList();
		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			return "failure";
		}

		return "success";

	}

	public ListeFavorisBean getListeFavorisBean() {
		return listeFavorisBean;
	}

	public void setListeFavorisBean(ListeFavorisBean listeFavorisBean) {
		this.listeFavorisBean = listeFavorisBean;
	}

	public Favoris getFavoris() {
		return favoris;
	}

	public void setFavoris(Favoris favoris) {
		this.favoris = favoris;
	}

}
