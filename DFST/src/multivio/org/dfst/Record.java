package multivio.org.dfst;

import java.util.HashMap;
import java.util.LinkedList;

/*
 * Record is a bean 
 */
public class Record {

	private String id;
	private LinkedList<String> parentId;
	private String nextId;
	private String previousId;
	private LinkedList<String> children;
	private String label;
	private HashMap<String, Object> meta;
	private int sequenceNumber;
	private String defaultUrl;

	public Record(String id) {
		this.id = id;
		this.parentId = new LinkedList<String>();
		this.children = new LinkedList<String>();
		this.meta = new HashMap<String, Object>();
	}

	public String getId() {
		return id;
	}

	public LinkedList<String> getParentId() {
		return parentId;
	}

	public void setParentId(LinkedList<String> parentId) {
		this.parentId = parentId;
	}

	public String getNextId() {
		return nextId;
	}

	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	public String getPreviousId() {
		return previousId;
	}

	public void setPreviousId(String previousId) {
		this.previousId = previousId;
	}

	public LinkedList<String> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<String> children) {
		this.children = children;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public HashMap<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(HashMap<String, Object> descriptiveMetadata) {
		this.meta = descriptiveMetadata;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public String getRecordToString() {
		StringBuffer st = new StringBuffer();
		st.append("\"guid\": \"" + this.getId() + "\", ");
		st.append("\"parentId\": [\"" + this.parentId.get(0) + "\"], ");
		st.append("\"nextId\": \"" + this.nextId + "\", ");
		st.append("\"previousId\": \"" + this.previousId + "\", ");
		if (this.children.size() != 0) {
			st.append("\"children\": [");
			for (int i = 0; i < children.size(); i++) {
				st.append("\"" + children.get(i) + "\" ");
				if (i != children.size() - 1)
					st.append(", ");
			}
			st.append("], ");
		}
		if(label != null){
			if(!meta.isEmpty()){
				st.append("\"label\": \"" + this.label + "\", ");
			}else{	
				st.append("\"label\": \"" + this.label + "\" ");
			}
		}
		if (!meta.isEmpty()) {
			st.append("\"metadata\": {");
			for (String key : meta.keySet()) {
				st.append("\"" + key + "\": \"" + meta.get(key) + "\"");
				if (!key.equals("language"))
					st.append(", ");
			}
			st.append("}");
		}
		if (sequenceNumber != 0) {
			st.append("\"sequenceNumber\": " + this.sequenceNumber + ", ");
			st.append("\"urlDefault\": \"" + this.defaultUrl + "\"");
		}
		return st.toString();
	}

}
