package com.hyend.project.EcommerceManager;

import com.hyend.project.EcommerceManager.handler.MainHandler;

/**
 * Main Class
 * 
 * @author karmakargopi
 *
 */
public class EcommerceManager
{
    public static void main( String[] args ) {        
    	MainHandler handler = new MainHandler();
    	handler.init();   	
    }
}