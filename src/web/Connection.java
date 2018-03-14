package web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import web.HttpRequest.HttpMethod;
import web.HttpResponse.StatusCode;

public class Connection implements Runnable {

	// server and client
	private Server server;
	private Socket client;
	
	// input- and output streams.
	private InputStream in;
	private OutputStream out;

	/*
	 * simple constructor for initialization of the members:
	 * server and client
	 */
	public Connection(Socket cl, Server s) {
		client = cl;
		server = s;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			
			// creates input and output streams across the client
			in = client.getInputStream();
			out = client.getOutputStream();
			
			// creates a http-request across the input stream.
			HttpRequest request = HttpRequest.parseAsHttp(in);
			
			// makes sure creating http-request was successful.
			if (request != null) {
				System.out.println("Request for " + request.getUrl() + " is being processed " +
					"by socket at " + client.getInetAddress() +":"+ client.getPort());
				
				/*
				 * creates a http-response and fills it with the needed informations.
				 */
				HttpResponse response;
				
				String method;
				
				// checks which method is active.
				if ((method = request.getMethod()).equals(HttpMethod.GET) 
						|| method.equals(HttpMethod.HEAD)) {
					
					// creates a handle to the required file.
					File f = new File(server.getWebRoot() + request.getUrl());
					response = new HttpResponse(StatusCode.OK).withFile(f);
					if (method.equals(HttpMethod.HEAD)) {
						response.removeBody();
					}
				} else {
					response = new HttpResponse(StatusCode.NOT_IMPLEMENTED);
				}
				
				// gives the response-object to the method respond.
				respond(response);
				
			} else { // error case
				System.err.println("Server accepts only HTTP protocol.");
			}
			
			in.close();
			out.close();
		} catch (IOException e) { // error case
			System.err.println("Error in client's IO.");
		} finally {
			try {
				client.close();
			} catch (IOException e) { // error case
				System.err.println("Error while closing client socket.");
			}
		}
	}

	/*
	 * writes the given response.
	 */
	public void respond(HttpResponse response) {
		String toSend = response.toString();
		PrintWriter writer = new PrintWriter(out);
		writer.write(toSend);
		writer.flush();
	}

}
