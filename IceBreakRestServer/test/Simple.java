import IceBreakRestServer.*;
import java.io.IOException;

public class Simple {

  public static void main(String[] args) {

    IceBreakRestServer rest;

    try { 
      rest  = new IceBreakRestServer();

      while (true) {
        rest.getHttpRequest();
        rest.write("Hello world - time is :" + rest.now() );
      }
    }
    catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}

