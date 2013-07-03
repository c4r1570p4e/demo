package org.cl.demo.service;

import java.util.List;

import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;

public interface TagService {

	Tag creerTag(Tag tag) throws MetierException;

	List<Tag> obtenirTags();

	Tag getTagById(int id) throws MetierException;

}
