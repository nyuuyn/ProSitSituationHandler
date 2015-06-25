package situationHandling.storage;

import java.util.ArrayList;

import situationHandling.storage.datatypes.Action;
import situationHandling.storage.datatypes.Situation;


public class RuleStorageTest {
	
	public static void main(String[] args) {

		RuleStorageAccess rsa = StorageAccessFactory
				.geRuleStorageAccess();
		
		ArrayList<Integer> ids = new ArrayList<>();
		
		Action action= null;
		Situation situation = new Situation("situation1", "object1");
		
		ids.add(rsa.addAction(situation, action));
		

		
	}

}
