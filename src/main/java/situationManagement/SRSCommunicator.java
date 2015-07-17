package situationManagement;

class SRSCommunicator {
	
	private static final String SUBSCRIPTION_RECEIVER_URL = "";

	public static void subscribe() {
		//TODO: Subscribe methode --> benötigt als Param noch die Situation oder so
	}

	public static void unsubscribe() {
		//TODO:unsubscrobe --> Sitaution als Param
	}

	public static boolean getSituationState() {
		//TODO: Get Abfrage an SRS ; Situation als Param benötigt
		return false;
	}
	
	public static void receiveSituationChange(SituationResult situationResult){
		//TODO: Das hier als Zielmethode für die entsprechende Camel ROute/Rest OP --> Sit Handling einleiten
		//TODO: fraglich ob das hier überhaupt nötig ist, oder ob die Sit Handler komponente direkt als Ziel dient
	}
}
