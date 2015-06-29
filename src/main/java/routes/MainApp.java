package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.camel.main.Main;
@Deprecated
public class MainApp {

	public static void main(String[] args) {

		shutdownHandling();

		Main main = new Main();

		main.enableHangupSupport();

		main.addRouteBuilder(new SituationHandlerRouteBuilder());
		 try {
			if (main.getCamelTemplate() == null){
				 System.out.println("null");
			 }else{
				 System.out.println("Not null");
			 }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		try {
			main.run(args);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * TODO:Scheint mit eclipse stop nicht zu gehn
	 */
	private static void shutdownHandling() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting Down..");
			}
		});
	}

}
