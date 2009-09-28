package multivio.org.communication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import multivio.org.dfst.StructureParser;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ServerDocument {

	private static ServerDocument instance;

	private ServerDocument() {}

	public static synchronized ServerDocument getInstance() {
		if (instance == null) {
			instance = new ServerDocument();
		}
		return instance;
	}

	public String getMetadataDocument(String urlDocument) {
		String jsonResult = null;
		try {
			URL url = new URL(urlDocument);
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = parser.parse(url.openConnection().getInputStream());
			jsonResult = StructureParser.getInstance().selectStrategy(doc);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return jsonResult;
	}

	public void getPDFFile(String fileName) {
		// to do call the pdf file and return it
		/*
		 * URL url = new URL(this.documentServerAddr+fileName); String type =
		 * url.openConnection().getContentType(); int val =
		 * url.openConnection().getContentLength();
		 */
	}
	
	public void loadImage (String imageUrl, String imageName) {
		//TO DO: after loading images must be resize (thumbnails?) 
		try {
			boolean exists = (new File("../webapps/zircon/images")).exists();
			if (!exists) {
				System.out.println("dir not exist");
				Process pr = Runtime.getRuntime().exec("mkdir ../webapps/zircon/images");
			}
			URL url = new URL(imageUrl);
			String type = url.openConnection().getContentType(); 
			int val = url.openConnection().getContentLength();
			RandomAccessFile f =  new RandomAccessFile("../webapps/zircon/images/"+imageName, "rw"); 
			InputStream in = url.openStream();
			byte[] buffer = new byte[2048];
			int read  = in.read(buffer);
			while(read != -1){
				f.write(buffer, 0, read );
				read  = in.read(buffer);
			}
			f.close();
			in.close();
			// to do create thumbnails
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
