package org.cl.demo.client.contenu;

import java.util.Date;
import java.util.List;

import org.cl.demo.entity.Favoris;

public interface IControlRestClient {

	boolean isUrlAutorise(String url, Date dateNaissance);
	
	List<Favoris> filterUrlsAutorise(List<Favoris> favoriss, Date dateNaissance);
	
}
