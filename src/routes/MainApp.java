package routes;

import org.apache.camel.main.Main;

public class MainApp {

	public static void main(String[] args) {
		Main main = new Main();

		main.enableHangupSupport();


		main.addRouteBuilder(new SituationHandlerRouteBuilder());

		try {
			main.run(args);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
