package gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import web.Server;

public class Frame {

	private JFrame frmWebserver;
	private JTextField inputPort;
	private JTextField inputDirectory;
	private JTextField inputThreads;
	private JButton btnNewButton;

	// the actual server
	private Thread thread;

	// status of the server on or off
	private boolean status;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame window = new Frame();
					window.frmWebserver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame() {
		initialize();
		status = false;
		btnNewButton.setBackground(Color.red);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWebserver = new JFrame();
		frmWebserver.setTitle("WebServer ");
		frmWebserver.setBounds(100, 100, 450, 300);
		frmWebserver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWebserver.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("port: ");
		lblNewLabel.setBounds(31, 32, 70, 15);
		frmWebserver.getContentPane().add(lblNewLabel);

		inputPort = new JTextField();
		inputPort.setText("8080");
		inputPort.setBounds(181, 30, 114, 19);
		frmWebserver.getContentPane().add(inputPort);
		inputPort.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("working directory:");
		lblNewLabel_1.setBounds(31, 59, 136, 15);
		frmWebserver.getContentPane().add(lblNewLabel_1);

		inputDirectory = new JTextField();
		inputDirectory.setText("wwwroot");
		inputDirectory.setBounds(181, 57, 114, 19);
		frmWebserver.getContentPane().add(inputDirectory);
		inputDirectory.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("max. threads: ");
		lblNewLabel_2.setBounds(31, 86, 136, 15);
		frmWebserver.getContentPane().add(lblNewLabel_2);

		inputThreads = new JTextField();
		inputThreads.setText("10");
		inputThreads.setBounds(181, 88, 114, 19);
		frmWebserver.getContentPane().add(inputThreads);
		inputThreads.setColumns(10);

		// turns on/off the server.
		btnNewButton = new JButton("on/off server");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!status) { // turns on the server

					if (inputPort.getText().length() > 0 && inputThreads.getText().length() > 0
							&& inputDirectory.getText().length() > 0) {
						int port = Integer.parseInt(inputPort.getText());
						int maxThreads = Integer.parseInt(inputThreads.getText());
						String webRoot = inputDirectory.getText();
						
						thread = new Thread(new Server(port, webRoot, maxThreads));
						thread.start();
						
					} else { // default values for the server
						thread = new Thread(new Server(8080, "wwwroot", 10));
						thread.start();
					}
					
					status = true;
					btnNewButton.setBackground(Color.green);

				} else { // turns off the server
					thread.interrupt();
					status = false;
					btnNewButton.setBackground(Color.red);
				}
			}
		});
		btnNewButton.setBounds(31, 217, 165, 25);
		frmWebserver.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("exit");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnNewButton_1.setBounds(255, 217, 117, 25);
		frmWebserver.getContentPane().add(btnNewButton_1);
	}
}
