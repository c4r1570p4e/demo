package org.cl.demo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
// ne pas oublier le cache de la collection plus bas
@Table(uniqueConstraints = @UniqueConstraint(name = "nomPropUnique", columnNames = { "nom", "propid" }))
public class Favoris implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotNull(message = "Nom du favoris obligatoire")
	private String nom;

	@NotNull(message = "Url obligatoire")
	private String url;

	@Enumerated(EnumType.ORDINAL)
	@NotNull(message = "Type obligatoire")
	private FavorisType type;

	@ManyToOne(optional = false, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@NotNull(message = "un favoris doit avoir un propriétaire")
	@JoinColumn(name = "propid")
	private Utilisateur proprietaire;

	@ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Tag> tags = new HashSet<Tag>();

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public void removeTag(Tag tag) {
		this.tags.remove(tag);
	}

	public void clearTag() {
		tags.clear();
	}

	public Favoris() {
		super();
	}

	public int getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FavorisType getType() {
		return type;
	}

	public void setType(FavorisType type) {
		this.type = type;
	}

	public Utilisateur getProprietaire() {
		return proprietaire;
	}

	public void setProprietaire(Utilisateur proprietaire) {
		this.proprietaire = proprietaire;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((proprietaire == null) ? 0 : proprietaire.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		Favoris other;
		try {
			other = (Favoris) obj;
		} catch (ClassCastException e) {
			return false;
		}
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (proprietaire == null) {
			if (other.proprietaire != null)
				return false;
		} else if (!proprietaire.equals(other.proprietaire))
			return false;
		return true;
	}

}
