package com.airbusds.idea.gui;

import com.airbusds.idea.MessageHandler;

public class MessageTicker{
	
	private static MessageHandler handler;
	
	public static void setHandler(MessageHandler mh){
		handler = mh;
	}
	
	public static void showMessage(String message){
		handler.showMessage(message);
	}
	
	public static void showError(String message){
		handler.showError(message);
	}
	
}
