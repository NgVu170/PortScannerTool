/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCPScanner;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author admin
 */
public class TCPScan {
    public static List<PortResult> Scan (String host) throws InterruptedException{
        int startPort = 1;
        int endPort = 65535;
        int poolSize = 200;
        
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<PortResult> results = Collections.synchronizedList(new ArrayList<>());
        
        for(int port = startPort; port <= endPort; port++){
            executor.submit(new TCPThread(port, host, results));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        System.out.println("Scan complete");
        
        return results;
    }
}
