package plugin;

import javax.swing.JPanel;

public abstract class Plugin {
	private String id;

	public static enum PluginState  {RUNNING, PAUSED, STOPPED}
	public Plugin(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// Callback method
	public abstract void layout(JPanel panel);
	public abstract void start();
	public abstract void stop();
	public abstract void pause();
	public abstract void load();
	public abstract PluginState getState();
}
