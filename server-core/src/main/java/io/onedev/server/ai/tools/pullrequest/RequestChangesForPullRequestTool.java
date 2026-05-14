package io.onedev.server.ai.tools.pullrequest;

import static io.onedev.server.ai.ToolUtils.convertToJson;

import java.util.Map;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import com.fasterxml.jackson.databind.JsonNode;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import io.onedev.server.OneDev;
import io.onedev.server.ai.TaskTool;
import io.onedev.server.ai.ToolExecutionResult;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.service.PullRequestReviewService;
import io.onedev.server.service.PullRequestService;

public final class RequestChangesForPullRequestTool implements TaskTool {

	public static final String TOOL_NAME = "requestChangesForPullRequest";

	private final long pullRequestId;

	public RequestChangesForPullRequestTool(long pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	@Override
	public ToolSpecification getSpecification() {
		return ToolSpecification.builder()
				.name(TOOL_NAME)
				.description("Record a request for changes for OneDev pull request. "
						+ "Call this exactly once after you have finished reviewing and decided changes are needed.")
				.parameters(JsonObjectSchema.builder()
						.addStringProperty("reason").description("Reason explaining why you are requesting changes for this pull request. Make sure to quote relevant code snippets if applicable")
						.required("reason")
						.build())
				.build();
	}

	@Override
	public ToolExecutionResult execute(Subject subject, JsonNode arguments) {
		var request = OneDev.getInstance(PullRequestService.class).load(pullRequestId);
		var user = SecurityUtils.getUser(subject);
		if (!SecurityUtils.canReadCode(subject, request.getProject()) || user == null)
			throw new UnauthorizedException();

		if (arguments.get("reason") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'reason' is required")), false);
		var reason = arguments.get("reason").asText();

		var result = PullRequestReviewerCheck.rejectIfNotReviewer(request, user);
		if (result != null)
			return result;

		OneDev.getInstance(PullRequestReviewService.class).review(user, request, false, reason);
		return new ToolExecutionResult(convertToJson(Map.of("successful", true)), false);
	}
}
