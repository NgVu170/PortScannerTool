/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCPScanner;

/**
 *
 * @author admin
 */
public class PortResult {
    private int port;
    private String status;
    
    public PortResult(int port, String status){
        this.port = port;
        this.status = status;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getStatus(){
        return status;
    }
}
