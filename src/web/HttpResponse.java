package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

public class HttpResponse {

	// specify the protocol type
	private static final String protocol = "HTTP/1.0";

	private String status;
	
	// represents the header of the http-response.
	private NavigableMap<String, String> headers = new TreeMap<String, String>();
	
	// represents the actual content of the http-response.
	private byte[] body = null;

	public HttpResponse(String status) {
		this.status = status;
		setDate(new Date());
	}

	/*
	 * reads the given file f and puts the content into the field body.
	 * The field body will be instantiate, too.
	 */
	public HttpResponse withFile(File f) {
		if (f.isFile()) {
			try {
				
				// read and write procedure of the content of the file.
				FileInputStream reader = new FileInputStream(f);
				int length = reader.available();
				body = new byte[length];
				reader.read(body);
				reader.close();
				
				// updates the header of the http-response.
				setContentLength(length);
				
				// determines the content type of the http-response and write it
				// into the header.
				if (f.getName().endsWith(".htm") || f.getName().endsWith(".html")) {
					setContentType(ContentType.HTML);
				} else {
					setContentType(ContentType.TEXT);
				}
			} catch (IOException e) { // error case
				System.err.println("Error while reading " + f);
			}
			return this; // returns this instance.
		} else { // error case if the given file is not a file.
			// if the given file is not available!
			return new HttpResponse(StatusCode.NOT_FOUND)
				.withHtmlBody("<html><body>File " + f + " not found.</body></html>");
		}
	}

	public HttpResponse withHtmlBody(String msg) {
		
		// setups the header of the http-response
		setContentLength(msg.getBytes().length);
		setContentType(ContentType.HTML);
		
		// writes the given message in the body field.
		body = msg.getBytes();
		return this; // returns this instance
	}

	
	/*
	 * The following methods manipulates the header of the http response.
	 * To be precise the data structure headers.
	 */
	
	public void setDate(Date date) {
		headers.put("Date", date.toString());
	}

	public void setContentLength(long value) {
		headers.put("Content-Length", String.valueOf(value));
	}

	public void setContentType(String value) {
		headers.put("Content-Type", value);
	}

	// end of manipulate methods. 
	
	/*
	 * removes the body 
	 * set the body field equal to null.
	 */
	public void removeBody() {
		body = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = protocol + " " + status +"\n";
		for (String key : headers.descendingKeySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";
		if (body != null) {
			result += new String(body);
		}
		return result;
	}

	/*
	 * inert classes for constants. 
	 */
	public static class StatusCode {
		public static final String OK = "200 OK";
		public static final String NOT_FOUND = "404 Not Found";
		public static final String NOT_IMPLEMENTED = "501 Not Implemented";
	}

	/*
	 * inert classes for constants. 
	 */
	public static class ContentType {
		public static final String TEXT = "text/plain";
		public static final String HTML = "text/html";
	}

}
