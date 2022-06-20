package atlassian;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Neo4jLoader {
    private final Driver driver;
    private final Logger logger;

    public Neo4jLoader() {
        this.logger  = LoggerFactory.getLogger(Neo4jLoader.class);
        this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "curse4"), Config.defaultConfig());
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

    private void createRelation(Transaction tx, String updNodeLabel, String updNodeIdField, Object updNodeIdValue, RelationInfo relationInfo) {
        String relationship = String.format("MATCH (p:%s), (u:%s) ", updNodeLabel,
                relationInfo.getEndNodeLabel());
        relationship += String.format("where p.%s = $pId AND u.%s= $uId ", updNodeIdField,
                relationInfo.getEndNodeIdField());
        relationship += String.format("CREATE (p)-[r:%s]->(u)", relationInfo.getRelationName());
        Map<String, Object> r_params = new HashMap<>();
        r_params.put("pId", updNodeIdValue);
        r_params.put("uId", relationInfo.getEndNodeId());
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
}
