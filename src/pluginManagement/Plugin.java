package pluginManagement;

public interface Plugin {

	// TODO: Params und Return Werte
	// TODO: Irgendwie ist das hier noch hässlich -->Um die Infos über ein
	// Plugin abrufen zu können, muss ständig eine Instanz im Speicher gehalten
	// werden --> Schlimm?

	// Man könnte die Plugin Infos auch am Anfang rauslesen und dann irgendwie
	// abspeichern. Oder irgendeine Property File einfügen?

	public void getName();

	public void getID();

	public void getNoOfRequiredParams();

	public void getParamDescriptions();

	public Runnable getSender(String Endpoints, String Message,
			PluginParams optionalParams);

	// uberhaupt noetig? Params? Implementierung?
	public void getAnswer();

}
