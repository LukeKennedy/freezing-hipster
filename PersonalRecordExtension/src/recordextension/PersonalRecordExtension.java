package recordextension;

import java.awt.BorderLayout;

import javax.swing.JPanel;


import plugin.Plugin;

public class PersonalRecordExtension extends Plugin {
	public static final String PLUGIN_ID = "Personal Record";

	JPanel panel;
	private String desciption = "Store stuff!"; 
	
	public PersonalRecordExtension() {
		super(PLUGIN_ID);
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new PersonalRecordPanel();
		parentPanel.add(panel);
	}

	@Override
	public void start() {
		// Nothing to initialize
	}

	@Override
	public void stop() {
		// Nothing to finalize
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	@Override
	public void pause() {
	}


	@Override
	public PluginState getState() {
		return null;
	}

	@Override
	public void resume() {
	}

	@Override
	public String getDescription() {
		return desciption;
	}

	@Override
	public Plugin getNewInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
