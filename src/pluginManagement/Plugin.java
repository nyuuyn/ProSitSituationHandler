package pluginManagement;

public interface Plugin {

	// TODO: Params und Return Werte
	// TODO: Irgendwie ist das hier noch h�sslich -->Um die Infos �ber ein
	// Plugin abrufen zu k�nnen, muss st�ndig eine Instanz im Speicher gehalten
	// werden --> Schlimm?

	// Man k�nnte die Plugin Infos auch am Anfang rauslesen und dann irgendwie
	// abspeichern. Oder irgendeine Property File einf�gen?

	public void getName();

	public void getID();

	public void getNoOfRequiredParams();

	public void getParamDescriptions();

	public Runnable getSender(String Endpoints, String Message,
			PluginParams optionalParams);

	// uberhaupt noetig? Params? Implementierung?
	public void getAnswer();

}
