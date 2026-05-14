package io.onedev.server.ai.tools.codecomment;

import static io.onedev.server.ai.ToolUtils.convertToJson;

import java.util.Map;

import org.apache.shiro.subject.Subject;

import com.fasterxml.jackson.databind.JsonNode;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import io.onedev.commons.utils.ExplicitException;
import io.onedev.server.OneDev;
import io.onedev.server.ai.CodeCommentHelper;
import io.onedev.server.ai.TaskTool;
import io.onedev.server.ai.ToolExecutionResult;
import io.onedev.server.service.CodeCommentService;

public final class AddCodeCommentReply implements TaskTool {

	@Override
	public ToolSpecification getSpecification() {
		return ToolSpecification.builder()
				.name("addCodeCommentReply")
				.description("Add a reply to an existing line-anchored code comment.")
				.parameters(JsonObjectSchema.builder()
						.addIntegerProperty("commentId").description("Existing code comment id")
						.addStringProperty("content").description("Reply body")
						.required("commentId", "content")
						.build())
				.build();
	}

	@Override
	public ToolExecutionResult execute(Subject subject, JsonNode arguments) {
		if (arguments.get("commentId") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'commentId' is required")), false);
		var commentId = arguments.get("commentId").asLong();

		if (arguments.get("content") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'content' is required")), false);
		var content = arguments.get("content").asText();

		var comment = OneDev.getInstance(CodeCommentService.class).load(commentId);
		try {
			var reply = CodeCommentHelper.addReply(subject, comment, content);
			return new ToolExecutionResult(convertToJson(reply), false);
		} catch (ExplicitException e) {
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", e.getMessage())), false);
		}
	}

}
