package com.airbusds.idea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.config.IDEAConfig;

public class ApplicationInstanceManager {

	private static Logger log = LogManager.getLogger(ApplicationInstanceManager.class.getName());
	
    private static ApplicationInstanceListener subListener;

    /** Randomly chosen, but static, high socket number */
    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;

    /** Must end with newline */
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewIDEAInstance$$\n";

    /**
     * Registers this instance of the application.
     * 
     * @return true if first instance, false if not.
     */
    public static boolean registerInstance() {
        // returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
        boolean returnValueOnError = true;
        // try to open network socket
        // if success, listen to socket for new instance message, return true
        // if unable to open, connect to existing and send new instance message, return false
        try {
            
        	InetAddress localAddress = null;
        	try{
        		localAddress = InetAddress.getLocalHost();
        		boolean reachable = localAddress.isReachable(10000);
        		log.debug("InetAddress.getLocalHost = "+localAddress.getHostName());
        		if(!reachable){
        			String hostName = IDEAConfig.getInstance().getProperty("localhostname");
        			localAddress = InetAddress.getByName(hostName);
            		log.debug("InetAddress.getByName = "+localAddress.getHostName());
        		}
        	}catch(Throwable t){}
        	
//        	if(localAddress == null){
//        		String hostName = IDEAConfig.getInstance().getProperty("localhostname");
//        		localAddress = InetAddress.getByName(hostName);
//        		log.debug("InetAddress.getByName() = "+localAddress.getHostName());
//        	}
        	
        	@SuppressWarnings("resource")
			final ServerSocket socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET, 10, localAddress);
            log.debug("Listening for application instances on socket " + SINGLE_INSTANCE_NETWORK_SOCKET);
            Thread instanceListenerThread = new Thread(new Runnable() {
                public void run() {
                    boolean socketClosed = false;
                    while (!socketClosed) {
                        if (socket.isClosed()) {
                            socketClosed = true;
                        } else {
                            try {
                                Socket client = socket.accept();
                                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                String message = in.readLine();
                                if (SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim())) {
                                    log.debug("Shared key matched - new application instance found");
                                    fireNewInstance();
                                }
                                in.close();
                                client.close();
                            } catch (IOException e) {
                                socketClosed = true;
                            }
                        }
                    }
                }
            });
            instanceListenerThread.start();
            // listen
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            return returnValueOnError;
        } catch (IOException e) {
            log.debug("Port is already taken.  Notifying first instance.");
            try {
                Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_NETWORK_SOCKET);
                OutputStream out = clientSocket.getOutputStream();
                out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
                out.close();
                clientSocket.close();
                log.debug("Successfully notified first instance.");
                return false;
            } catch (UnknownHostException e1) {
                log.error(e.getMessage(), e);
                return returnValueOnError;
            } catch (IOException e1) {
                log.error("Error connecting to local port for single instance notification");
                log.error(e1.getMessage(), e1);
                return returnValueOnError;
            }

        }
        return true;
    }

    public static void setApplicationInstanceListener(ApplicationInstanceListener listener) {
        subListener = listener;
    }

    private static void fireNewInstance() {
      if (subListener != null) {
        subListener.newInstanceCreated();
      }
  }
}

interface ApplicationInstanceListener {
    public void newInstanceCreated();
}