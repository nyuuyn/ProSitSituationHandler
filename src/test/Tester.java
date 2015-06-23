package test;

import java.util.Map;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;

public class Tester {

	public static void main(String[] args) {

		PluginManager pm = PluginManagerFactory.getManager();
	

		PluginParams params = new PluginParams();


		//mail plugin
//		try {
//			params.setParam("Email Subject", "Gmail Plugin Test");
//			 pm.getPluginSender("situationHandler.gmail",
//			 "stefan.fuerst.89@gmail.com", "Die Erste Mail vom Gmail Plugin",
//			 params).call();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//http plugin
		try {
			String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test=\"http://test/\">\r\n   <soapenv:Header/>\r\n   <soapenv:Body>\r\n      <test:hello>\r\n         <name>Stefan</name>\r\n      </test:hello>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>";
			params.setParam("Http method", "POST");
			Map<String, String> response = pm.getPluginSender("situationHandler.http",
					"http://localhost:4435/miniwebservice", soapBody, params)
					.call();
			
			System.out.println("Response:");
			System.out.println(response.get("body"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
