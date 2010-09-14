/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IceBreakRestServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.URL.*;
import java.net.URLDecoder;
import java.net.MalformedURLException;



/**
 *
 * @author nli
 */
public class IceBreakRestServer {

  private ServerSocket providerSocket = null;
  private Socket connection = null;
  private PrintWriter pw;
  private String ContentType;
  private String Status;
  private StringBuilder resp  = new StringBuilder(1024);
  private Boolean doFlush = false;
  private int Port ;
  private int Queue ;
  private InputStream in;
  public  String request;
  public  String payload;
  public  String method ;
  public  String queryStr;
  public  String resource;
  public  String httpVer;

  public  Map<String, String> header = new HashMap<String, String>();
  public  Map<String, String> parms  = new HashMap<String, String>();

  private void loadProps ( ) {
    Properties prop = new Properties();

    try {
      //load a properties file
      prop.load(new FileInputStream("config.properties"));
      Port  = Integer.parseInt(prop.getProperty("restserver.port","65000"));
      Queue = Integer.parseInt(prop.getProperty("restserver.queuesize", "10"));
    } catch (IOException ex) {
      // ex.printStackTrace();
    }
  }

  public IceBreakRestServer() {
    loadProps ();
  }

  public void setContentType(String s) {
    ContentType = s;
  }
  public void setStatus(String s) {
    Status = s;
  }

  public void setPort(int port) {
    Port = port;
  }

  public  void setQueue(int queue) {
     Queue = queue;
  }

  public  String getQuery(String Key , String Default) {
     String temp = parms.get(Key);
     if (temp == null) return Default;
     return temp;
  }

  public  String getQuery(String Key) {
     return parms.get(Key);
  }

  public String now () {
    String s;
    Format formatter;
    Date date = new Date();
    formatter = new SimpleDateFormat("hh:mm:ss");
    s = formatter.format(date);
    return s;
  }

  public static Map<String, String> getQueryMap(String query) {  
     String[] params = query.split("&");  
     Map<String, String> map = new HashMap<String, String>();  
     for (String param : params)  {  
       int p = param.indexOf('=');
       if (p >= 0) {
         String name = param.substring( 0, p);
         String value = param.substring( p+1);
         String s = URLDecoder.decode(value);
         map.put(name, s);
       }
     }  
     return map;  
 }
 // This handles bothe windows <CR><LF> and mac/aix/linux <CR>
 // and returns both end of header and end of line sequence
 private int isEol(byte [] buf , int i) {
   if (buf[i] == 0x0d &&buf[i+1] == 0x0a) {
     if (buf[i+2] == 0x0d &&buf[i+3] == 0x0a) {
       return -4; // End Of header
     }
     return 2;
   }
   if (buf[i] == 0x0d ) {
     if (buf[i+1] == 0x0d ) {
       return -2; // End Of header
     }
     return 1;
   }
   if (buf[i] == 0x0a ) {
     if (buf[i+1] == 0x0a ) {
       return -2; // End Of header
     }
     return 1;
   }
   return 0;
}


 private void unpackRequest() throws IOException {

    byte buf [] = new byte[4096];
    in = connection.getInputStream();
    int read = in.read(buf);
    int len =0, pos =0, eol=0;
    header.clear();
    parms.clear();
    request = payload = method = queryStr = httpVer = resource = null;
    for (int i = 0; i < read && eol >= 0; i++) {
       eol  = isEol(buf , i);
       if (eol > 0) {
         // First line is the request. Now parse that partial
         if (request == null) {
            request =  new String(buf, pos  , len);
            String [] temp = request.split(" ");
            method = temp[0];
            queryStr = temp[1];
            httpVer = temp[2];
            int p = queryStr.indexOf('?');
            if (p>=0) {
              resource = queryStr.substring( 0, p);
              parms = getQueryMap(queryStr.substring( p+1));
            } else {
              resource = queryStr;
            }
         } else {
            String param  =  new String(buf, pos  , len);
            int p = param.indexOf(':');
            String name = param.substring( 0, p);
            String value = param.substring( p+1);
            header.put(name, value.trim());
         }
         len = 0;
         pos = i + eol;
         i+=eol-1;
       } else if (eol < 0) {
         pos = i + (-eol);
         payload = new String(buf, pos , read - pos);
       } else {
         len ++;
       }
    }
    
    System.out.println("resource: " + request);
    System.out.println("method: " + method);
    System.out.println("resource: " + resource);
    System.out.println("queryStr: " + queryStr);
    System.out.println("httpVer: " + httpVer);
    System.out.println("header  : " + header   );
    System.out.println("parms : " + parms  );

    /*
    // String query = url.getQuery();
    Map<String, String> map = getQueryMap(query);
    Set<String> keys = map.keySet();
    for (String key : keys)
    {
       System.out.println("Name=" + key);
       System.out.println("Value=" + map.get(key));
    }

    */

  }

  void sendResponse () {

    pw.print("HTTP/1.1 " + Status + "\r\n" +
             "Connection: Keep-Alive\r\n" +
             "Accept: multipart/form-data\r\n"+
             "Accept-Encoding: multipart/form-data\r\n" +
             "Server: IceBreak Java Services\r\n" +
             "cache-control: no-store\r\n" +
             "Content-Length: " + Integer.toString(resp.length()) + "\r\n" +
             "Content-Type: " + ContentType + "\r\n" +
             "\r\n" + resp.toString());
    pw.flush();
  }

  public void getHttpRequest () throws IOException {
    if (providerSocket == null) {
      providerSocket = new ServerSocket(Port , Queue);
    }
    if (doFlush) flush();

    connection = providerSocket.accept();
    pw = new PrintWriter(connection.getOutputStream());
    resp.setLength(0);
    unpackRequest();
    ContentType = "text/plain; charset=utf-8";
    Status = "200 OK";
    doFlush = true;
  }

  public void write(String s) {
    resp.append(s);
  }

  public void flush() throws IOException {
    sendResponse ();
    connection.close();
    doFlush = false;
  }


}



