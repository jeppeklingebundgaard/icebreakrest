import IceBreakRestServer.*;
import java.io.IOException;

public class Simple03 {

  public static void main(String[] args) {

    IceBreakRestServer rest;

    try { 
      rest  = new IceBreakRestServer();

      while (true) {
        rest.getHttpRequest();
        rest.setContentType("text/html; charset=utf-8");
        rest.write("<html><body>");
        rest.write("<p>" + rest.request  + "</p>");
        rest.write("<p>" + rest.header   + "</p>");
        rest.write("<p>" + rest.payload   + "</p>");
        rest.write("<p>Value of a :" + rest.getQuery("a")   + "</p>");
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

