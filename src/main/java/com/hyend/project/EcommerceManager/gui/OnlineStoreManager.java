package com.hyend.project.EcommerceManager.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.hyend.project.EcommerceManager.handler.MainHandler;
import com.hyend.project.EcommerceManager.util.ConstantFields;

public class OnlineStoreManager {

	private JFrame frame;
	private JTextField endDate;
	private JTextField orderId;
	private JTextField startDate;	
	private JTextField returnDateOrCond;
	
	private MainHandler mainHandler = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OnlineStoreManager window = new OnlineStoreManager();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OnlineStoreManager() {
		mainHandler = new MainHandler();
		mainHandler.init();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblHyendStore = new JLabel("HyEnd Store");
		lblHyendStore.setBounds(204, 24, 81, 16);
		frame.getContentPane().add(lblHyendStore);
		
		final JComboBox<String> ecommPlatforms = new JComboBox<String>();
		for(String item: ConstantFields.ECOMMERCE_PLATFORMS) {
			ecommPlatforms.addItem(item);
		}
		ecommPlatforms.setBounds(49, 73, 215, 27);
		ecommPlatforms.setSelectedIndex(0);
		frame.getContentPane().add(ecommPlatforms);
		ecommPlatforms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent platform) {
				mainHandler.setEcommercePlatform(String.valueOf(
						ecommPlatforms.getSelectedItem()));
			}
		});
		
		JButton savePDFButton = new JButton("Save Invoice Pdf To DB");		
		savePDFButton.setBounds(261, 72, 191, 29);
		frame.getContentPane().add(savePDFButton);		
		savePDFButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.saveInvoicePdfToDB();
			}
		});
		
		startDate = new JTextField();
		startDate.setColumns(10);
		startDate.setBounds(23, 128, 141, 26);
		frame.getContentPane().add(startDate);
		
		endDate = new JTextField();
		endDate.setColumns(10);
		endDate.setBounds(336, 128, 141, 26);
		frame.getContentPane().add(endDate);
		
		JButton excelByDate = new JButton("Create Excel");
		excelByDate.setBounds(176, 128, 150, 29);
		frame.getContentPane().add(excelByDate);
		excelByDate.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				mainHandler.generateSpreadSheetBetween(
						startDate.getText(), endDate.getText());
			}
		});		
		
		JButton excelForDelivered = new JButton("Excel For Delivered");
		excelForDelivered.setBounds(44, 169, 184, 29);
		frame.getContentPane().add(excelForDelivered);
		
		JButton excelForAll = new JButton("Excel For All From DB");
		excelForAll.setBounds(275, 169, 177, 29);
		frame.getContentPane().add(excelForAll);
		
		JButton btnNewButton_4 = new JButton("Courier Status");
		btnNewButton_4.setBounds(6, 240, 158, 29);
		frame.getContentPane().add(btnNewButton_4);
		
		orderId = new JTextField();
		orderId.setColumns(10);
		orderId.setBounds(166, 240, 207, 26);
		frame.getContentPane().add(orderId);
		
		JButton btnNewButton_5 = new JButton("Return Status");
		btnNewButton_5.setBounds(377, 240, 117, 29);
		frame.getContentPane().add(btnNewButton_5);
		
		JButton btnNewButton_6 = new JButton("Return Condition");
		btnNewButton_6.setBounds(6, 293, 158, 29);
		frame.getContentPane().add(btnNewButton_6);
		
		JButton btnNewButton_7 = new JButton("Return Date ");
		btnNewButton_7.setBounds(377, 293, 117, 29);
		frame.getContentPane().add(btnNewButton_7);
		
		returnDateOrCond = new JTextField();
		returnDateOrCond.setColumns(10);
		returnDateOrCond.setBounds(166, 293, 207, 26);
		frame.getContentPane().add(returnDateOrCond);
	}
}
