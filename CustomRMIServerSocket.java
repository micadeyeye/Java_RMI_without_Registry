package com.socket.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import com.secure.rmi.test.bankingApp.OnlineBankAccount;
import com.secure.rmi.test.bankingApp.OnlineBankAccountIntf;
import com.server.proxy.StubWrapper;
import com.socket.BaseCustomSocket;

public class CustomRMIServerSocket extends BaseCustomSocket implements  Runnable{

//static final int SERVER_PORT = 11099;
SSLServerSocket serverSocket;
SSLSocket _connection;
Object stubObject;

final static String USER_DATABASE_FILE = "c:\\temp\\usertable.txt" ;
final static String COMMENT_STRING = "#";

HashMap<String , String > userTable ;
HashMap<String , String >authUserTable;
	
String token;
Object proxy ;
String rmiInfaceClassName;
	

	
	

public boolean KEEP_RUNNING = true;
static final int WAIT_TIME = 500;

public CustomRMIServerSocket (Object obj, String className)
{
   this.stubObject = obj;
   this.rmiInfaceClassName = className;
   Thread serverThread = new Thread(this);
   serverThread.start();		
	
// start the thread;
}
public void run() {
		
try{

	 SSLServerSocket serverSocket  = SecureBaseServerSocket.getInstance().sslServerSocket;

               System.out.println ("connection ready");
			
                while (KEEP_RUNNING)
              {
                    Socket socket_ = serverSocket.accept();
                   _connection = (SSLSocket)socket_;
                  initialiseStreams(_connection);		
                  String userCredentials = stringReader.readLine();
                  System.out.println(userCredentials);
				

               token = authenticate (userCredentials); // if authentication is correct, sendStubObject
	
    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(_connection.getOutputStream());
    objectOutputStream.writeObject(token);

				
if (token == null) // authentication is unsuccessful, try again
    {
         _connection = null;
         System.out.println ("try again..");
    }
 else
{
					
  objectOutputStream.writeObject(proxy);
//stringWriter.write(token);
					
//objectOutputStream.writeObject(token);
objectOutputStream.flush();

break;
}
}
//stringWriter.flush();		

while (KEEP_RUNNING)
{
     //do nothing
     try{
             Thread.sleep(WAIT_TIME);
}

catch (InterruptedException ex) {
System.out.println(ex.getMessage());
ex.printStackTrace();

}


}
}

catch (IOException ex){
                    System.out.println(ex.getMessage());
                     ex.printStackTrace();
}
}

/**
 * 
	 * authenticate client using forwarded credentials  and return stub object if authentication is successful
	 * @param credentials
	 * 
	 * this method can be overridden to implement preferred authentication approach
 */

private String authenticate (String userCredentials){

// fetch all user details from flat file
if (userTable == null ){

userTable = new HashMap<String, String>();

try{
	FileReader fReader = new FileReader(new File(USER_DATABASE_FILE));
				BufferedReader bReader = new BufferedReader(fReader);

String nextLine ="";

while ((nextLine= bReader.readLine()) != null)
{

	if (!(nextLine.substring(0,1).compareTo(COMMENT_STRING)==0)) // to ignore commented lines
           {
                        int loc = nextLine.lastIndexOf(":"); // 
                        System.out.println("AB: " + nextLine.substring(0, loc) + " CD: " + nextLine.substring(loc+1));				

	userTable.put(nextLine.substring(0, loc), nextLine.substring(loc+1)); // add user:pass as key and method as data

}

nextLine ="";
}

bReader.close();
fReader.close();
}

catch (FileNotFoundException ex){

System.out.println(ex.getMessage());
}

catch (IOException ex){

System.out.println(ex.getMessage());
			}
		}

		// get User details
if (userTable.containsKey(userCredentials)){
   // user found and successfully authenticated

   //generate token and add details to authUserTable
if (authUserTable == null){
	authUserTable = new HashMap<String, String>();
}

String token = generateToken(userCredentials.substring(0,userCredentials.indexOf(":")));

 // retrieve user name  to generate a token
// set up token/authorizations map in authUserTable hashmap

   synchronized (this) 
{
	authUserTable.put(token, userTable.get(userCredentials));  // e.g [aTom:12075757; abc,def,ghi] 
}

System.out.println("token " + token +  " credentials  " + userTable.get(userCredentials));
			
InvocationHandler handler = new StubWrapper(stubObject,token,userTable.get(userCredentials));

try{
         proxy = Class.forName(rmiInfaceClassName).cast(Proxy.newProxyInstance(      Class.forName(rmiInfaceClassName).getClassLoader(), new Class[] { Class.forName(rmiInfaceClassName) },	                            handler));
}

 catch (Exception ex)
{
     ex.printStackTrace();
}
	

    return token;
}

   System.out.println("Authentication successful");

   return null;
}



private String generateToken(String username)
{
return   username + ":" + new Double(Math.random()).toString();
}
	
	
public static void main (String arg[]) throws RemoteException, InterruptedException

{
System.setProperty("javax.net.ssl.keyStore","C:\\temp\\xslt\\keystore.jks");
System.setProperty("javax.net.ssl.keyStorePassword","changeit");
System.setProperty("com.socket.server.portNumber","11099");
		
//System.setProperty("sun.rmi.server.logLevel","true");
//System.setProperty("javax.net.debug","all");

System.out.println(System.getProperty("javax.net.ssl.keyStore"));
		
/*StudentExamCalc result = new StudentExamCalc();

	StudentExamCalcIntf resultStub =  (StudentExamCalcIntf) UnicastRemoteObject.exportObject(result, 0, new  SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

*/
	
OnlineBankAccount result = new OnlineBankAccount();
		
OnlineBankAccountIntf resultStub =  (OnlineBankAccountIntf) UnicastRemoteObject.
				exportObject(result, 0, new  SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
		
	CustomRMIServerSocket socketInstance = new CustomRMIServerSocket(resultStub, "com.test.rmi.server.OnlineBankAccountIntf");		
		
              
                Thread.sleep(Long.MAX_VALUE);
	
  //new Thread(socketInstance).start();

      }

}
