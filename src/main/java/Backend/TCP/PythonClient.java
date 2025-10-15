/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCP;

/**
 *
 * @author admin
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;
public class PythonClient {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public PythonClient(String host, int port){
        this.host = host;
        this.port = port;
    }
    
    public void startScan(Map<String,Object> job, Consumer <Map<String,Object>> onLine) throws IOException{
        socket = new Socket(host, port);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        
        String jobJson = mapper.writeValueAsString(job);
        writer.write(jobJson);
        writer.flush();
        socket.shutdownOutput();
        
        Thread t = new Thread(() ->{
           try{
               String line;
               while((line = reader.readLine()) != null){
                   if (line.trim().isEmpty()) continue;
                   try{
                       Map<String,Object> obj = mapper.readValue(line, Map.class);
                       onLine.accept(obj);
                   } catch (Exception ex){
                       System.err.println("Parse error: " + ex.getMessage() + " -line: "+ line);
                   }
               }
           } catch (IOException e){
               
           } finally{
               try { if (reader != null) reader.close(); } catch (IOException e) {} 
               try { if (writer != null) writer.close(); } catch (IOException e) {} 
               try { if (socket != null) socket.close(); } catch (IOException e) {}
           }
        }, "PythonClient-Reader");
        t.setDaemon(true);
        t.start();
    }
    
    public void stop(){
        try{
            if(socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e){
            
        }
    }
}
