package org.cl.demo.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.cl.demo.entity.FavorisType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
@Qualifier(value = "listeTypesBean")
public class ListeTypesBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// list referentiel
	private static List<SelectItem> types = null;

	static {
		types = new ArrayList<SelectItem>();
		types.add(new SelectItem(FavorisType.PUBLIC, "Public"));
		types.add(new SelectItem(FavorisType.PRIVE, "Privé"));
	}

	public List<SelectItem> getTypes() {
		return types;
	}

	public void setTypes(List<SelectItem> types) {
		ListeTypesBean.types = types;
	}

}
