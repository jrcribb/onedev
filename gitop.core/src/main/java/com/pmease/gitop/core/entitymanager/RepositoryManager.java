package com.pmease.gitop.core.entitymanager;

import java.io.File;

import com.google.inject.ImplementedBy;
import com.pmease.commons.persistence.dao.GenericDao;
import com.pmease.gitop.core.entitymanager.impl.DefaultRepositoryManager;
import com.pmease.gitop.core.model.Repository;

@ImplementedBy(DefaultRepositoryManager.class)
public interface RepositoryManager extends GenericDao<Repository> {
	File locateStorage(Repository repository);
}
