/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCP;
import Backend.Resource.PortResult;
import java.net.*;
import java.io.*;
import java.util.concurrent.Callable;
/**
 *
 * @author admin
 */
public class TCPWorker implements Callable<PortResult> {
    private final String host;
    private final int port;
    private final int timeoutMs;
    
    public TCPWorker(String host, int port, int timeoutMs){
        this.host = host;
        this.port = port;
        this.timeoutMs = timeoutMs;
    }
    
    private String guessService(String Banner){
        if (Banner == null) return null;
        String banner = Banner.toLowerCase();
        
        if (banner.contains("ssh")) return "ssh";
        if (banner.contains("smtp")) return "smtp";
        if (banner.contains("ftp")) return "ftp";
        if (banner.contains("http/") 
                || banner.startsWith("get ") 
                || banner.startsWith("post ")) return "http";
        if (banner.contains("mysql") 
                || banner.contains("mariadb")) return "mysql";
        return null;
    }Python: Select Interpreter
    
    private String guessServiceFallback(int port, String banner) {
        String service = guessService(banner);
        if (service != null) return service;

        switch (port) {
            case 80: return "HTTP";
            case 443: return "HTTPS";
            case 22: return "SSH";
            case 53: return "DNS";
            case 25: return "SMTP";
            case 21: return "FTP";
            default: return "Unknown";
        }
    }

    
    @Override
    public PortResult call(){
        PortResult result = new PortResult(host, port, "TCP");
        Socket socket = null;
        long start = System.currentTimeMillis();
        
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(Math.min(1000, Math.max(200, timeoutMs)));
            result.setState("Open");
            
            try{
                InputStream input = socket.getInputStream();
                byte[] bannerAuto = new byte[1024];
                int read = input.read(bannerAuto);
                if (read > 0){
                    String banner = new String (bannerAuto,0,read,"UTF-8").trim();
                    result.setBanner(banner);
                    result.setService(guessServiceFallback(port, banner));
                } else{
                    if (port == 80) {
                        try {
                            OutputStream out = socket.getOutputStream();
                            out.write("HEAD / HTTP/1.0\r\n\r\n".getBytes("UTF-8"));
                            out.flush();
                            byte[] bannerHttp = new byte[1024];
                            int rlen = input.read(bannerHttp);
                            if (rlen > 0) {
                                String banner2 = new String(bannerHttp, 0, rlen, "UTF-8").trim();
                                result.setBanner(banner2);
                                result.setService(guessServiceFallback(port, banner2));
                            } else {
                                result.setService(guessServiceFallback(port, null));
                            }
                        } catch (Exception ignore) {
                            result.setService(guessServiceFallback(port, null));
                        }
                    } else {
                        result.setService(guessServiceFallback(port, null));
                    }
                }
            } catch (SocketTimeoutException ste) {
                // no banner within short time
            } catch (IOException ioe) {
                // ignore banner reading errors
            }
        }catch (SocketTimeoutException e) {
            result.setState("filtered"); // timed out -> likely filtered
            result.setError("timeout");
        } catch (ConnectException e) {
            result.setState("closed");
            result.setError(e.getMessage());
        } catch (IOException e) {
            result.setState("filtered");
            result.setError(e.getMessage());
        } finally {
            long end = System.currentTimeMillis();
            result.setRttMs(end - start);
            if (socket != null) {
                try { socket.close(); } catch (IOException ignore) {}
            }
        }
        return result;
    }
}
