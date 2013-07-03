package org.cl.demo.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;

import org.cl.demo.entity.Tag;
import org.cl.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
@Qualifier(value = "listeTagBean")
public class ListeTagBean implements Serializable {

	private List<SelectItem> tags = null;

	@Autowired
	private TagService tagService;

	@PostConstruct
	private void init() {
		List<Tag> listTags = tagService.obtenirTags();
		tags = new ArrayList<SelectItem>();
		for (Tag tag : listTags) {
			tags.add(new SelectItem(tag, tag.getNom()));
		}
	}

	public List<SelectItem> getTags() {
		return tags;
	}


}
