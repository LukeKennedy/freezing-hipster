package plugin;

import javax.swing.JPanel;

public abstract class Plugin {
	private String id;
	private long time;
	private long start;

	public static enum PluginState {
		RUNNING, PAUSED, STOPPED
	}

	public Plugin(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void start() {
		this.start = System.currentTimeMillis();
		time = 0;
	}

	public void pause() {
		time += System.currentTimeMillis() - start;
	}

	public void resume() {
		start = System.currentTimeMillis();
	}

	public long getRunningTime() {
		if(getState() == PluginState.PAUSED){
			return time;
		}
		return time + System.currentTimeMillis() - start;
	}

	public abstract void layout(JPanel panel);

	public abstract void stop();

	public abstract PluginState getState();

	public abstract String getDescription();

	public abstract Plugin getNewInstance();
}
