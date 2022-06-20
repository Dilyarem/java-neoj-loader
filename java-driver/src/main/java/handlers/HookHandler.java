package handlers;


import atlassian.Fields;
import atlassian.Neo4jLoader;
import atlassian.NodeUpdInfo;
import atlassian.RelationInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HookHandler {
    private final Logger logger;
    private final Neo4jLoader neo4jLoader;



    public HookHandler(Neo4jLoader neo4jLoader) {
        this.logger  = LoggerFactory.getLogger(HookHandler.class);
        this.neo4jLoader = neo4jLoader;
    }



    public void handleIssueUpdated(JSONObject issue_info) {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject issue_info = jsonArray.getJSONObject(i);

            JSONObject issue_info = json.getJSONObject("issue");
        NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Issue", "id", issue_info.get("id").toString());
        for (String field: Fields.issueFields()) {
            if (issue_info.getJSONObject("fields").get(field) == JSONObject.NULL) {
                continue;
            }
            nodeUpdInfo.addProperty(field, issue_info.getJSONObject("fields").get(field));
        }
        nodeUpdInfo.addProperty("key", issue_info.get("key"));

        for (String rel: Fields.issueUserRelations()) {
            RelationInfo userRel = new RelationInfo("User","accountId",
                    issue_info.getJSONObject("fields").getJSONObject(rel).get("accountId"), rel);
            nodeUpdInfo.addRelation(userRel);
        }

        RelationInfo projRel = new RelationInfo("Project", "id", issue_info.getJSONObject("fields").getJSONObject("project").get("id").toString(), "project");
        nodeUpdInfo.addRelation(projRel);
        nodeUpdInfos.add(nodeUpdInfo);


        neo4jLoader.load(nodeUpdInfos);
    }



    public void handleProjUpdated(JSONObject proj_info)  {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject proj_info = jsonArray.getJSONObject(i);

        NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Project", "id", proj_info.get("id").toString());

        for (String field: Fields.projectFields()) {
            nodeUpdInfo.addProperty(field, proj_info.get(field));
        }

        RelationInfo projLead = new RelationInfo("User","accountId",
                proj_info.getJSONObject("projectLead").get("accountId"), "projectLead");
        nodeUpdInfo.addRelation(projLead);

            nodeUpdInfos.add(nodeUpdInfo);
            }

        neo4jLoader.load(nodeUpdInfos);
    }

    public void handleProjDeleted(String json) {
        JSONObject proj_info = (new JSONObject(json)).getJSONObject("project");

        NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Project", "id", proj_info.get("id").toString());
        nodeUpdInfo.addProperty("deleted", true);
        neo4jLoader.load(List.of(nodeUpdInfo));
    }
}
