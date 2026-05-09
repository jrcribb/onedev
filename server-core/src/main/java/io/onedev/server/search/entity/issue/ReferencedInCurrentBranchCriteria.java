package io.onedev.server.search.entity.issue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.jspecify.annotations.Nullable;

import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.model.Issue;
import io.onedev.server.util.ProjectScope;
import io.onedev.server.util.ProjectScopedBranch;
import io.onedev.server.util.criteria.Criteria;

public class ReferencedInCurrentBranchCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;

	private static final Pattern BRANCH_PATTERN = Pattern.compile("(?:.+/)?issue-(\\d+)(?:-.*)?");

	@Nullable
	private static Long parseIssueNumber(String branchName) {
		Matcher matcher = BRANCH_PATTERN.matcher(branchName);
		if (matcher.matches()) {
			try {
				return Long.parseLong(matcher.group(1));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		ProjectScopedBranch branch = ProjectScopedBranch.get();
		if (branch == null)
			throw new ExplicitException("No branch in query context");
		Long issueNumber = parseIssueNumber(branch.getBranchName());
		if (issueNumber == null)
			return builder.disjunction();
		return builder.and(
				builder.equal(from.get(Issue.PROP_PROJECT), branch.getProject()),
				builder.equal(from.get(Issue.PROP_NUMBER), issueNumber));
	}

	@Override
	public boolean matches(Issue issue) {
		ProjectScopedBranch branch = ProjectScopedBranch.get();
		if (branch == null)
			throw new ExplicitException("No branch in query context");
		Long issueNumber = parseIssueNumber(branch.getBranchName());
		if (issueNumber == null)
			return false;
		return issue.getProject().equals(branch.getProject())
				&& issue.getNumber() == issueNumber.longValue();
	}

	@Override
	public String toStringWithoutParens() {
		return IssueQuery.getRuleName(IssueQueryLexer.ReferencedInCurrentBranch);
	}

}
