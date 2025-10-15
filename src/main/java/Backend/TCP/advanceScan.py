import socket
import json
import time
import concurrent.futures

HOST = "127.0.0.1"
PORT = 9000

def check_connect(target, port, timeout = 1.0):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(timeout)
    try:
        s.connect((target, port))
        s.close()
        return "Open"
    except socket.timeout:
        return "Timeout"
    except Exception:
        return "Closed_or_Filtered"
    
def handle_client(conn):
    try:
        data = b""
        while True:
            chunk = conn.recv(8192)
            if not chunk:
                break
            data += chunk
        if not data:
            conn.close()
            return
        
        job = json.loads(data.decode("utf-8"))
        target = job.get("target")
        ports = job.get("ports")
        if ports is None:
            start = int(job.get("start", 1))
            end = int(job.get("end", 1024))
            ports = list(range(start, end + 1))
        timeout = float(job.get("timeout", 1.0))
        workers = int(job.get("workers",min(100, max(4,len(ports)))))
        
        header ={"status":"started","target":target,"ports":ports,"count":len(ports)} 
        conn.sendall((json.dumps(header)+"\n").encode())
        
        #thread pool for scanning
        with concurrent.futures.ThreadPoolExecutor(max_workers=workers) as exe:
            futures = {exe.submit(check_connect,target,p,timeout): p for p in ports}
            for future in concurrent.futures.as_completed(futures):
                port = futures[future]
                try:
                    res = future.result()
                except Exception as e:
                    res = "Error: "+str(e)
                out = {"port": port, "result": res}
                
                try:
                    conn.sendall((json.dumps(out)+"\n").encode())
                except BrokenPipeError:
                    break
                
        conn.sendall((json.dumps({"status":"done"})+"\n").encode())
    except Exception as e:
        try:
            conn.sendall((json.dumps({"error": str(e)}) + "\n").encode())
        except Exception:
            pass
    finally:
        try:
            conn.close()
        except:
            pass
        
def main():
    serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    serv.bind((HOST, PORT))
    serv.listen(5)
    print(f"Connect-mode scanner server listening on {HOST}:{PORT}")
    try:
        while True:
            conn,addr = serv.accept()
            import threading
            t = threading.Thread(target=handle_client, args=(conn,), daemon=True)
            t.start()
    except KeyboardInterrupt:
        print("Shutting down server...")
    finally:
        serv.close()
        
if __name__ == "__main__":
    main()