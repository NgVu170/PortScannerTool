/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.ScanRange;

import Backend.Resource.PortResult;
import Backend.TCP.TCPWorker;
import Backend.UDP.UDPWorker;
import java.util.*;
import java.util.concurrent.*;
/**
 *
 * @author admin
 */
public class ScanIpRange {
    private final long ipStart;
    private final long ipEnd;
    private final String protocol;
    
    private List<PortResult> resultOneIp;
    
    
    public ScanIpRange(long ipStart, long ipEnd, String protocol){
        this.ipStart = ipStart;
        this.ipEnd = ipEnd;
        this.protocol = protocol;
    }
    
    public List<List<PortResult>> ScanInRange(){
        List<List<PortResult>> resultOfAll = new ArrayList<List<PortResult>>();
        
        for (long ip = ipStart; ip <= ipEnd; ip++) {
        try {
                List<PortResult> resultOneIp = scanIp(ip, protocol);
                resultOfAll.add(resultOneIp);
            } catch (Exception e) {
                System.err.println("Error scanning IP: " + ip + " - " + e.getMessage());
                resultOfAll.add(Collections.emptyList()); // hoặc bỏ qua tùy ý
            }
        }
        return resultOfAll;
    }
    
    private List<PortResult> scanIp(long ip, String protocol) throws InterruptedException, ExecutionException{
        List<PortResult> results = new ArrayList<>();
        String ipString = longToIp(ip);       
        List<Integer>portsToScan = TopPort.topPortsList();
        
        ExecutorService pool = Executors.newFixedThreadPool(14);
        CompletionService<PortResult> servicePool = new ExecutorCompletionService<>(pool);
        
        int taskCount = 0;
        for (int port : portsToScan) {
            if (protocol.equals("tcp") || protocol.equals("both")) {
                servicePool.submit(new TCPWorker(ipString, port, 300));
                taskCount++;
            }
            if (protocol.equals("udp") || protocol.equals("both")) {
                servicePool.submit(new UDPWorker(ipString, port, 300));
                taskCount++;
            }   
        }

        for (int i = 0; i < taskCount; i++) {
            Future<PortResult> future = servicePool.take();
            PortResult result = future.get();
            results.add(result);
        }
        
        pool.shutdown();
        return results;
    }
    
    private String longToIp(long ip) {
    return String.format("%d.%d.%d.%d",
        (ip >> 24) & 0xFF,
        (ip >> 16) & 0xFF,
        (ip >> 8) & 0xFF,
        ip & 0xFF);
}

}
