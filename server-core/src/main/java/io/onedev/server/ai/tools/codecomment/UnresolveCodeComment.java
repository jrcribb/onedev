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

public final class UnresolveCodeComment implements TaskTool {

	@Override
	public ToolSpecification getSpecification() {
		return ToolSpecification.builder()
				.name("unresolveCodeComment")
				.description("Mark a resolved line-anchored code comment as unresolved, optionally with a short note.")
				.parameters(JsonObjectSchema.builder()
						.addIntegerProperty("commentId").description("Existing code comment id")
						.addStringProperty("note").description("Optional note explaining why it is marked as unresolved")
						.required("commentId")
						.build())
				.build();
	}

	@Override
	public ToolExecutionResult execute(Subject subject, JsonNode arguments) {
		if (arguments.get("commentId") == null)
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", "Argument 'commentId' is required")), false);
		var commentId = arguments.get("commentId").asLong();

		var noteNode = arguments.get("note");
		var note = noteNode != null && !noteNode.isNull() ? noteNode.asText() : null;

		var comment = OneDev.getInstance(CodeCommentService.class).load(commentId);
		try {
			var commentInfo = CodeCommentHelper.changeStatus(subject, comment, false, note);
			return new ToolExecutionResult(convertToJson(commentInfo), false);
		} catch (ExplicitException e) {
			return new ToolExecutionResult(convertToJson(Map.of("successful", false, "failReason", e.getMessage())), false);
		}
	}
}
