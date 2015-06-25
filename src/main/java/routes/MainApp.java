package routes;

import org.apache.camel.main.Main;

public class MainApp {

	public static void main(String[] args) {
		
		shutdownHandling();
		
		Main main = new Main();

		main.enableHangupSupport();
		

		main.addRouteBuilder(new SituationHandlerRouteBuilder());

		try {
			main.run(args);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		

	}
	
	/**
	 * 
	 * TODO:Scheint mit ctrl+c bzw. eclipse stop nicht zu gehn
	 */
	private static void shutdownHandling(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { System.out.println("Shutting Down..");}
		 });
	}
	


}
