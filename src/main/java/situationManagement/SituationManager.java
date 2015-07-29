package situationManagement;

import situationHandling.storage.datatypes.Situation;

public interface SituationManager {
	
	public boolean situationOccured (Situation situation);
	
	public void subscribeOnSituation(Situation situation);

	public void removeSubscription(Situation situation);
	
	public void init ();
	

}
