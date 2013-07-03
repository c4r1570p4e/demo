package org.cl.demo.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.cl.demo.dao.ReattachHelper;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session")
@Qualifier(value = "modifierFavorisBean")
public class ModifierFavorisBean implements Serializable {

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

	private Favoris favoris;

	@NotNull(message = "L'url doit être renseigné")
	private String url;

	@NotNull(message = "le type de favoris doit être choisi")
	private FavorisType favorisType;

	private List<Tag> favorisTags;

	public String modifierFavoris() {

		favoris = reattachHelper.reattach(favoris);

		favoris.clearTag();

		for (Tag tag : favorisTags) {
			favoris.addTag(reattachHelper.reattach(tag));
		}

		try {
			Utilisateur utilisateur = reattachHelper.reattach(listeFavorisBean.getUtilisateur());

			favoris = favorisService.modifierFavoris(utilisateur, favoris, url, favorisType);

			listeFavorisBean.refreshList();
		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			return "failure";
		}

		clear();

		return "success";

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FavorisType getFavorisType() {
		return favorisType;
	}

	public void setFavorisType(FavorisType favorisType) {
		this.favorisType = favorisType;
	}

	public Favoris getFavoris() {
		return favoris;
	}

	public void setFavoris(Favoris favoris) {
		this.favoris = favoris;
		this.favorisType = favoris.getType();
		this.url = favoris.getUrl();

		this.favorisTags = new ArrayList<Tag>();
		if (favoris != null) {
			favorisTags.addAll(favoris.getTags());
		}
	}

	public List<Tag> getFavorisTags() {
		return favorisTags;
	}

	public void setFavorisTags(List<Tag> favorisTags) {
		this.favorisTags = favorisTags;
	}

	public ListeFavorisBean getListeFavorisBean() {
		return listeFavorisBean;
	}

	public void setListeFavorisBean(ListeFavorisBean listeFavorisBean) {
		this.listeFavorisBean = listeFavorisBean;
	}

	private void clear() {
		favoris = null;
		url = null;
		favorisType = null;
		favorisTags = null;
	}

}
