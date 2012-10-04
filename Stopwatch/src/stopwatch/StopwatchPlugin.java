package stopwatch;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import plugin.Plugin;

public class StopwatchPlugin extends Plugin {
	
	public static final String PLUGIN_ID = "Stopwatch Plugin";
	private String description = "A simple Stopwatch!";
	private StopPanel panel;

	private PluginState state;
	
	public StopwatchPlugin(){
		super(PLUGIN_ID);
		state = PluginState.RUNNING;
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new StopPanel();
		parentPanel.add(panel);
		state = PluginState.RUNNING;
	}

	@Override
	public void stop() {
		state = PluginState.STOPPED;
	}

	@Override
	public PluginState getState() {
		return state;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Plugin getNewInstance() {
		return new StopwatchPlugin();
	}
	
	
	public void pause(){
		super.pause();
		panel.stop();
		state = PluginState.PAUSED;
	}
	
	public void resume(){
		super.resume();
		panel.resume();
		state = PluginState.RUNNING;
	}

}
