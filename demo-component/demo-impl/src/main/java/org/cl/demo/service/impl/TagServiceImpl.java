package org.cl.demo.service.impl;

import java.util.List;

import org.cl.demo.dao.TagDao;
import org.cl.demo.entity.Tag;
import org.cl.demo.exceptions.MetierException;
import org.cl.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
@Transactional(rollbackFor = { MetierException.class })
public class TagServiceImpl implements TagService {

	@Autowired
	private TagDao tagDao;

	@Override
	public Tag creerTag(Tag tag) throws MetierException {

		if (tag == null) {
			throw new MetierException("Le tag est null");
		}

		if (tagDao.findTagByName(tag.getNom()) != null) {
			throw new MetierException("un tag avec ce nom existe dejà : " + tag.getNom());
		}

		return tagDao.create(tag);

	}

	@Override
	public List<Tag> obtenirTags() {
		return tagDao.findAllTags();
	}

	@Override
	public Tag getTagById(int id) throws MetierException {
		Tag tag = tagDao.findById(id);

		if (tag == null) {
			throw new MetierException("Le Tag n'existe pas, id : " + id);
		}

		return tag;

	}

	public TagDao getTagDao() {
		return tagDao;
	}

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

}
