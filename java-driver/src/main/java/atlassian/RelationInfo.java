package atlassian;

public class RelationInfo {
    private final String endNodeLabel;
    private final String endNodeIdField;
    private final Object endNodeId;

    private final String relationName;

    public RelationInfo(String endNodeLabel, String endNodeIdField, Object endNodeId, String relationName) {
        this.endNodeLabel = endNodeLabel;
        this.endNodeIdField = endNodeIdField;
        this.endNodeId = endNodeId;
        this.relationName = relationName;
    }

    public String getEndNodeLabel() {
        return endNodeLabel;
    }

    public String getEndNodeIdField() {
        return endNodeIdField;
    }

    public Object getEndNodeId() {
        return endNodeId;
    }

    public String getRelationName() {
        return relationName;
    }
}
