/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.UDP;
import Backend.Resource.PortResult;
import java.net.*;
import java.io.*;
import java.util.concurrent.Callable;
/**
 *
 * @author admin
 */
public class UDPWorker implements Callable<PortResult>{
    private final String host;
    private final int port;
    private final int timeoutMs;

    public UDPWorker(String host, int port, int timeoutMs) {
        this.host = host;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }
    
    private String guessService(String banner) {
        if (banner == null) return null;
        String b = banner.toLowerCase();
        if (b.contains("dns")) return "dns";
        return null;
    }
    
    @Override
    public PortResult call(){
        PortResult result = new PortResult(host, port, "UDP");
        DatagramSocket socket = null;
        long start = System.currentTimeMillis();
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(timeoutMs);
            InetAddress addr = InetAddress.getByName(host);
            byte[] data = new byte[]{0};
            DatagramPacket sendPacket= new DatagramPacket(data, data.length, addr, port);
            socket.send(sendPacket);
            
            byte[] res = new byte[1024];
            DatagramPacket recievePacket = new DatagramPacket(res, res.length);
            socket.receive(recievePacket);
            String resMessage = new String (recievePacket.getData(), 
                    0, 
                    recievePacket.getLength(),
                    "UTF-8").trim();
            
            result.setState("Open");
            result.setBanner(resMessage);
            result.setService(guessService(resMessage));
        } catch (SocketTimeoutException e){
            result.setState("open|filtered");
            result.setError("timeout");
        } catch (PortUnreachableException e){
            result.setState("Close");
            result.setError("Port Unreachable");
        } catch (IOException e){
            result.setState("Filtered");
            result.setError(e.getMessage());
        } finally{
            long end = System.currentTimeMillis();
            result.setRttMs(end - start);
            if (socket != null) socket.close();
        }
        return result;
    }
}
