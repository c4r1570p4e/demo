package org.cl.demo.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import org.cl.demo.client.contenu.ControlRestClientImpl;
import org.cl.demo.entity.Favoris;
import org.cl.demo.entity.FavorisType;
import org.cl.demo.entity.Tag;
import org.cl.demo.entity.Utilisateur;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.FavorisService;
import org.cl.demo.service.TagService;
import org.cl.demo.service.UtilisateurService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:context_test.xml" })
@TransactionConfiguration(transactionManager = "myTxManager", defaultRollback = true)
@Transactional
public class FavorisServiceTest {

	@Autowired
	private FavorisService favorisService;

	@Autowired
	private UtilisateurService utilisateurService;

	@Autowired
	private TagService tagService;

	@PersistenceContext(unitName = "pu")
	private EntityManager em;

	@Autowired
	private ControlRestClientImpl controlRestClientImpl;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer mockServer;

	@Autowired
	private ApplicationContext applicationContext;

	private static final DateTimeFormatter DATE_TIMER_FORMATTER = DateTimeFormat.forPattern("dd-MM-yyyy");

	private static final Date DATE_NAISSANCE_USER1 = DATE_TIMER_FORMATTER.parseDateTime("12-05-1978").toDate();
	private static final String URL1 = "url1";
	private static final String URL2 = "url2";
	private static final String NEW_URL = "newUrl";

	private static final String SERVER_ADRESS = "http://localhost:8080/ctrl-contenu-war";

	@Before
	public void init() {
		controlRestClientImpl.setAdresseServer(SERVER_ADRESS);
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	private Utilisateur createUser() throws MetierException {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin("user1");
		utilisateur.setMotDePasse("mdp1");

		utilisateur.setDateNaissance(DATE_NAISSANCE_USER1);

		utilisateurService.creerUtilisateur(utilisateur);

		em.flush();

		return utilisateur;
	}

	private Utilisateur createUser2() throws MetierException {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setLogin("user2");
		utilisateur.setMotDePasse("mdp2");
		utilisateur.setDateNaissance(new Date());

		utilisateurService.creerUtilisateur(utilisateur);

		em.flush();

		return utilisateur;
	}

	private Tag createTag1() throws MetierException {
		Tag tag = new Tag();
		tag.setNom("tag1");

		tagService.creerTag(tag);

		return tag;
	}

	private Tag createTag2() throws MetierException {
		Tag tag = new Tag();
		tag.setNom("tag2");

		tagService.creerTag(tag);

		return tag;
	}

	private void mockCtrlUrl1() {
		mockServer
				.expect(requestTo("http://localhost:8080/ctrl-contenu-war/url?url=" + URL1 + "&dateNaissance="
						+ DATE_TIMER_FORMATTER.print(DATE_NAISSANCE_USER1.getTime())))
				.andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
	}

	private void mockCtrlUrl2() {
		mockServer
				.expect(requestTo("http://localhost:8080/ctrl-contenu-war/url?url=" + URL2 + "&dateNaissance="
						+ DATE_TIMER_FORMATTER.print(DATE_NAISSANCE_USER1.getTime())))
				.andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
	}

	private void mockCtrlNewUrl() {
		mockServer
				.expect(requestTo("http://localhost:8080/ctrl-contenu-war/url?url=" + NEW_URL + "&dateNaissance="
						+ DATE_TIMER_FORMATTER.print(DATE_NAISSANCE_USER1.getTime())))
				.andExpect(method(HttpMethod.POST)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));
	}

