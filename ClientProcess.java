package com.socket.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

import javax.net.ssl.SSLSocket;

import com.secure.rmi.test.bankingApp.OnlineBankAccountIntf;
import com.secure.rmi.test.schlApp.SchoolActivitiesIntf;
import com.server.proxy.StubWrapper;
import com.socket.exceptions.AuthenticationException;

/**
 * 
 * @author micadeyeye
 *class connect to  server socket and also retrieves proxy object upon successful 
 *authentication
 */
public class ClientProcess {

Object proxyObject;

SecureBaseClientSocket sbcSocket; 
BufferedWriter bWriter;
BufferedReader bReader; 

ObjectInputStream objectInputStream;
	String loginCredentials;  // name/password for secure socket authentication with Server.... 
	
public Object stub;

/**
	 * 
    * @return
    * secure connection and authentication with the server
    * Upon successful authentication, clients retrieves a token and server (wrapper)proxy
 */

public boolean startProcess()

{
   try
        {
			

               loginCredentials = System.getProperty("com.socket.login.credentials");
			
	// singleton method for retrieving client socket , to  (securely) connect to  server
             sbcSocket = SecureBaseClientSocket.getInstance();

            if (sbcSocket == null)
           {
                    throw new Exception();
          }
         
bWriter = sbcSocket.stringWriter; // writer for sending info to the server via stream

authenticate(); 

System.out.println("done authenticating");
			
sbcSocket.sslSocket.close();

return true;
}

catch (Exception e) {

System.err.println(" could not instantiate socket " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
}

return true;
}
	/**
	 *  method authenticates against the server, retreives the proxy and a token  if authentication is successful
	 */
	
	/**
	 * 
	 */
private void authenticate() {

try{

	System.out.println("sending authentication details to the server ");
			bWriter.write(loginCredentials+"\n");

                         bWriter.flush();
			
                        ObjectInputStream objectInputStream = new ObjectInput                  Stream(sbcSocket.sslSocket.getInputStream()); 


	Object authenticationRslt = objectInputStream.readObject(); // retrieving response  [authentication token]from the  server

try{

       if (authenticationRslt == null)

	{
					throw new AuthenticationException();
				}

				System.out.println("token is  "  + authenticationRslt);
				
		stub = objectInputStream.readObject();
	
			}
			catch (AuthenticationException ex)
			{
				System.out.println(ex.getMessage());
			}


		}

		catch (IOException ex)	
		{
			System.out.print("error while authentication user " + ex.getMessage());

			ex.printStackTrace();
		}

		catch (ClassNotFoundException ex)
		{
			System.out.println(ex.getMessage());
		}
		 
	}


public Object getStub() {
		
		return stub;
	}

/*
	 * 
	 * Method closes all socket connections and streams.
 */
	
public void closeConnection() throws Exception
	
{
		
	SSLSocket socket = sbcSocket.sslSocket;
		
                                            if (!socket.isClosed()) 
			socket.close();
		
                                            try {
			sbcSocket.objectInputStream.close();
                                                  }
                                               catch (Exception ex)
		{
			
		}
		
                                                try {
			sbcSocket.objectOutputStream.close();
                                                      }
                                            catch (Exception ex)
		{
			
		}
		
		try {
			sbcSocket.streamWriter.close();
		}
		catch (Exception ex)
		{
			
		}

		try {
			sbcSocket.stringReader.close();
		}
		catch (Exception ex)
		{
			
		}	
		
		try {
			sbcSocket.stringWriter.close();
		}
		catch (Exception ex)
		{
			
		}	
		try {
			sbcSocket.streamReader.close();
		}
		catch (Exception ex)
		{
			
		}		
	}

public static void main (String args[]) throws RemoteException
	{
		System.setProperty("javax.net.ssl.trustStore","C:\\temp\\xslt\\keystore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword","changeit");
		System.setProperty("javax.net.ssl.keyStore","C:\\temp\\xslt\\keystore.jks");
		
		System.setProperty("javax.net.ssl.keyStorePassword","changeit");
		System.setProperty("com.socket.login.credentials","anonymous:password\n");
		System.setProperty("com.socket.server.portNumber","11099");	
		
		System.setProperty("com.socket.server.serverName","testmachine");
		
		//System.setProperty("sun.rmi.client.logLevel","true");
		//System.setProperty("javax.net.debug","all");
		
		
		
		ClientProcess client = new ClientProcess();
		client.startProcess();
		
	/*	StudentExamCalcIntf stub = (StudentExamCalcIntf)client.stub;
		
	   //	StudentExamCalcIntf stub = (StudentExamCalcIntf)  objectInputStream.readObject();			
		// pass on client security token to the proxy
		
		//((StubWrapper)Proxy.getInvocationHandler(stub)).captureClientSecurityToken((authenticationRslt));
		stub.contactInstructor("sege");
		System.out.println(stub.viewScore("id"));
		
		*/
		
	OnlineBankAccountIntf stub = (OnlineBankAccountIntf)client.stub;
		
		if(stub != null)
		{
			System.out.println(stub.bankName());
	System.out.println(stub.getAccountBalance("CustomerAcctNumber"));
		}
		
		
		
	}
}
