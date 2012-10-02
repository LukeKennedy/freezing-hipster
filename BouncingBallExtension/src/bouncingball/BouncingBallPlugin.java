package bouncingball;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import plugin.Plugin;

/**
 * An extension plugin.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 * 
 */
public class BouncingBallPlugin extends Plugin {
	public static final String PLUGIN_ID = "Bouncing Ball";

	private BBPanel panel;
	private PluginState state;
	private String description = "A simple bouncing ball animation!";

	public BouncingBallPlugin() {
		super(PLUGIN_ID);
		state = PluginState.RUNNING;
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new BBPanel();
		parentPanel.add(panel);
		state = PluginState.RUNNING;
	}

	@Override
	public void start() {
		super.start();
		state = PluginState.RUNNING;
	}

	@Override
	public void stop() {
		state = PluginState.STOPPED;
	}

	// For now we need to declare dummy main method
	// to include in manifest file
	public static void main(String[] args) {
	}

	@Override
	public void pause() {
		super.pause();
		state = PluginState.PAUSED;
		panel.setEnabled(false);
	}



	@Override
	public PluginState getState() {
		return state;
	}

	@Override
	public void resume() {
		super.resume();
		state = PluginState.RUNNING;
		panel.setEnabled(true);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Plugin getNewInstance() {
		return new BouncingBallPlugin();
	}
}
