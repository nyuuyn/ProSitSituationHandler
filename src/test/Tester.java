package test;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;

public class Tester {

	public static void main(String[] args) {

		PluginManager pm = PluginManagerFactory.getManager();
		
		PluginParams params = new PluginParams();
		params.setParam("Email Subject", "Gmail Plugin Test");
		
		pm.getPluginSender("situationHandler.gmail", "stefan.fuerst.89@gmail.com", "Die Erste Mail vom Gmail Plugin", params).run();;
	
	}

}
