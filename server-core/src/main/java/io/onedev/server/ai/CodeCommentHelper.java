package io.onedev.server.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.onedev.server.OneDev;
import io.onedev.server.model.CodeComment;
import io.onedev.server.model.CodeCommentReply;

public class CodeCommentHelper {
    
    private static ObjectMapper getObjectMapper() {
        return OneDev.getInstance(ObjectMapper.class);
    }

    public static Map<String, Object> getDetail(CodeComment comment) {        
        var typeReference = new TypeReference<LinkedHashMap<String, Object>>() {};
        var data = getObjectMapper().convertValue(comment, typeReference);
        data.remove("id");
        data.remove("uuid");
        data.remove("userId");
        data.put("user", comment.getUser().getName());
        data.put("project", comment.getProject().getPath());
        data.put("filePath", comment.getMark().getPath());
        data.put("fromLineNumber", comment.getMark().getRange().getFromRow() + 1);
        data.put("toLineNumber", comment.getMark().getRange().getToRow() + 1);
        data.remove("lastActivity");
        data.remove("replyCount");
        data.remove("compareContext");
        data.remove("mark");
        return data;
    }

    public static List<Map<String, Object>> getReplies(CodeComment comment) {
        var replies = new ArrayList<Map<String, Object>>();
        comment.getReplies().stream().sorted(Comparator.comparing(CodeCommentReply::getId)).forEach(reply -> {
            var replyMap = new HashMap<String, Object>();
            replyMap.put("user", reply.getUser().getName());
            replyMap.put("date", reply.getDate());
            replyMap.put("content", reply.getContent());
            replies.add(replyMap);
        });
        return replies;
    }

}
