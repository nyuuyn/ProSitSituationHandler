package situationHandling.storage;

import situationManagement.Situation;

public interface SituationStorageAccess {
	
	//TODO: Return Typen und Parameter richtig!
	
	public void addEndpoint (String endpoint, Situation situation);
	
	public void removeEndpoint();
	
	public void getAllEndpoints();
	
	public void getEndpointBySituation(Situation situation);
	
	public void addRule();
	
	public void removeRule();
	
	public void getRuleBySituation (Situation situation);
	
	public void getAllRules();

}
