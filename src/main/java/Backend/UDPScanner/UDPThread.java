/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.UDPScanner;
import Backend.Resource.PortResult;
import Resource.ScanController;
import java.net.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author admin
 */
public class UDPThread implements Runnable {
    private int port;
    private String host;
    private List<PortResult> resultList;
    
    public UDPThread(int port, String host, List<PortResult> resultList){
        this.port = port;
        this.host = host;
        this.resultList = resultList;
    }
    
    public void run(){
        ScanController.checkPause();
        try{
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            
            byte[] sendData = "ping".getBytes();
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);
            
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            socket.receive(sendPacket);
            resultList.add(new PortResult(port, "OPEN"));
        } catch (SocketTimeoutException e){
            resultList.add(new PortResult(port, "NO RESPONSE"));
        } catch (Exception e){
            resultList.add(new PortResult(port, "CLOSE"));
        }
    }
}
