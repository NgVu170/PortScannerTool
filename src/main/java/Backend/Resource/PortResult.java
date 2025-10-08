/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.Resource;

/**
 *
 * @author admin
 */
public class PortResult {
    private String host;
    private int port;
    private String protocol; //tcp, udp
    private String state; //open, closed, filtered, open|filtered
    private String service; // guess service base port
    private String banner;
    private long rttMs;
    private String error;
    private long timestamp;
    
    public PortResult(String target, int port, String protocol){
        this.host = target;
        this.port = port;
        this.protocol = protocol;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getHost(){ return host;}
    
    public int getPort() {return port;}
    
    public String getState() {return state;}
    public void setState(String state) {this.state = state;}
    
    public String getService(){return service;}
    public void setService(String service) {this.service = service;}
    
    public String getBanner(){return banner;}
    public void setBanner(String banner) {this.banner = banner;}
    
    public long getRttMs(){return this.rttMs;}
    public void setRttMs(long rttms) {this.rttMs = rttms;}
    
    public String getError(){return error;}
    public void setError(String errorType) {this.error = errorType;}
    
    public long getTimestam(){return timestamp;}
}
