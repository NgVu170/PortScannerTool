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
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean isScanning = new AtomicBoolean(true);
    
    public ScanPort(int start, int end, String host, String protocol, int PoolSize, int Timeout, AtomicBoolean isScanning){
        this.startPort = start;
        this.endPort = end;
        this.host = host;
        this.protocol = protocol;
        this.poolSize = PoolSize;
        this.timeoutMs = Timeout;
        this.isScanning = isScanning;
    }
    public List<PortResult> ScanProcess() throws IOException, InterruptedException, ExecutionException {
        result = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        CompletionService<PortResult> servicePool= new ExecutorCompletionService<>(pool);
        
        int task = 0;
        for (int port = startPort; port < endPort; port++) {
            if (!isScanning.get()) break;

            if (protocol.equals("TCP") || protocol.equals("Both")) {
                servicePool.submit(new TCPWorker(host, port, timeoutMs));
                task++;
            }
            if (protocol.equals("UDP") || protocol.equals("Both")) {
                servicePool.submit(new UDPWorker(host, port, timeoutMs));
                task++;
            }
        }

        
        for(int i = 0; i < task; i++){
            Future<PortResult> run = servicePool.take();
            PortResult temp = run.get();
            result.add(temp);
        }
        return result;
    }
}
