// Drop this jar-file into you project
import IceBreakRestServer.*;
import java.io.IOException;

public class Simple {

  public static void main(String[] args) {

    // Declare the IceBreak HTTP REST server class
    IceBreakRestServer rest;

    try {

      // Instantiate it once
      rest  = new IceBreakRestServer();

      while (true) {

        // Now wait for any HTTP request
        // the "config.properties" file contains the port we are listening on
        rest.getHttpRequest();


        // If we reach this point, we have received a request
        // now we can pull out the parameters from the query-string
        // if not found we return the default "N/A"
        String name = rest.getQuery("name", "N/A");

        // we can now produce the response back to the client.
        // That might be XML, HTML, JSON or just plain text like here:
        rest.write("Hello world - the 'name' parameter is: " + name );
      }
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}