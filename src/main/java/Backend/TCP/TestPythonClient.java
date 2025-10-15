/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCP;
import java.util.*;
/**
 *
 * @author admin
 */
public class TestPythonClient {
     public static void main(String[] args) throws Exception {
        PythonClient client = new PythonClient("127.0.0.1", 9000);

        Map<String, Object> job = new HashMap<>();
        job.put("target", "127.0.0.1");
        job.put("start", 75);
        job.put("end", 85);
        job.put("timeout", 0.5);
        job.put("workers", 20);

        client.startScan(job, (Map<String, Object> res) -> {
            System.out.println("=> " + res);
        });

        // Giữ chương trình không thoát ngay
        Thread.sleep(10000);
        client.stop();
    }
}
