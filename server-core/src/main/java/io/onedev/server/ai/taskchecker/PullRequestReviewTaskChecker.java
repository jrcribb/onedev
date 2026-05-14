package io.onedev.server.ai.taskchecker;

import java.util.Set;

import io.onedev.server.ai.tools.pullrequest.ApprovePullRequest;
import io.onedev.server.ai.tools.pullrequest.RequestChangesForPullRequestTool;

public class PullRequestReviewTaskChecker implements TaskChecker {

    @Override
    public String preToolCall(String toolName, Set<String> calledTools) {
        if (toolName.equals(ApprovePullRequest.TOOL_NAME) || toolName.equals(RequestChangesForPullRequestTool.TOOL_NAME)) {
            if (calledTools.contains(ApprovePullRequest.TOOL_NAME))
                return "You've already approved the pull request";
            else if (calledTools.contains(RequestChangesForPullRequestTool.TOOL_NAME))
                return "You've already requested changes for the pull request";
            else
                return null;
        } else {
            return null;
        }
    }

    @Override
    public boolean isResponseRequired(Set<String> calledTools) {
        return !calledTools.contains(ApprovePullRequest.TOOL_NAME) && !calledTools.contains(RequestChangesForPullRequestTool.TOOL_NAME);
    }

}
