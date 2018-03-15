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
		else if (args.length == 3) {
			
			// checks the command-line arguments with regular expressions
			
			// checks the port number
			if (!args[0].matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) {
				System.err.println("Error: invailde port number!");
				System.exit(1);
			}
			
			// checks the working directory for the server
			if (!args[1].matches("^(.+)/?([^/]+)$")) {
				System.err.println("Error: invalide working directory for the server!");
				System.exit(1);
			}
			
			// checks the number of max Threads
			try {
				int number = Integer.parseInt(args[2]);
				if (number < 1) {
					
					// goes to the catch block.
					throw new NumberFormatException(); 
				}
				
				// using the given command-line argument.
				maxThreads = number;
			}
			catch (NumberFormatException e) {
				System.err.println("Error: invalide number of max. threads!");
				System.exit(1);
			}
			
			// using of the command line arguments.
			port = Integer.parseInt(args[0]);
			webRoot = args[1];
		} else { // error case 
			
			// end user informations.
			System.err.println("Please give a proper number of arguments");
			System.err.println("Use -h or -help for more informations.");
			System.err.println("You can use the server with none arguments under"
					+ " localhost:8080");
			System.exit(1);
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
//			if (!threadsPool.awaitTermination(10, TimeUnit.SECONDS)) 
//				threadsPool.shutdownNow(); // otherwise send new shutdown-command.
			
			// makes sure that after ten seconds the executor service is down.
			while(!threadsPool.awaitTermination(10, TimeUnit.SECONDS)) {
				threadsPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			System.err.println("close: error by closing the threads pool");
		} // error case at the moment empty. 
	}

	/*
	 * simple getter method for the string member webRoot.
	 */
	public String getWebRoot() {
		return webRoot;
	}

}