	private void mockFiltrerList(String fileNameIn, String fileNameOut) {

		String in = readFile(fileNameIn);
		String out = readFile(fileNameOut);

		mockServer.expect(requestTo("http://localhost:8080/ctrl-contenu-war/url/multi"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(method(HttpMethod.POST))
				.andExpect(content().string(in)).andRespond(withSuccess(out, MediaType.APPLICATION_JSON));
	}

	private String readFile(String fileName) {
		try {
			return Files.toString(applicationContext.getResource("classpath:" + fileName).getFile(),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Test
	@Transactional
	public void ajouterFavoris() throws MetierException {

		// setup
		Utilisateur utilisateur = createUser();
		mockCtrlUrl1();
		mockFiltrerList("/json/URL1_IN.json", "/json/URL1_OUT.json");

		// test
		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);

		Favoris favoris2 = favorisService.creerFavoris(utilisateur, favoris);

		em.flush();
		em.clear();

		Utilisateur utilisateur2 = utilisateurService.authentifier("user1", "mdp1");

		List<Favoris> favoris3 = favorisService.listerFavoris(utilisateur2);
		assertEquals(1, favoris3.size());
		assertEquals("fav1", favoris3.get(0).getNom());

		mockServer.verify();

	}

	@Test
	@Transactional
	public void listerFavoris() throws MetierException {

		// setup
		Utilisateur utilisateur = createUser();
		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL1_URL2_IN.json", "/json/URL1_URL2_OUT.json");

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);

		favoris = new Favoris();
		favoris.setNom("fav2");
		favoris.setUrl(URL2);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);
		
		em.flush();
		em.clear();

		//test
		Utilisateur utilisateur2 = utilisateurService.authentifier("user1", "mdp1");

		List<Favoris> favoriss = favorisService.listerFavoris(utilisateur2);
		assertEquals(2, favoriss.size());
		assertEquals("fav1", favoriss.get(0).getNom());
		assertEquals("fav2", favoriss.get(1).getNom());

		mockServer.verify();

	}

	@Test
	@Transactional
	public void listerFavorisFiltreUrl1() throws MetierException {

		// setup
		Utilisateur utilisateur = createUser();
		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL1_URL2_IN.json", "/json/URL2_OUT.json");

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);

		favoris = new Favoris();
		favoris.setNom("fav2");
		favoris.setUrl(URL2);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);
		
		em.flush();
		em.clear();

		//test
		Utilisateur utilisateur2 = utilisateurService.authentifier("user1", "mdp1");

		List<Favoris> favoriss = favorisService.listerFavoris(utilisateur2);
		assertEquals(1, favoriss.size());
		assertEquals("fav2", favoriss.get(0).getNom());

		mockServer.verify();

	}	
	
	
	@Test
	@Transactional
	public void listerFavorisFiltreAllUrl() throws MetierException {

		// setup
		Utilisateur utilisateur = createUser();
		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL1_URL2_IN.json", "/json/EMPTY_OUT.json");

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);

		favoris = new Favoris();
		favoris.setNom("fav2");
		favoris.setUrl(URL2);
		favoris.setType(FavorisType.PUBLIC);
		favorisService.creerFavoris(utilisateur, favoris);
		
		em.flush();
		em.clear();

		//test
		Utilisateur utilisateur2 = utilisateurService.authentifier("user1", "mdp1");

		List<Favoris> favoriss = favorisService.listerFavoris(utilisateur2);
		assertEquals(0, favoriss.size());

