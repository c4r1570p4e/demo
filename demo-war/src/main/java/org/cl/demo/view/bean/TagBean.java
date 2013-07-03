package org.cl.demo.view.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
@Qualifier(value = "tagBean")
public class TagBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private TagService tagService;

	@NotNull(message = "Le tag doit avoir un nom")
	private String nom;

	public String creerTag() {
		Tag tag = new Tag();
		tag.setNom(nom);

		try {
			tagService.creerTag(tag);
		} catch (MetierException e) {
			FacesMessage facesMessage = new FacesMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);

			return "failure";

		}

		return "success";
	}

//	public TagService getTagService() {
//		return tagService;
//	}
//
//	public void setTagService(TagService tagService) {
//		this.tagService = tagService;
//	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

}
