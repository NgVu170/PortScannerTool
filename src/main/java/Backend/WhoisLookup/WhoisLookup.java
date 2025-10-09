/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.WhoisLookup;
import java.io.*;
import java.net.*;


/**
 *
 * @author admin
 */
public class WhoisLookup {
    public static String getWhois(String Domain){
        StringBuilder result = new StringBuilder();
        String WhoisServer = "whois.verisign-grs.com"; //for .com and .net
        try(Socket socket = new Socket(WhoisServer, 43)){
            OutputStream out = socket.getOutputStream();
            out.write((Domain + "\r\n").getBytes());
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                result.append(line).append("\n");
            }
        } catch (IOException e){
            result.append("Error: ").append(e.getMessage());
        }
        return result.toString();
    }
}
