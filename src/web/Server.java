package web;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {

	// actual server
	private ServerSocket server;
	
	// working directory of the server.
	private final String webRoot;
	
	// manager of the threads.
	private ExecutorService threadsPool;

	// server port
	private final int port;
	
	// max. numbers of threads.
	private final int threadsLimit;

	/*
	 * main-program
	 * inputs: command-line-arguments. 
	 * 		   port
	 * 		   webRoot (working directory of the server)
	 * 		   maxthreads (max number of threads)
	 * If none arguments given the main-method uses standard arguments.
	 */
	public static void main(String[] args) {
		
		// local variables overwrites the member variables.
		int port = 8080;
		String webRoot = "wwwroot";
		int maxThreads = 10;
		
		// makes sure whether command-line arguments are given.
		// or whether the help-option is given.
		if (args.length == 0 || args[0].equals("-h") || args[0].equals("-help"))
			
			// end user information for proper using the server. 
			System.out.println("Usage: java -cp WebServer.jar web.Server <port> <web root> <threads limit>\n");
		else {
			
			// using of the command line arguments.
			port = Integer.parseInt(args[0]);
			webRoot = args[1];
			maxThreads = Integer.parseInt(args[2]);
		}
		
		// instantiates a main-thread and given a object of type Server.
		// after that the main-thread will be start. 
		new Thread(new Server(port, webRoot, maxThreads)).start();
	}

	/*
	 * simple constructor that initialized the members:
	 * port
	 * threadsLimit
	 * and webRoot.
	 */
	public Server(int port, String webRoot, int maxThreads) {
		this.port = port;
		this.threadsLimit = maxThreads;
		this.webRoot = webRoot;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			
			// creates a server socket.
			server = new ServerSocket(port);
			
			// creates a ExecutorService for the threads
			threadsPool = Executors.newFixedThreadPool(threadsLimit);
		} catch (IOException e) { // error case
			System.err.println("Cannot listen on port " + port);
			System.exit(1);
		}
		
		// end user message that report success and informations for the server.
		System.out.println("Running server on the port " + port + 
				" with web root folder \"" + webRoot + "\" and " + threadsLimit + " threads limit.");

		// registers new connection threads by the http server
		while (!Thread.interrupted()) {
			try {
				
				// registers new threads by the executor service.
				threadsPool.execute(new Thread(new Connection(server.accept(), this)));
			} catch (IOException e) { // error case
				System.err.println("Cannot accept client.");
			}
		}
		
		// closes the Server; cleanup
		close();
	}

	/*
	 * cleanup of the server.
	 */
	public void close() {
		try {
			
			// closes the server socket.
			server.close();
		} catch (IOException e) { // error case
			System.err.println("Error while closing server socket.");
		}
		
		// shutdowns the executor service for managing the connection threads.
		threadsPool.shutdown();
		try {
			
			// makes sure that after ten seconds the executor service is down.
			if (!threadsPool.awaitTermination(10, TimeUnit.SECONDS)) 
				threadsPool.shutdownNow(); // otherwise send new shutdown-command.
		} catch (InterruptedException e) {} // error case at the moment empty. 
	}

	/*
	 * simple getter method for the string member webRoot.
	 */
	public String getWebRoot() {
		return webRoot;
	}

}
