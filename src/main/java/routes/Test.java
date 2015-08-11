package routes;

import situationHandling.WsaSoapMessage;


public class Test {

	public static void main(String[] args) {
		
		
		

		System.out.println(WsaSoapMessage.createRollbackRequest(
				"http://testEmpfaenger", "123345667").toString());

	}
}
