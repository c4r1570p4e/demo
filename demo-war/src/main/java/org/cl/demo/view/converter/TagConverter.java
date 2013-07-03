package org.cl.demo.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.TagService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

@FacesConverter(value = "cl.demo.view.tagconverter")
public class TagConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String s) {

		if ((s == null) || (s.trim().equals(""))) {
			return null;
		}

		if (s != null && s.startsWith("TAG:")) {
			String sId = s.substring(4);

			int id = Integer.parseInt(sId);

			ApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
			TagService tagService = ctx.getBean(TagService.class);

			try {
				return tagService.getTagById(id);
			} catch (MetierException e) {
				throw new ConverterException("Impossible de trouver le Tag id : " + id);
			}

		} else {
			throw new ConverterException("N'est pas un Tag " + s);
		}

	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object o) {

		if (o == null) {
			return null;
		}

		if (o instanceof Tag) {
			Tag tag = (Tag) o;

			return "TAG:" + tag.getId();

		} else {
			throw new ConverterException("L'objet n'est pas un Tag");
		}
	}

}
