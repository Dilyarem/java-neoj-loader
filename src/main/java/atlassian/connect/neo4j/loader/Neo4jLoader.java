package atlassian.connect.neo4j.loader;

import atlassian.connect.neo4j.graphInfo.NodeUpdInfo;
import atlassian.connect.neo4j.graphInfo.RelationInfo;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Neo4jLoader {
    private final Driver driver;
    private final Logger logger;

    public Neo4jLoader(@Value("${spring.neo4j.uri}") String uri, @Value("${spring.neo4j.authentication.username}") String username, @Value("${spring.neo4j.authentication.password}") String password) {
        this.logger  = LoggerFactory.getLogger(Neo4jLoader.class);
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password), Config.defaultConfig());
    }


    public void load(List<NodeUpdInfo> nodeUpdInfos) {
        try (Session session = driver.session()) {
            try(Transaction tx = session.beginTransaction() ) {
                for (NodeUpdInfo nodeUpdInfo: nodeUpdInfos) {
                    String updNodeLabel = nodeUpdInfo.getLabel();
                    String updNodeIdField = nodeUpdInfo.getIdField();
                    Object updNodeIdValue = nodeUpdInfo.getIdValue();

                    updateProperties(tx, nodeUpdInfo.getProps(), updNodeLabel, updNodeIdField, updNodeIdValue);

                    for (RelationInfo relationInfo: nodeUpdInfo.getRels()) {
                        createRelation(tx, updNodeLabel, updNodeIdField, updNodeIdValue, relationInfo);
                    }
                }
                tx.commit();
            }

        } catch (Neo4jException ex) {
            logger.error(ex.toString());
        }
    }

    private void createRelation(Transaction tx, String updNodeLabel, String updNodeIdField, Object updNodeIdValue,
                                RelationInfo relationInfo) {
        String relationship = String.format("MERGE (p:%s{%s:$pId})", updNodeLabel, updNodeIdField);
        relationship += String.format("MERGE (u:%s{%s:$uId})",  relationInfo.getEndNodeLabel(),
                relationInfo.getEndNodeIdField());
        relationship += String.format("CREATE (p)-[r:%s]->(u)", relationInfo.getRelationName());
        Map<String, Object> r_params = new HashMap<>();
        r_params.put("pId", updNodeIdValue.toString());
        r_params.put("uId", relationInfo.getEndNodeId().toString());
        logger.info("CREATE / UPDATE relation "+ relationship + " with params " + r_params);
        tx.run(relationship, r_params);
    }

    private void updateProperties(Transaction tx, Map<String, Object> props, String updNodeLabel, String updNodeIdField, Object updNodeIdValue) {
        String update = String.format("MERGE (p:%s{%s:'%s'}) SET p += $props", updNodeLabel,
                updNodeIdField, updNodeIdValue);
        Map<String,Object> params = new HashMap<>();
        params.put( "props", props);
        logger.info("CREATE / UPDATE NODE "+ update + " with params " + props);
        tx.run(update, params);
    }

    public void delete(List<NodeUpdInfo> nodeUpdInfos) {
        try (Session session = driver.session()) {
            try(Transaction tx = session.beginTransaction() ) {
                for (NodeUpdInfo nodeUpdInfo: nodeUpdInfos) {
                    String updNodeLabel = nodeUpdInfo.getLabel();
                    String updNodeIdField = nodeUpdInfo.getIdField();
                    Object updNodeIdValue = nodeUpdInfo.getIdValue();

                    updateProperties(tx, nodeUpdInfo.getProps(), updNodeLabel, updNodeIdField, updNodeIdValue);
                }
                tx.commit();
            }

        } catch (Neo4jException ex) {
            logger.error(ex.toString());
        }
    }
}