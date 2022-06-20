package atlassian;

import java.util.Set;

public class Fields {
    public static Set<String> issueFields() {
        //return Set.of("id", "key", "created", "resolutiondate", "updated", "description", "summary", "watches");
        return Set.of("created", "resolutiondate", "updated", "description", "summary");

    }

    public static Set<String> issueUserRelations() {
        //return Set.of("issuetype", "project", "resolution", "priority", "assignee", "status", "creator", "subtasks",
          //      "reporter");
        return Set.of("creator", "reporter");
    }

    public static Set<String> projectFields() {
        return Set.of("key", "name");
    }

    Set<String> projectRelations() {
        return Set.of("projectTypeKey", "isPrivate");
    }

    public static Set<String> userFields() {
        return Set.of("accountId", "displayName");
    }

    Set<String> uesrRelations() {
        return Set.of("timeZone", "accountType"); //ToDo мб убрать тип аккаунта
    }
}
