package io.onedev.server.ai.tools.pullrequest;

import static io.onedev.server.ai.ToolUtils.convertToJson;

import java.util.Map;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import com.fasterxml.jackson.databind.JsonNode;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.OneDev;
import io.onedev.server.ai.CodeCommentHelper;
import io.onedev.server.ai.PullRequestHelper;
import io.onedev.server.ai.TaskTool;
import io.onedev.server.ai.ToolExecutionResult;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.service.PullRequestService;

public final class AddPullRequestCodeComment implements TaskTool {

	private final long pullRequestId;

	public AddPullRequestCodeComment(long pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	@Override
	public ToolSpecification getSpecification() {
		return ToolSpecification.builder()
				.name("addPullRequestCodeComment")
				.description("Add a line-anchored code comment on a file in the OneDev pull request. "
						+ "The line range must appear on the right side (added or equal context lines) of the PR diff. "
						+ "Line numbers are 1-based in the file at the PR head commit.")
				.parameters(JsonObjectSchema.builder()
						.addStringProperty("filePath").description("Path of the file in the repository")
						.addIntegerProperty("fromLine").description("Start line number, 1-based")
						.addIntegerProperty("toLine").description("End line number, 1-based (omit to use fromLine)")
						.addStringProperty("content").description("Comment body")
						.required("filePath", "fromLine", "content")
						.build())
				.build();
	}

	@Override
	public ToolExecutionResult execute(Subject subject, JsonNode arguments) {
		var request = OneDev.getInstance(PullRequestService.class).load(pullRequestId);
		var user = SecurityUtils.getUser(subject);
		if (!SecurityUtils.canReadCode(subject, request.getProject()) || user == null)
			throw new UnauthorizedException();

		if (arguments.get("filePath") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'filePath' is required")), false);
		var filePath = arguments.get("filePath").asText();

		if (arguments.get("fromLine") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'fromLine' is required")), false);
		var fromLine = arguments.get("fromLine").asInt();

		var toLineNode = arguments.get("toLine");
		var toLine = toLineNode != null && !toLineNode.isNull() ? toLineNode.asInt() : fromLine;

		if (arguments.get("content") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'content' is required")), false);
		var content = arguments.get("content").asText();

		try {
			var comment = PullRequestHelper.addCodeComment(request, user, filePath, fromLine, toLine, content);
			return new ToolExecutionResult(convertToJson(CodeCommentHelper.getDetail(comment)), false);
		} catch (ExplicitException e) {
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", e.getMessage())), false);
		}
	}
}
