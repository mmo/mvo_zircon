package multivio.org.dfst;

import java.util.Set;
import java.util.Arrays;

import org.w3c.dom.Document;

/**
 * StructureParser is a singleton. The goal of this class is to choose witch
 * parser to use
 * 
 * @author heritierc
 * 
 */
public class StructureParser {

	private static StructureParser instance;

	private StructureParser() {

	}

	public static synchronized StructureParser getInstance() {
		if (instance == null) {
			instance = new StructureParser();
		}
		return instance;
	}

	public String selectStrategy(Document doc) {
		// to do choose the rigth parser
		// today only dublinCore
		DublinCoreParser par = new DublinCoreParser();
		CoreDocumentModel cdm = par.parseDocument(doc);
		String res = writeCDM(cdm);
		return res;
	}

	public String writeCDM(CoreDocumentModel cdm) {
		//
		Set<String> keys = cdm.getCDM().keySet();
		StringBuffer myString = new StringBuffer();

		myString.append("{");
		String[] tl = keys.toArray(new String[keys.size()]);
		Arrays.sort(tl);
		for (int h = 0; h < keys.size(); h++) {
			String oneKey = tl[h].toString();
			Record oneRecord = cdm.getCDM().get(oneKey);
			myString.append("\"" + oneRecord.getId() + "\": {");
			myString.append(oneRecord.getRecordToString() + "}");
			if (h != keys.size() - 1) {
				myString.append(", ");
			}

		}
		myString.append("}");
		return myString.toString();
	}
}
