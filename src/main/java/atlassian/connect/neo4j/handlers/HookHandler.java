package atlassian.connect.neo4j.handlers;

import atlassian.connect.neo4j.Fields;
import atlassian.connect.neo4j.graphInfo.NodeUpdInfo;
import atlassian.connect.neo4j.graphInfo.RelationInfo;
import atlassian.connect.neo4j.loader.Neo4jLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HookHandler {
    private final Logger logger;
    private final Neo4jLoader neo4jLoader;



    public HookHandler(Neo4jLoader neo4jLoader) {
        this.logger  = LoggerFactory.getLogger(HookHandler.class);
        this.neo4jLoader = neo4jLoader;
    }


    public void handleIssueUpdated(JSONArray jsonArray) {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject issueInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Issue", "id", issueInfo.get("id").toString());

            addProps(issueInfo, nodeUpdInfo, Fields.issueFields());
            addRels(issueInfo.getJSONObject("fields"), nodeUpdInfo, Fields.issueRelations());
            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }

    public void handleIssueDeleted(JSONArray jsonArray) {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject issueInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Issue", "id", issueInfo.get("id").toString());
            nodeUpdInfo.addProperty("deleted", true);
            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }

    private void addRels(JSONObject info, NodeUpdInfo nodeUpdInfo, Map<String, List<List<String>>> rels) {
        for (Map.Entry<String, List<List<String>>> entry : rels.entrySet()) {
            String endNodeLabel = entry.getKey();

            List<List<String>> relations = entry.getValue();
            for (List<String> path: relations) {
                JSONObject obj = info;

                for (int j = 0; j < path.size() - 1; ++j) {
                    try {
                    obj = obj.getJSONObject(path.get(j));
                    } catch (JSONException e) {}
                }

                try {
                if (obj.get(path.get(path.size() - 1)) == JSONObject.NULL) {
                    continue;
                } }  catch (JSONException e) {continue;}


                String id = obj.get(path.get(path.size() - 1)).toString();
                RelationInfo rel = new RelationInfo(endNodeLabel, path.get(path.size() - 1),
                        id, path.get(0));
                nodeUpdInfo.addRelation(rel);
            }
        }
    }

    private void addProps(JSONObject info, NodeUpdInfo nodeUpdInfo, Map<String, List<String>> props) {
        for (Map.Entry<String,List<String>> entry : props.entrySet()) {
            String fieldName = entry.getKey();
            List<String> path = entry.getValue();
            JSONObject obj = info;
            try {
                for (int j = 0; j < path.size() - 1; ++j) {
                    obj = obj.getJSONObject(path.get(j));
                } } catch (JSONException e) {continue;}

            if (obj.get(path.get(path.size() - 1)) == JSONObject.NULL) {
                continue;
            }
            String value = obj.get(path.get(path.size() - 1)).toString();
            nodeUpdInfo.addProperty(fieldName, value);
        }
    }


    public void handleProjUpdated(JSONArray jsonArray)  {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject projInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Project", "id", projInfo.get("id").toString());
            addProps(projInfo, nodeUpdInfo, Fields.projectFields());
            addRels(projInfo, nodeUpdInfo, Fields.projectRelations());
            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }



    public void handleProjDeleted(JSONArray jsonArray)  {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject projInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("Project", "id", projInfo.get("id").toString());
            nodeUpdInfo.addProperty("deleted", true);
            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }

    public void handleUserUpdated(JSONArray jsonArray)  {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("User", "accountId", userInfo.get("accountId").toString());

            addProps(userInfo, nodeUpdInfo, Fields.userFields());
            addRels(userInfo, nodeUpdInfo, Fields.userRelations());

            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }

    public void handleUserDeleted(JSONArray jsonArray)  {
        List<NodeUpdInfo> nodeUpdInfos = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userInfo = jsonArray.getJSONObject(i);
            NodeUpdInfo nodeUpdInfo = new NodeUpdInfo("User", "accountId", userInfo.get("accountId").toString());
            nodeUpdInfo.addProperty("deleted", true);
            nodeUpdInfos.add(nodeUpdInfo);
        }
        neo4jLoader.load(nodeUpdInfos);
    }
}
