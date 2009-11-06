package multivio.org.error;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.*;

/**
 * 
 * This class reads information from the message sent to the server and extracts
 * and saves in a file the relevant fields such as: user name, level, message,
 * user’s message, etc. Moreover, it adds a timestamp referred to server-side
 * time.
 * 
 * @author FCA
 * @version {0.1.0}
 */

public class Logger {
  
  private static final String FILENAME = "../webapps/zircon/log.txt";
  /* keywords to search for in the message */
  private static final String[] KEYTAB = { "level", "\"message", "\'message", "useragent", "custom message" };
  private static final String regex = "[\"\',]";

  public String add (HttpServletRequest request, HttpServletResponse response) throws IOException {
    Pattern p;
    Matcher m;
    String line, tmpLine = "";
    int contentLen, length = 0;

    String clientName = request.getParameter("ClientName");
    if (clientName != null) {
      writeLog("\nClient Name: " + clientName + "\n");
    }

    Calendar calendar = new GregorianCalendar();
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int minutes = calendar.get(Calendar.MINUTE);
    int seconds = calendar.get(Calendar.SECOND);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    /* 1 is necessary to have a correct visualization */
    int moth = calendar.get(Calendar.MONTH) + 1;
    int year = calendar.get(Calendar.YEAR);
    String time = day + "/" + moth + "/" + year + " " + hours + ":" + minutes + ":" + seconds;
    if (time != null) writeLog(time + "\n");

    contentLen = request.getContentLength();
    if (contentLen > 0) {
      byte buffer[] = new byte[contentLen];
      length = request.getInputStream().readLine(buffer, 0, buffer.length);
      while (length > 0) {
        line = new String(buffer, 0, length);

        for (int i = 0; i < KEYTAB.length; i++) {
          p = Pattern.compile(KEYTAB[i]);
          m = p.matcher(line);
          if (m.find()) {
            tmpLine = line.substring(m.end());
            // Check if the pattern is "\'message"
            line = (i == 2) ? "EXCEPTION TYPE" + tmpLine :
                KEYTAB[i].toUpperCase() + tmpLine;
            line = line.replaceAll(regex, "");
            writeLog(line);
            break;
          }
        }
        buffer = new byte[contentLen];
        length = request.getInputStream().readLine(buffer, 0, buffer.length);
      }
    }
    return new String("one log has been added");
  }

  /**
   * writeLog writes the log information in a .txt file.
   * 
   * @param value information to be add in the log file.
   */
  private void writeLog (String value) {
    PrintWriter p;
    try {
      // Open Stream writer
      p = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME, true)));
      // Write File
      p.print(value);
      // Close File
      p.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
