package com.socket.server;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;


/**
 * 
 * @author micadeyeye
 * class provides a Secure path for Socket  communication
 *
 */
public class SecureBaseServerSocket  {


	SSLContext context;
	KeyManagerFactory kmf;
	KeyStore ks;

	char[] storepass =System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
	char[] keypass = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
	String storename = System.getProperty("javax.net.ssl.keyStore");
	int SOCKET_PORT = Integer.parseInt( System.getProperty("com.socket.server.portNumber"));



	SSLServerSocket sslServerSocket = null;

	private static  SecureBaseServerSocket instance ;

	private  SecureBaseServerSocket () {

		try{


			context = SSLContext.getInstance("SSL");
			kmf = KeyManagerFactory.getInstance("SunX509");
			FileInputStream fin = new FileInputStream(storename);
			ks = KeyStore.getInstance("JKS");
			ks.load(fin, storepass);

			kmf.init(ks, keypass);
			context.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory ssf = context.getServerSocketFactory();

			sslServerSocket =(SSLServerSocket) ssf.createServerSocket(SOCKET_PORT);  


		}
		catch ( Exception ex)
		{
			System.err.println("Error while inititalising SecureBaseServerSocket " + ex.getMessage());
			ex.printStackTrace();
		}


	}

	public static SecureBaseServerSocket getInstance(){

		if (instance  ==  null)
			instance = new SecureBaseServerSocket();

		return instance;


	}

}
