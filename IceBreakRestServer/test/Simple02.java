import IceBreakRestServer.*;
import java.io.IOException;

public class Simple02 {


  public static void main(String[] args) {

    IceBreakRestServer rest;

    try {
      rest = new IceBreakRestServer();
      // We can overrite the config file
      // rest.setPort(65000); // This is the default so I can leave this out
      // rest.setQueue(10);   // This is the default so I can leave this out

      while (true) {
        // Wait for the client to make a http request
        rest.getHttpRequest();

        // Now I will produce a HTML response
        rest.setContentType("text/html; charset=utf-8");
        rest.write("<html><body>");
        for (int i= 1 ; i < 10000 ; i ++) {
          rest.write("<p>looping: " + Integer.toString(i) + ":" + rest.now() + "</p>");
        }
        rest.write("</body></html>");

        // And I will controle when to send it back -  that is now
        rest.flush();
      }
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
