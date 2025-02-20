package io.onedev.server.search.entity.build;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.model.Build;
import io.onedev.server.model.User;
import io.onedev.server.util.ProjectScope;
import io.onedev.server.util.criteria.Criteria;

public class SubmittedByMeCriteria extends Criteria<Build> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		if (User.get() != null) {
			Path<User> attribute = from.get(Build.PROP_SUBMITTER);
			return builder.equal(attribute, User.get());
		} else {
			throw new ExplicitException("Please login to perform this query");
		}
	}

	@Override
	public boolean matches(Build build) {
		if (User.get() != null)
			return User.get().equals(build.getSubmitter());
		else
			throw new ExplicitException("Please login to perform this query");
	}

	@Override
	public String toStringWithoutParens() {
		return BuildQuery.getRuleName(BuildQueryLexer.SubmittedByMe);
	}

}
