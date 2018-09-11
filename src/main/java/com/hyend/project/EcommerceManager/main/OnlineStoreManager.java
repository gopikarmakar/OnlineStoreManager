package com.hyend.project.EcommerceManager.main;

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
import javax.swing.JSeparator;
import java.awt.Panel;

/**
 * 
 * @author karmakargopi
 *
 */
public class OnlineStoreManager {

	private JFrame mainWindowFrame;	
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
				app.mainHandler.init();
				if(app.mainHandler.isConnectedToDB()) {	            
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
        JOptionPane.showMessageDialog(null, errorMessage, "Error: " + 
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
	    connectingWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeMainWindow() {
		mainWindowFrame = new JFrame();
		mainWindowFrame.getContentPane().setLayout(null);
		mainWindowFrame.setBounds(100, 100, 500, 640);
		mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		JLabel lblHyendStore = new JLabel("HyEnd Store");
		lblHyendStore.setBounds(204, 24, 81, 16);
		mainWindowFrame.getContentPane().add(lblHyendStore);
		
		/*Panel fileReadAndCreatePanel = new Panel();
		fileReadAndCreatePanel.setLayout(null);
		fileReadAndCreatePanel.setBounds(23, 55, 454, 160);
		mainWindowFrame.getContentPane().add(fileReadAndCreatePanel);*/
		
		final JComboBox<String> ecommPlatforms = new JComboBox<String>();
		for(String item: ConstantFields.ECOMMERCE_PLATFORMS) {
			ecommPlatforms.addItem(item);
		}
		ecommPlatforms.setBounds(44, 73, 215, 30);
		ecommPlatforms.setSelectedIndex(0);
		mainWindowFrame.getContentPane().add(ecommPlatforms);
		ecommPlatforms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent platform) {
				mainHandler.setEcommercePlatform(String.valueOf(ecommPlatforms.getSelectedItem()));
			}
		});
		
		JButton savePDFButton = new JButton("Save Invoice Pdf To DB");		
		savePDFButton.setBounds(261, 70, 191, 35);
		mainWindowFrame.getContentPane().add(savePDFButton);		
		savePDFButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.saveInvoicePdfToDB();
			}
		});
		
		final JTextField startDate = new JTextField();
		startDate.setColumns(10);
		startDate.setBounds(23, 128, 141, 26);
		mainWindowFrame.getContentPane().add(startDate);
		
		final JTextField endDate = new JTextField();
		endDate.setColumns(10);
		endDate.setBounds(336, 128, 141, 26);
		mainWindowFrame.getContentPane().add(endDate);
		
		JButton excelByDate = new JButton("Create Spreadsheet");
		excelByDate.setBounds(176, 123, 150, 35);
		mainWindowFrame.getContentPane().add(excelByDate);
		excelByDate.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.generateSpreadSheetBetween(startDate.getText(), endDate.getText());
			}
		});
		
		JButton sheetForPaymentRcvd = new JButton("Payments Received Sheet");
		sheetForPaymentRcvd.setBounds(44, 169, 184, 35);
		mainWindowFrame.getContentPane().add(sheetForPaymentRcvd);
		
		
		JButton sheetForDelivered = new JButton("Courier Delivered Sheet");
		sheetForDelivered.setBounds(275, 169, 177, 35);
		mainWindowFrame.getContentPane().add(sheetForDelivered);
				
		/*Panel updateDataPanel = new Panel();
		updateDataPanel.setBounds(23, 225, 454, 245);
		mainWindowFrame.getContentPane().add(updateDataPanel);
		updateDataPanel.setLayout(null);*/
		
		final JTextField orderId = new JTextField();
		orderId.setBounds(132, 240, 243, 30);
		mainWindowFrame.getContentPane().add(orderId);
		orderId.setColumns(10);
		
		JButton updatePaymentStatusBtn = new JButton("Update Payment Received");
		updatePaymentStatusBtn.setBounds(158, 278, 187, 35);
		mainWindowFrame.getContentPane().add(updatePaymentStatusBtn);
		updatePaymentStatusBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.updatePaymentStatusAsReceived(orderId.getText());
			}
		});
		
		JButton updateReturnStatusBtn = new JButton("Update Courier Delivered");
		updateReturnStatusBtn.setBounds(45, 325, 191, 35);
		mainWindowFrame.getContentPane().add(updateReturnStatusBtn);
		updateReturnStatusBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.updateReturnStatusFAsReturned(orderId.getText());
			}
		});
		
		JButton updateCourierStatusBtn = new JButton("Update Courier Returned");
		updateCourierStatusBtn.setBounds(275, 325, 187, 35);
		mainWindowFrame.getContentPane().add(updateCourierStatusBtn);
		updateCourierStatusBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.updateCourierStatusAsDelivered(orderId.getText());;
			}
		});
		
		JComboBox<String> updateReturnCondition = new JComboBox<String>();
		updateReturnCondition.setBounds(136, 380, 239, 30);
		for(String item: ConstantFields.COURIER_RETURN_CONDITIONS) {
			updateReturnCondition.addItem(item);
		}
		mainWindowFrame.getContentPane().add(updateReturnCondition);
		
		final JTextField returnRcvdDate = new JTextField();
		returnRcvdDate.setBounds(44, 425, 210, 30);
		mainWindowFrame.getContentPane().add(returnRcvdDate);
		returnRcvdDate.setColumns(10);
		
		JButton updateReturnRcvdDateBtn = new JButton("Update Return Rceived Date");
		updateReturnRcvdDateBtn.setBounds(261, 420, 201, 35);
		mainWindowFrame.getContentPane().add(updateReturnRcvdDateBtn);
		
		Panel panel = new Panel();
		panel.setLayout(null);
		panel.setBounds(23, 480, 454, 110);
		mainWindowFrame.getContentPane().add(panel);		
		
		JButton deleteAllPaymentStatusRcvdBtn = new JButton("Delete All Payments Rcvd");
		deleteAllPaymentStatusRcvdBtn.setBounds(140, 12, 180, 35);
		panel.add(deleteAllPaymentStatusRcvdBtn);
		deleteAllPaymentStatusRcvdBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.deleteAllPaymentStatusAsReceived();
			}
		});
		
		JButton deleteAllCourierStatusDeliveredBtn = new JButton("Delete All Delivered");
		deleteAllCourierStatusDeliveredBtn.setBounds(20, 59, 150, 35);
		panel.add(deleteAllCourierStatusDeliveredBtn);
		deleteAllCourierStatusDeliveredBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.deleteAllCourierStatusAsDelivered();				
			}
		});
		
		JButton deleteAllCourierStatusReturnedBtn = new JButton("Delete All Returned");
		deleteAllCourierStatusReturnedBtn.setBounds(281, 59, 150, 35);
		panel.add(deleteAllCourierStatusReturnedBtn);
		deleteAllCourierStatusReturnedBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.deleteAllCourierStatusAsReturned();				
			}
		});
	}	
}
