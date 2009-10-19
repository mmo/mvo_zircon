package multivio.org.error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Logger {
	
	public String add (HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream in = request.getInputStream();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("../webapps/zircon/log.txt", true));
			byte[] buffer = new byte[2048];
			int read = in.read(buffer);
			String st = new String();
			while (read != -1) {
				st = new String(buffer, 0, read);
				out.write(st);
				read = in.read(buffer);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		in.close();
		return new String("one log has been added");
	}
}
