package com.hyend.project.EcommerceManager.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.BorderFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.hyend.project.EcommerceManager.handler.MainHandler;
import com.hyend.project.EcommerceManager.util.ConstantFields;

/**
 * 
 * @author karmakargopi
 *
 */
public class OnlineStoreManager {

	private JTextField endDate;
	private JTextField orderId;
	private JTextField startDate;
	private JFrame mainWindowFrame;	
	private JTextField returnDateOrCond;
	private JFrame connectingWindowFrame;
	private  MainHandler mainHandler = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		startApp();
	}
	
	public static void startApp() {
		
		final OnlineStoreManager app = new OnlineStoreManager();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				app.connectingWindowFrame.setVisible(true);
				if(app.mainHandler.init()) {	            
	        		EventQueue.invokeLater(new Runnable() {
	        			public void run() {
	        				try {
	        					app.connectingWindowFrame.setVisible(false);
	    	                	app.connectingWindowFrame.dispose();
	        					app.mainWindowFrame.setVisible(true);
	        				} catch (Exception e) {
	        					e.printStackTrace();
	        				}
	        			}
	        		});
	        	}
	        	else {
	        		showErrorMessage("MongoDB Connection Failed...", 
	    					"MongoDB Server Is Down. Please Check and Try Again!");
	        		EventQueue.invokeLater(new Runnable() {
		                public void run() {
		                	app.connectingWindowFrame.setVisible(false);
		                	app.connectingWindowFrame.dispose();
		                }
		            });
	        	}				
			}
		}).start();
	}
	
	public static void showInfoMessage(String titleBar, String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Info: " + 
        		titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
	
	public static void showErrorMessage(String titleBar, String errorMessage)
    {
        JOptionPane.showMessageDialog(null, errorMessage, "Info: " + 
        		titleBar, JOptionPane.ERROR_MESSAGE);
    }
	
	/**
	 * Create the application.
	 */
	public OnlineStoreManager() {
		mainHandler = new MainHandler();
		initializeConnectingWindow();
		initializeMainWindow();	
	}
	
	private void initializeConnectingWindow() {
		connectingWindowFrame = new JFrame("Connecting...");
	    final JProgressBar progressBar = new JProgressBar();
	    progressBar.setIndeterminate(true);
	    final JPanel contentPane = new JPanel();
	    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    contentPane.setLayout(new BorderLayout());
	    contentPane.add(new JLabel("Connecting To mongodb://localhost:27017"), BorderLayout.NORTH);
	    contentPane.add(progressBar, BorderLayout.CENTER);
	    connectingWindowFrame.setContentPane(contentPane);
	    connectingWindowFrame.pack();
	    connectingWindowFrame.setLocationRelativeTo(null);	    
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeMainWindow() {
		mainWindowFrame = new JFrame();
		mainWindowFrame.setBounds(100, 100, 500, 400);
		mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindowFrame.getContentPane().setLayout(null);
		
		JLabel lblHyendStore = new JLabel("HyEnd Store");
		lblHyendStore.setBounds(204, 24, 81, 16);
		mainWindowFrame.getContentPane().add(lblHyendStore);
		
		final JComboBox<String> ecommPlatforms = new JComboBox<String>();
		for(String item: ConstantFields.ECOMMERCE_PLATFORMS) {
			ecommPlatforms.addItem(item);
		}
		ecommPlatforms.setBounds(49, 73, 215, 27);
		ecommPlatforms.setSelectedIndex(0);
		mainWindowFrame.getContentPane().add(ecommPlatforms);
		ecommPlatforms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent platform) {
				mainHandler.setEcommercePlatform(String.valueOf(ecommPlatforms.getSelectedItem()));
			}
		});
		
		JButton savePDFButton = new JButton("Save Invoice Pdf To DB");		
		savePDFButton.setBounds(261, 72, 191, 29);
		mainWindowFrame.getContentPane().add(savePDFButton);		
		savePDFButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.saveInvoicePdfToDB();
			}
		});
		
		startDate = new JTextField();
		startDate.setBounds(166, 240, 207, 26);
		mainWindowFrame.getContentPane().add(startDate);
		startDate.setColumns(10);	
		
		JButton excelByDate = new JButton("Create Excel");
		excelByDate.setBounds(176, 128, 150, 29);
		mainWindowFrame.getContentPane().add(excelByDate);
		excelByDate.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.generateSpreadSheetBetween(startDate.getText(), endDate.getText());
			}
		});
		
		endDate = new JTextField();
		endDate.setColumns(10);
		endDate.setBounds(166, 293, 207, 26);
		mainWindowFrame.getContentPane().add(endDate);
		
		JButton excelForDelivered = new JButton("Excel For Delivered");
		excelForDelivered.setBounds(44, 169, 184, 29);
		mainWindowFrame.getContentPane().add(excelForDelivered);
		
		JButton excelForAll = new JButton("Excel For All From DB");
		excelForAll.setBounds(275, 169, 177, 29);
		mainWindowFrame.getContentPane().add(excelForAll);
		
		JButton btnNewButton_4 = new JButton("Courier Status");
		btnNewButton_4.setBounds(6, 240, 158, 29);
		mainWindowFrame.getContentPane().add(btnNewButton_4);
		
		orderId = new JTextField();
		orderId.setColumns(10);
		orderId.setBounds(23, 128, 141, 26);
		mainWindowFrame.getContentPane().add(orderId);
		
		JButton btnNewButton_5 = new JButton("Return Status");
		btnNewButton_5.setBounds(377, 240, 117, 29);
		mainWindowFrame.getContentPane().add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("Return Condition");
		btnNewButton_6.setBounds(6, 293, 158, 29);
		mainWindowFrame.getContentPane().add(btnNewButton_6);
		
		JButton btnNewButton_7 = new JButton("Return Date ");
		btnNewButton_7.setBounds(377, 293, 117, 29);
		mainWindowFrame.getContentPane().add(btnNewButton_7);
		
		returnDateOrCond = new JTextField();
		returnDateOrCond.setColumns(10);
		returnDateOrCond.setBounds(336, 128, 141, 26);
		mainWindowFrame.getContentPane().add(returnDateOrCond);
	}	
}
