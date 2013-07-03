package org.cl.demo.view.converter;

import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.cl.demo.entity.Tag;

@FacesConverter(value = "cl.demo.view.affichageConverter")
public class AffichageTagsConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object o) {

		@SuppressWarnings("unchecked")
		Set<Tag> tags = (Set<Tag>) o;

		String s = "";

		for (Tag tag : tags) {
			s += tag.getNom() + " ";
		}

		return s;
	}

}