		mockServer.verify();

	}	
	
	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterFavorisKo1() throws MetierException {

		// setup
		mockCtrlUrl1();
		mockCtrlUrl2();

		Utilisateur utilisateur = createUser();

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);

		favorisService.creerFavoris(utilisateur, favoris);

		em.flush();

		// test
		favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL2);
		favoris.setType(FavorisType.PRIVE);

		favorisService.creerFavoris(utilisateur, favoris);

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterFavorisKo2() throws MetierException {

		Utilisateur utilisateur = createUser();

		favorisService.creerFavoris(utilisateur, null);

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterFavorisKo3() throws MetierException {

		Favoris favoris = new Favoris();
		favoris.setNom("fav1");
		favoris.setUrl(URL1);
		favoris.setType(FavorisType.PUBLIC);

		favorisService.creerFavoris(null, favoris);

	}

	private Favoris createFavoris1(Utilisateur utilisateur) throws MetierException {
		Favoris favoris1 = new Favoris();
		favoris1.setNom("favoris1");
		favoris1.setUrl(URL1);
		favoris1.setType(FavorisType.PUBLIC);

		favorisService.creerFavoris(utilisateur, favoris1);
		return favoris1;
	}

	private Favoris createFavoris2(Utilisateur utilisateur) throws MetierException {
		Favoris favoris1 = new Favoris();
		favoris1.setNom("favoris2");
		favoris1.setUrl(URL2);
		favoris1.setType(FavorisType.PRIVE);

		favorisService.creerFavoris(utilisateur, favoris1);
		return favoris1;
	}

	@Test(expected = MetierException.class)
	@Transactional
	public void modifierFavorisKo1() throws MetierException {
		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.modifierFavoris(utilisateur, null, NEW_URL, FavorisType.PRIVE);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void modifierFavorisKo2() throws MetierException {
		// setup
		mockCtrlUrl1();
		mockCtrlNewUrl();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.modifierFavoris(null, favoris1, NEW_URL, FavorisType.PRIVE);

		em.flush();

	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void modifierFavorisKo3() throws MetierException {
		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.modifierFavoris(utilisateur, favoris1, null, FavorisType.PRIVE);

		em.flush();

	}

	@Test(expected = ConstraintViolationException.class)
	@Transactional
	public void modifierFavorisKo4() throws MetierException {
		// setup
		mockCtrlUrl1();
		mockCtrlNewUrl();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.modifierFavoris(utilisateur, favoris1, NEW_URL, null);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void modifierFavorisKoWrongUser() throws MetierException {
		// setup
		mockCtrlUrl1();
		mockCtrlNewUrl();

		Utilisateur utilisateur = createUser();
		Utilisateur utilisateur2 = createUser2();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur2 = em.merge(utilisateur2);
		favoris1 = em.merge(favoris1);

		favorisService.modifierFavoris(utilisateur2, favoris1, NEW_URL, FavorisType.PUBLIC);

		em.flush();

	}

	@Test
	@Transactional
	public void modifierFavoris() throws MetierException {

		// setup
		mockCtrlUrl1();
		mockCtrlUrl2();
		mockCtrlNewUrl();
		mockFiltrerList("/json/NEWURL_URL2_IN.json", "/json/NEWURL_URL2_OUT.json");		

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Favoris favoris2 = createFavoris2(utilisateur);

		em.flush();
		em.clear();

		// test

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favoris1 = favorisService.modifierFavoris(utilisateur, favoris1, NEW_URL, FavorisType.PRIVE);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		List<Favoris> favoris = favorisService.listerFavoris(utilisateur);

		assertEquals(2, favoris.size());

		int f1Pos = 0;
		int f2Pos = 1;

		if (!favoris.get(0).equals(favoris1)) {
			f1Pos = 1;
			f2Pos = 0;
		}

		assertEquals(NEW_URL, favoris.get(f1Pos).getUrl());
		assertEquals(FavorisType.PRIVE, favoris.get(f1Pos).getType());

		assertEquals(URL2, favoris.get(f2Pos).getUrl());
		assertEquals(FavorisType.PRIVE, favoris.get(f2Pos).getType());

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerFavorisKo1() throws MetierException {
		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.supprimerFavoris(null, favoris1);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerFavorisKo2() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.supprimerFavoris(utilisateur, null);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerFavorisKoWrongUser() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Utilisateur utilisateur2 = createUser2();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur2 = em.merge(utilisateur2);
		favoris1 = em.merge(favoris1);

		favorisService.supprimerFavoris(utilisateur2, favoris1);

		em.flush();

	}

	@Test
	@Transactional
	public void supprimerFavoris() throws MetierException {
		// setup
		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL2_IN.json", "/json/URL2_OUT.json");

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Favoris favoris2 = createFavoris2(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);

		favorisService.supprimerFavoris(utilisateur, favoris1);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		List<Favoris> favoris = favorisService.listerFavoris(utilisateur);

		assertEquals(1, favoris.size());

		assertEquals(favoris2, favoris.get(0));

	}

	//

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterTagFavorisKo1() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.ajouterTag(null, favoris1, tag1);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterTagFavorisKo2() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.ajouterTag(utilisateur, null, tag1);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterTagFavorisKo3() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.ajouterTag(utilisateur, favoris1, null);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void ajouterTagKoWrongUser() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Utilisateur utilisateur2 = createUser2();
		Tag tag1 = createTag1();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur2 = em.merge(utilisateur2);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.ajouterTag(utilisateur2, favoris1, tag1);

		em.flush();

	}

	@Test
	@Transactional
	public void ajouterTag() throws MetierException {
		// setup

		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL1_URL2_IN.json", "/json/URL1_URL2_OUT.json");

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Favoris favoris2 = createFavoris2(utilisateur);
		Tag tag1 = createTag1();
		Tag tag2 = createTag2();

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		favoris2 = em.merge(favoris2);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		favorisService.ajouterTag(utilisateur, favoris1, tag1);
		favorisService.ajouterTag(utilisateur, favoris1, tag2);

		favorisService.ajouterTag(utilisateur, favoris2, tag1);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		List<Favoris> favoris = favorisService.listerFavoris(utilisateur);

		assertEquals(2, favoris.size());

		int f1Pos = 0;
		int f2Pos = 1;

		if (!favoris.get(0).equals(favoris1)) {
			f1Pos = 1;
			f2Pos = 0;
		}

		assertEquals(2, favoris.get(f1Pos).getTags().size());
		assertTrue(favoris.get(f1Pos).getTags().contains(tag1));
		assertTrue(favoris.get(f1Pos).getTags().contains(tag2));

		assertEquals(1, favoris.get(f2Pos).getTags().size());
		assertTrue(favoris.get(f2Pos).getTags().contains(tag1));

	}

	@Test
	@Transactional
	public void ajouterTagTwice() throws MetierException {

		// setup
		mockCtrlUrl1();
		mockFiltrerList("/json/URL1_IN.json", "/json/URL1_OUT.json");		
		

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();
		Tag tag2 = createTag2();

		em.flush();
		em.clear();

		// test

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		favorisService.ajouterTag(utilisateur, favoris1, tag1);
		favorisService.ajouterTag(utilisateur, favoris1, tag2);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		favorisService.ajouterTag(utilisateur, favoris1, tag2);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		List<Favoris> favoris = favorisService.listerFavoris(utilisateur);

		assertEquals(1, favoris.size());

		assertEquals(2, favoris.get(0).getTags().size());
		assertTrue(favoris.get(0).getTags().contains(tag1));
		assertTrue(favoris.get(0).getTags().contains(tag2));

	}

	//

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerTagFavorisKo1() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();
		favorisService.ajouterTag(utilisateur, favoris1, tag1);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.supprimerTag(null, favoris1, tag1);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerTagFavorisKo2() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();
		favorisService.ajouterTag(utilisateur, favoris1, tag1);

		em.flush();
		em.clear();

		// test

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.supprimerTag(utilisateur, null, tag1);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerTagFavorisKo3() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();
		favorisService.ajouterTag(utilisateur, favoris1, tag1);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.supprimerTag(utilisateur, favoris1, null);

		em.flush();

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerTagKoWrongUser() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();
		Utilisateur utilisateur2 = createUser2();
		Tag tag1 = createTag1();

		Favoris favoris1 = createFavoris1(utilisateur);

		em.flush();
		em.clear();

		// test
		utilisateur2 = em.merge(utilisateur2);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);

		favorisService.supprimerTag(utilisateur2, favoris1, tag1);

		em.flush();

	}

	@Test
	@Transactional
	public void supprimerTag() throws MetierException {
		// setup

		mockCtrlUrl1();
		mockCtrlUrl2();
		mockFiltrerList("/json/URL1_URL2_IN.json", "/json/URL1_URL2_OUT.json");		

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Favoris favoris2 = createFavoris2(utilisateur);
		Tag tag1 = createTag1();
		Tag tag2 = createTag2();

		favorisService.ajouterTag(utilisateur, favoris1, tag1);
		favorisService.ajouterTag(utilisateur, favoris1, tag2);

		favorisService.ajouterTag(utilisateur, favoris2, tag1);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		favoris2 = em.merge(favoris2);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		// test
		favorisService.supprimerTag(utilisateur, favoris1, tag1);
		favorisService.supprimerTag(utilisateur, favoris2, tag1);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		List<Favoris> favoris = favorisService.listerFavoris(utilisateur);

		assertEquals(2, favoris.size());

		int f1Pos = 0;
		int f2Pos = 1;

		if (!favoris.get(0).equals(favoris1)) {
			f1Pos = 1;
			f2Pos = 0;
		}

		assertEquals(1, favoris.get(f1Pos).getTags().size());
		assertTrue(favoris.get(f1Pos).getTags().contains(tag2));

		assertEquals(0, favoris.get(f2Pos).getTags().size());

	}

	@Test(expected = MetierException.class)
	@Transactional
	public void supprimerTagTwice() throws MetierException {

		// setup
		mockCtrlUrl1();

		Utilisateur utilisateur = createUser();

		Favoris favoris1 = createFavoris1(utilisateur);
		Tag tag1 = createTag1();
		Tag tag2 = createTag2();

		favorisService.ajouterTag(utilisateur, favoris1, tag1);
		favorisService.ajouterTag(utilisateur, favoris1, tag2);

		em.flush();
		em.clear();

		// test
		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		favorisService.supprimerTag(utilisateur, favoris1, tag2);

		em.flush();
		em.clear();

		utilisateur = em.merge(utilisateur);
		favoris1 = em.merge(favoris1);
		tag1 = em.merge(tag1);
		tag2 = em.merge(tag2);

		favorisService.supprimerTag(utilisateur, favoris1, tag2);

		em.flush();
		em.clear();

	}

}
