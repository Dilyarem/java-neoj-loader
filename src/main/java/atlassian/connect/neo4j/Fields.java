package atlassian.connect.neo4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fields {
    public static Map<String, List<String>> issueFields() {
        return Map.of("key", List.of("key"),
                "description", List.of("fields", "description"),
                "summary", List.of("fields", "summary"),
                "created", List.of("fields", "created"),
                "resolutiondate", List.of("fields", "resolutiondate"),
                "updated", List.of("fields", "updated"),
                "votes", List.of("fields", "votes", "votes"),
                "watches", List.of("fields", "watches", "watchCount")
                );
    }

    public static Map<String, List<List<String>>> issueRelations() {
        return Map.of(
                "User", List.of(List.of("creator", "accountId"), List.of("assignee", "accountId"),
                        List.of("reporter", "accountId")),
                "Project", List.of(List.of( "project", "id")),
                "priority", List.of(List.of( "priority", "name")),
                "status", List.of(List.of("status", "name")),
                "issuetype", List.of(List.of( "issuetype", "name")),
                "resolution", List.of(List.of( "resolution", "name"))

        );
    }

    public static Map<String, List<String>> projectFields() {
        return Map.of("key", List.of("key"),
                "name", List.of("name"));
    }

    public static Map<String, List<List<String>>> projectRelations() {
        return Map.of(//"User", List.of(List.of("projectLead", "accountId")),
                "projectTypeKey", List.of(List.of("projectTypeKey")),
                "isPrivate", List.of(List.of("isPrivate")));
    }

    public static Map<String, List<String>> userFields() {
        return Map.of("key", List.of("displayName"));
    }

    public static Map<String, List<List<String>>> userRelations() {
        return Map.of("timeZone", List.of(List.of("timeZone")),
                "accountType", List.of(List.of("accountType")));
    }
}