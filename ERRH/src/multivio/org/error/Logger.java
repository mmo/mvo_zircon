package multivio.org.error;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.http.*;

/**
 *
 * @class Logger.java reads information from the message sent to the server and extracts
 *               and saves in a file the relevant fields such as: client ip address, level, message,
 *               user’s message, etc. Moreover, it adds a timestamp referred to server-side time.
 * @author FCA
 * @ version {0.9.0}
 *
 */
public class Logger {
  public final String FILENAME = "../webapps/zircon/log.txt";
  public String add (HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String message = "";
    int length = 1;    
    
    String clientIP = request.getRemoteAddr() ;
    if (clientIP != null) {
    message += "\nClientIP: " + clientIP + "\n";
    }
  
    Calendar calendar = new GregorianCalendar();
    String time = calendar.getTime().toString();
    message += time + "\n";
    
    int contentLen = request.getContentLength();
    if (contentLen > 0) {
      while (length > 0) {
        byte buffer[] = new byte[2048];
        length = request.getInputStream().readLine (buffer, 0, buffer.length);
        if (length > 0) {
          message += new String (buffer, 0, length);
        } 
      }
    }
    writeLog(message + "\n");
    return new String ("one log has been added");
  }
 
  /**
   * @method 
   * writeLog writes the log information in a .txt file.
   * @param value information to be add in the log file.
   *
   */
  private void writeLog (String value) {
    PrintWriter p;
    try {
      // Open Stream writer
      p = new PrintWriter (new BufferedWriter (new FileWriter (FILENAME, true)));
      // Write File
      p.print (value);
      // Close File
      p.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
