package test.plugin;

import java.util.Map;

import pluginManagement.PluginManager;
import pluginManagement.PluginManagerFactory;
import situationHandler.plugin.PluginParams;

public class PluginTest {

	public static void main(String[] args) {

		PluginManager pm = PluginManagerFactory.getPluginManager();

		PluginParams params = new PluginParams();

		// mail plugin (deactive anti virus)
		try {
			params.setParam("Email Subject", "Gmail Plugin Test");
			pm.getPluginSender("situationHandler.gmail",
					"stefan.fuerst.89@gmail.com",
					"Die Erste Mail vom Gmail Plugin", params).call();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// add plugin test (place jar at path)

		pm.addPlugin(
				"situationHandler.http",
				"C:\\Users\\Stefan\\workspace_Masterarbeit\\Situation Handler\\situationHandler.http.jar");

		// http plugin (needs server running)
		try {
			String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test=\"http://test/\">\r\n   <soapenv:Header/>\r\n   <soapenv:Body>\r\n      <test:hello>\r\n         <name>Stefan</name>\r\n      </test:hello>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>";
			params.setParam("Http method", "POST");
			Map<String, String> response = pm.getPluginSender(
					"situationHandler.http",
					"http://localhost:4435/miniwebservice", soapBody, params)
					.call();

			System.out.println("Response:");
			System.out.println(response.get("body"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		pm.removePlugin("situationHandler.http");

		pm.addPlugin(
				"situationHandler.http",
				"C:\\Users\\Stefan\\workspace_Masterarbeit\\Situation Handler\\situationHandler.http.jar");

		// http plugin (needs server running)
		try {
			String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:test=\"http://test/\">\r\n   <soapenv:Header/>\r\n   <soapenv:Body>\r\n      <test:hello>\r\n         <name>Stefan</name>\r\n      </test:hello>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>";
			params.setParam("Http method", "POST");
			Map<String, String> response = pm.getPluginSender(
					"situationHandler.http",
					"http://localhost:4435/miniwebservice", soapBody, params)
					.call();

			System.out.println("Response:");
			System.out.println(response.get("body"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(pm.getAllPluginIDs().size());
		pm.removePlugin("situationHandler.http");
		//trying to remove twice
		pm.removePlugin("situationHandler.gmail");
		pm.removePlugin("situationHandler.gmail");
		
		//trying to add twice
		pm.addPlugin(
				"situationHandler.http",
				"C:\\Users\\Stefan\\workspace_Masterarbeit\\Situation Handler\\situationHandler.http.jar");

		pm.addPlugin(
				"situationHandler.http",
				"C:\\Users\\Stefan\\workspace_Masterarbeit\\Situation Handler\\situationHandler.http.jar");

		
		System.out.println(pm.getAllPluginIDs().size());

	}


}
