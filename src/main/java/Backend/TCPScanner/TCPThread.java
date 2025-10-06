/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCPScanner;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author admin
 */
public class TCPThread implements Runnable{
    private int port;
    private String host;
    private List<PortResult> resultList;
    
    public TCPThread(int port, String host, List<PortResult> resultList){
        this.port = port;
        this.host = host;
        this.resultList = resultList;
    }
    
    public void run(){
        TCPScanController.checkPause();
        try(Socket socket = new Socket(host, port)){
            resultList.add(new PortResult(port, "Open"));
        } catch (IOException e){
            resultList.add(new PortResult(port, "Closed"));
        }
    }
}
