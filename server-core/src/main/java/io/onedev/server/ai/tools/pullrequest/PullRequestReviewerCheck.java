package io.onedev.server.ai.tools.pullrequest;

import static io.onedev.server.ai.ToolUtils.convertToJson;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import io.onedev.server.ai.ToolExecutionResult;
import io.onedev.server.model.PullRequest;
import io.onedev.server.model.PullRequestReview;
import io.onedev.server.model.User;

public final class PullRequestReviewerCheck {

	private PullRequestReviewerCheck() {
	}

	@Nullable
	public static ToolExecutionResult rejectIfNotReviewer(PullRequest request, User user) {
		var review = request.getReview(user);
		if (review == null || review.getStatus() == PullRequestReview.Status.EXCLUDED) {
			var data = Map.of(
					"successful", false,
					"failReason", "You are not a reviewer and is not allowed to approve this pull request. Add your option as comment instead");
			return new ToolExecutionResult(convertToJson(data), false);
		}
		return null;
	}
}
