package situationHandling;

import situationHandling.storage.datatypes.Situation;

public interface SituationHandler {
	
	public void situationChanged (Situation situation, boolean state);

}
