package io.onedev.server.search.entity.issue;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.model.Issue;
import io.onedev.server.model.IssueWatch;
import io.onedev.server.model.User;
import io.onedev.server.util.ProjectScope;
import io.onedev.server.util.criteria.Criteria;

public class WatchedByMeCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		if (User.get() != null) {
			Subquery<IssueWatch> watchQuery = query.subquery(IssueWatch.class);
			Root<IssueWatch> watch = watchQuery.from(IssueWatch.class);
			watchQuery.select(watch);
			watchQuery.where(builder.and(
					builder.equal(watch.get(IssueWatch.PROP_ISSUE), from),
					builder.equal(watch.get(IssueWatch.PROP_USER), User.get()),
					builder.equal(watch.get(IssueWatch.PROP_WATCHING), true)));
			return builder.exists(watchQuery);
		} else {
			throw new ExplicitException("Please login to perform this query");
		}
	}

	@Override
	public boolean matches(Issue issue) {
		if (User.get() != null)
			return issue.getWatches().stream().anyMatch(it -> it.isWatching() && it.getUser().equals(User.get()));
		else
			throw new ExplicitException("Please login to perform this query");
	}

	@Override
	public String toStringWithoutParens() {
		return IssueQuery.getRuleName(IssueQueryLexer.WatchedByMe);
	}

}
