package atlassian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeUpdInfo {
    private final String label;
    private final String idField;
    private final Object idValue;

    private Map<String, Object> props;
    private List<RelationInfo> rels;

    public NodeUpdInfo(String label, String idField, Object idValue) {
        this.label = label;
        this.idField = idField;
        this.idValue = idValue;
        this.props = new HashMap<>();
        this.rels = new ArrayList<>();
    }

    public void addProperty(String fieldName, Object value) {
        if (value != null && value != "null") {
            props.put(fieldName, value);
        }
    }

    public void addRelation(RelationInfo relationInfo) {
        rels.add(relationInfo);
    }

    public List<RelationInfo> getRels() {
        return rels;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public Object getIdValue() {
        return idValue;
    }

    public String getIdField() {
        return idField;
    }

    public String getLabel() {
        return label;
    }
}
