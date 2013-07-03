package org.cl.demo.dao;

import java.util.List;

import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;

public interface TagDao extends GenericDao<Tag> {

	List<Tag> findAllTags();

	Tag create(Tag tag);

	Tag findTagByName(String name) throws MetierException;

}
