package multivio.org.dfst;

import java.util.HashMap;
import java.util.LinkedList;

import multivio.org.communication.ServerDocument;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DublinCoreParser implements ParserInterface {

	// private String parserId;
	private CoreDocumentModel cdm;
	private int localId;
	private int pageNumber;

	public DublinCoreParser() {
		// this.parserId = "DublinCore";
		this.cdm = new CoreDocumentModel();
		this.localId = 0;
		this.pageNumber = 0;
	}

	public String generateID (){
		String recordId;
		localId++;
		if(localId < 10)
			recordId = "n0000" + Integer.toString(localId);	
		else if(9 < localId && localId < 100)
			recordId = "n000" + Integer.toString(localId);
		else if(99 < localId && localId < 1000)
			recordId = "n00" + Integer.toString(localId);
		else
			recordId = "undefined";
		return recordId;
	}
	
	public int generateSequenceNumber() {
		pageNumber++;
		System.out.println("pageNumber "+ pageNumber);
		return pageNumber;
	}
		
	
	public CoreDocumentModel parseDocument(Document doc) {
		//first create the root node with metadata
		NodeList listOfFile = doc.getElementsByTagName("dc:identifier");
		int nbOfFile = listOfFile.getLength();
		String documentTitle = doc.getElementsByTagName("dc:title").item(0)
		.getTextContent();
		Record root = new Record(generateID());
		root.setLabel(documentTitle);
		HashMap<String, Object> descriptiveMetadata = new HashMap<String, Object>();
		descriptiveMetadata.put("title", documentTitle);
		descriptiveMetadata.put("language", 
								doc.getElementsByTagName("dc:language").item(0).getTextContent());
		NodeList creators = doc.getElementsByTagName("dc:creator");
		if (creators.getLength() > 0) {
			LinkedList<String> creatorNames = new LinkedList<String>();
			for (int i = 0; i < creators.getLength(); i++) {
				creatorNames.add(creators.item(i).getTextContent());
			}
			descriptiveMetadata.put("creator", creatorNames);
		}
		NodeList contributors = doc.getElementsByTagName("dc:contributor");
		if (contributors.getLength() > 0) {
			LinkedList<String> contributorNames = new LinkedList<String>();
			for (int i = 0; i < contributors.getLength(); i++) {
				contributorNames.add(contributors.item(i).getTextContent());
			}
			descriptiveMetadata.put("contributor", contributorNames);
		}
		root.setMeta(descriptiveMetadata);
		LinkedList<String> parentId = new LinkedList<String>();
		parentId.add("undefined");
		root.setParentId(parentId);
		root.setPreviousId("undefined");

		LinkedList<String> children = new LinkedList<String>();
		Record leafNode = null;
		String previousNumber = root.getId();
		//then for all files create two nodes: one with the label on the child with the url
		for (int j = 0; j < nbOfFile; j++) {
			Record labelNode = new Record(generateID());
			//set the nextid of the root node
			if(j == 0){
				root.setNextId(labelNode.getId());
			}
			parentId = new LinkedList<String>();
			parentId.add(root.getId());
			labelNode.setParentId(parentId);
			children.add(labelNode.getId());
			labelNode.setPreviousId(previousNumber);
			previousNumber = labelNode.getId();
			int localNumber = generateSequenceNumber();
			labelNode.setLabel("[" + Integer.toString(localNumber) + "]");
				
			String fileUrl = listOfFile.item(j).getTextContent();
			
			if(leafNode != null){
				leafNode.setNextId(labelNode.getId());
				this.cdm.addRecord(leafNode);
			}
			leafNode = new Record(generateID());
			parentId = new LinkedList<String>();
			parentId.add(labelNode.getId());
			leafNode.setParentId(parentId);
			leafNode.setPreviousId(previousNumber);
			previousNumber = leafNode.getId();
			labelNode.setNextId(leafNode.getId());
			LinkedList<String> childs = new LinkedList<String>();
			childs.add(leafNode.getId());
			labelNode.setChildren(childs);
			
			if(fileUrl.endsWith(".pdf")){
				//to do load pdf file and create records
				leafNode.setDefaultUrl(fileUrl);
			}
			if(fileUrl.endsWith(".gif")||fileUrl.endsWith("jpg")){
				//load image(s) into images dir
				String defaultName = fileUrl.substring(fileUrl.lastIndexOf("/"));
				ServerDocument server = new ServerDocument();
				server.loadImage(fileUrl, defaultName);
				leafNode.setDefaultUrl("http://localhost:8080/zircon/images"+defaultName);
			}
			
			leafNode.setSequenceNumber(localNumber);
				
			if (j == nbOfFile - 1) {
				leafNode.setNextId("undefined");
				this.cdm.addRecord(leafNode);
			}
			
			this.cdm.addRecord(labelNode);
		}
		root.setChildren(children);

		this.cdm.addRecord(root);
		return this.cdm;
	}
	
	public void parsePDF(String fileURL) {
		ServerDocument serverDoc = new ServerDocument();
		serverDoc.getPDFFile(fileURL);
		// to do create the cdm node for this pdf
	}

}
