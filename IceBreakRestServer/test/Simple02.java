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
        rest.setContentType("text/html");
        rest.write("<html><body><p>OK</p>");
        rest.write("<p>" + rest.now() + "</p>");
        for (int i= 1 ; i < 10000 ; i ++) {
          rest.write("lopping: " + Integer.toString(i) + ":" + rest.now() + "<br>");
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
