package routes;

import java.net.InetAddress;
import java.net.UnknownHostException;

import situationHandling.storage.StorageAccessFactory;

public class Test {

	public static void main(String[] args) {

		
		System.out.println(StorageAccessFactory.getHistoryAccess().getHistorySize());
		
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
}
