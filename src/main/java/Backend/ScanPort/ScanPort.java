/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.ScanPort;

import Backend.Resource.PortResult;
import Backend.TCP.TCPWorker;
import Backend.UDP.UDPWorker;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

/**
 *
 * @author admin
 */
public class ScanPort {
    private final int startPort;
    private final int endPort;
    private final String host;
    private final String protocol;
    private List<PortResult> result;
    private final int poolSize;
    private final int timeoutMs;
    
    public ScanPort(int start, int end, String host, String protocol, int PoolSize, int Timeout){
        this.startPort = start;
        this.endPort = end;
        this.host = host;
        this.protocol = protocol;
        this.poolSize = PoolSize;
        this.timeoutMs = Timeout;
    }
    public List<PortResult> ScanProcess(){
        result = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        CompletionService<PortResult> servicePool= new ExecutorCompletionService<PortResult>(pool);
        
        int task = 0;
        for( int port = startPort; port < endPort; port++){
            if(protocol.equals("tcp") || protocol.equals("both")){
                servicePool.submit(new TCPWorker(host, port, timeoutMs));
                task++;
            } else if(protocol.equals("udp") || protocol.equals("both")){
                servicePool.submit(new UDPWorker(host, port, timeoutMs));
                task++;
            }
        }
        
        for(int i = 0; i < task; i++){
            try{
                Future<PortResult> run = servicePool.take();
                PortResult temp = run.get();
                result.add(temp);
            }catch (Exception e) {
                System.err.println("Task error: " + e.getMessage());
            }
        }
        return result;
    }
}
