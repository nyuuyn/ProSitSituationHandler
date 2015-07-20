package situationManagement;

public interface SituationManager {
	
	public boolean situationOccured ();
	
	public void subscribeOnSituation();

	public void removeSubscription();
	

}
