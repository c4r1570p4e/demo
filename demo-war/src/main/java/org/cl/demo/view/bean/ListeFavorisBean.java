package org.cl.demo.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.cl.demo.dao.ReattachHelper;
import org.cl.demo.entity.Favoris;
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
@Qualifier(value = "listeFavorisBean")
public class ListeFavorisBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private FavorisService favorisService;
	
	@Autowired
	private ReattachHelper reattachHelper;

	private List<UIFavoris> favoris;

	private Utilisateur utilisateur;

	private boolean afficherConnecter = true;

	private boolean afficherDeConnecter = false;

	private boolean afficherAjouter = false;

	private String nomRecherche;

	private Tag tagRecherche;

	public ListeFavorisBean() {
		super();
	}

	@PostConstruct
	private void postConstructInit() throws MetierException {
		init();
	}

	private List<UIFavoris> getUIFavoris(List<Favoris> listFavoris) {
		List<UIFavoris> retour = new ArrayList<UIFavoris>();

		for (Favoris favoris : listFavoris) {
			retour.add(new UIFavoris(favoris));
		}

		return retour;
	}

	private void init() throws MetierException {

		if (tagRecherche != null) {
			tagRecherche = reattachHelper.reattach(tagRecherche);
		}

		initFlag();

	}

	public void initFlag() {
		if (utilisateur == null) {
			afficherConnecter = true;
			afficherDeConnecter = false;
			afficherAjouter = false;
		} else {
			afficherConnecter = false;
			afficherDeConnecter = true;
			afficherAjouter = true;
		}
	}

	public void refreshList() throws MetierException {
		boolean filtre = false;

		if (nomRecherche != null && !nomRecherche.trim().equals("")) {
			filtre = true;
		} else {
			nomRecherche = null;
		}

		if (tagRecherche != null) {
			filtre = true;
		}

		if (utilisateur != null) {
			if (!reattachHelper.contains(utilisateur)) {
				utilisateur = reattachHelper.reattach(utilisateur);
			}
		}

		if (filtre) {
			favoris = getUIFavoris(favorisService.listerFavorisBy(utilisateur, nomRecherche, tagRecherche));
		} else {
			favoris = getUIFavoris(favorisService.listerFavoris(utilisateur));
		}
	}

	public String deconnecter() {

		utilisateur = null;

		try {
			init();

			refreshList();

		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			return "deconnecter";
		}

		return "deconnecter";
	}

	public String filtrer() {
		try {
			init();

			refreshList();
		} catch (MetierException e) {

			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			return "failure";
		}

		return "success";
	}

	public String modifier(Favoris favoris) {

		getModifierFavorisBean().setFavoris(favoris);

		return "modifier";
	}

	private ModifierFavorisBean getModifierFavorisBean() {
		Application application = FacesContext.getCurrentInstance().getApplication();
		return application.evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{modifierFavorisBean}", ModifierFavorisBean.class);
	}

	public String supprimer(Favoris favoris) {

		try {
			utilisateur = reattachHelper.reattach(utilisateur);
			Favoris favoris2 = reattachHelper.reattach(favoris);
			favorisService.supprimerFavoris(utilisateur, favoris2);
			init();
			refreshList();
		} catch (MetierException e) {
			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			return "failure";
		}

		return "supprimer";
	}

	public boolean isAfficherConnecter() {
		return afficherConnecter;
	}

	public void setAfficherConnecter(boolean afficherConnecter) {
		this.afficherConnecter = afficherConnecter;
	}

	public boolean isAfficherDeConnecter() {
		return afficherDeConnecter;
	}

	public void setAfficherDeConnecter(boolean afficherDeConnecter) {
		this.afficherDeConnecter = afficherDeConnecter;
	}

	public boolean isAfficherAjouter() {
		return afficherAjouter;
	}

	public void setAfficherAjouter(boolean afficherAjouter) {
		this.afficherAjouter = afficherAjouter;
	}

	public List<UIFavoris> getFavoris() {
		if (favoris == null) {// cas du 1er affichage la liste est pas
								// initialise
			try {
				refreshList();
			} catch (MetierException e) {
				FacesMessage facesMessage = new FacesMessage(e.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			}
		}
		return favoris;
	}

	public String getNomRecherche() {
		return nomRecherche;
	}

	public void setNomRecherche(String nomRecherche) {
		this.nomRecherche = nomRecherche;
	}

	public Tag getTagRecherche() {
		return tagRecherche;
	}

	public void setTagRecherche(Tag tagRecherche) {
		this.tagRecherche = tagRecherche;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

}
