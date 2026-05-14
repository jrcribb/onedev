package io.onedev.server.ai.tools.pullrequest;

import static io.onedev.server.ai.ToolUtils.convertToJson;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import com.fasterxml.jackson.databind.JsonNode;

import dev.langchain4j.agent.tool.ToolSpecification;
import io.onedev.server.OneDev;
import io.onedev.server.ai.PullRequestHelper;
import io.onedev.server.ai.TaskTool;
import io.onedev.server.ai.ToolExecutionResult;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.service.PullRequestService;

public final class GetPullRequestCodeComments implements TaskTool {

	private final long pullRequestId;

	public GetPullRequestCodeComments(long pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	@Override
	public ToolSpecification getSpecification() {
		return ToolSpecification.builder()
				.name("getPullRequestCodeComments")
				.description("Get line-anchored code comments on the OneDev pull request in json format. "
						+ "Each item includes id, filePath, startLine, endLine (1-based on the PR head), user, date, "
						+ "content, resolved, and replies.")
				.build();
	}

	@Override
	public ToolExecutionResult execute(Subject subject, JsonNode arguments) {
		var request = OneDev.getInstance(PullRequestService.class).load(pullRequestId);
		if (!SecurityUtils.canReadCode(subject, request.getProject()))
			throw new UnauthorizedException();
		return new ToolExecutionResult(convertToJson(PullRequestHelper.getCodeComments(request)), false);
	}
}
