package org.cl.demo.client.contenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.cl.demo.client.contenu.domain.Url;
import org.cl.demo.client.contenu.domain.UrlsRequest;
import org.cl.demo.entity.Favoris;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;

@Data
@Slf4j
@Component
public class ControlRestClientImpl implements IControlRestClient {

	@Autowired
	private RestTemplate restTemplate;

	@Value(value = "${ctrl.contenu.server.adress}")
	private String adresseServer;

	private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormat.forPattern("dd-MM-yyyy");

	@Override
	public boolean isUrlAutorise(String url, Date dateNaissance) {

		if (url == null || url.trim().equals("")) {
			return true;
		}

		String ressource = adresseServer + "/url?url={url}&dateNaissance={dateNaissance}";

		try {
			restTemplate.postForEntity(ressource, null, Void.class, url,
					DATE_TIME_FORMATER.print(new DateTime(dateNaissance.getTime())));
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
				return false;
			} else {
				log.error("Erreur innatendue", e);
				Throwables.propagate(e);
			}
		}

		return true;
	}

	@Override
	public List<Favoris> filterUrlsAutorise(List<Favoris> favoriss, Date dateNaissance) {

		String ressource = adresseServer + "/url/multi";

		if (dateNaissance == null) {
			dateNaissance = new Date(); // pas authentifie -> age = 0 ans
		}

		UrlsRequest urlsRequest = new UrlsRequest();
		urlsRequest.setDateNaissance(dateNaissance);
		urlsRequest.setUrls(new ArrayList<Url>());

		for (Favoris favoris : favoriss) {
			urlsRequest.getUrls().add(new Url(favoris.getUrl()));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UrlsRequest> entity = new HttpEntity<UrlsRequest>(urlsRequest, headers);

		Url[] urls = restTemplate.postForObject(ressource, entity, Url[].class);

		Collection<String> listUrls = Collections2.transform(Arrays.asList(urls), new Function<Url, String>() {
			@Override
			public String apply(Url input) {
				return input.getUrl();
			}
		});

		List<Favoris> favorisFiltres = new ArrayList<>(listUrls.size());

		for (Favoris favoris : favoriss) {
			if (listUrls.contains(favoris.getUrl())) {
				favorisFiltres.add(favoris);
			}
		}

		return favorisFiltres;
	}
}
