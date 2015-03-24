IceBreak REST server is a tiny stand alone server side HTTP stack you can embed in your JAVA program. Just drop one small jar file into you project and you are golden.

Sometimes you just need to be able to get request and send response back by the HTTP protocol. In these cases it will be totally overkill to use i.e. WebSphere, Tomcat or Glassfish server. What you need is a small Representational State Transfer server - a REST server http://en.wikipedia.org/wiki/Representational_State_Transfer

# The aim #
The aim is to utilize Java classes server side i.e. into PHP, CGI etc. simply by issuing server side HTTP request on the loop-back port, and their by gaining performance since the class loader is only invoke once - you don't have to jump in and out of the java environment.

# Where to use #
This REST server works well especially in mixed language environments. If cases where it to much of an overhead to enter and leave the Java environment repeatedly.

It was designed to run on the IBMi/iSeries/i5/AS/400 platform, where the overhead of entering and leaving Java has a performance penalty. The "price" of doing a RPG "HttpRequest" on the loopback interface and wait for a service to respond is cheap. Your requests are serialized to one single server - just like coding against a dataqueue.

Also for server-to-server communication in mixed environments where you want IBMi data into a ASP.NET application on IIS - this tiny server might be a great solution.

We have successfully used this tiny server for incorporate XMLsec, iText,  NemID among other java projects into a non-Java project like IceBreak-RPG ,  C#, RPG-CGI, PHP and COBOL applications.


The following code illustrates how easy it is to use:

```
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
```

This project "IceBreak REST server for Java" is only a tiny tool used in the professional IceBreak package for IBMi.

read more on http://icebreak-community.com
