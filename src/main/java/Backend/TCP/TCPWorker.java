package Backend.TCP;

import Backend.Resource.PortResult;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
    }

    private String guessServiceFallback(int port, String banner) {
        String service = guessService(banner);
        if (service != null) return service;

        switch (port) {
            case 80: return "http";
            case 443: return "https";
            case 22: return "ssh";
            case 53: return "dns";
            case 25: return "smtp";
            case 21: return "ftp";
            default: return "unknown";
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
            result.setState("open"); // standardized lower-case

            try{
                InputStream input = socket.getInputStream();
                byte[] bannerAuto = new byte[1024];
                int read = -1;
                try {
                    read = input.read(bannerAuto);
                } catch (SocketTimeoutException ste) {
                    // timed out reading banner - we'll treat as no banner
                    read = -1;
                }

                if (read > 0){
                    String banner = new String(bannerAuto, 0, read, StandardCharsets.UTF_8).trim();
                    result.setBanner(banner);
                    result.setService(guessServiceFallback(port, banner));
                } else{
                    if (port == 80) {
                        try {
                            OutputStream out = socket.getOutputStream();
                            out.write("HEAD / HTTP/1.0\r\n\r\n".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                            byte[] bannerHttp = new byte[1024];
                            int rlen = -1;
                            try {
                                rlen = input.read(bannerHttp);
                            } catch (SocketTimeoutException ste) {
                                rlen = -1;
                            }
                            if (rlen > 0) {
                                String banner2 = new String(bannerHttp, 0, rlen, StandardCharsets.UTF_8).trim();
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
