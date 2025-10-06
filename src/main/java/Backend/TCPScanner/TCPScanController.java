/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend.TCPScanner;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author admin
 */
public class TCPScanController {
    public static volatile boolean isRunning = true;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition resumeCondition = lock.newCondition();
    public static volatile boolean isPaused = false;
    
    public static void checkPause(){
        lock.lock();
        try{
            while(isPaused){
                resumeCondition.await();
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        } finally{
            lock.unlock();
        }    
    }
    
    public static void pause(){
        isPaused = true;
    }
    public static void resume(){
        lock.lock();
        try{
            isPaused = false;
            resumeCondition.signalAll();
        } finally{
            lock.unlock();
        }
    }
}
