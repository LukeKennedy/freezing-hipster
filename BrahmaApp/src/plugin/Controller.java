package plugin;

import java.awt.EventQueue;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.ListModel;

import plugin.Plugin.PluginState;

public class Controller {

	private DefaultListModel<String> listModel;
	private HashMap<String, Plugin> idToPlugin;
	private HashMap<Integer, Plugin> tabToPlugin;
	private PluginManager pluginManager;
	private PluginTimer updater;
	private Thread updaterThread;
	private View view;

	public Controller() {
		idToPlugin = new HashMap<String, Plugin>();
		tabToPlugin = new HashMap<Integer, Plugin>();
		listModel = new DefaultListModel<String>();
		view = new View(this);
		updater = new PluginTimer(null, view.getStatusBar());
		try {
			this.pluginManager = new PluginManager(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread thread = new Thread(this.pluginManager);
		thread.start();
	}

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				view.setVisible(true);
			}
		});
	}

	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				view.setVisible(false);
			}
		});
	}

	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		view.setStaus("The " + plugin.getId()
				+ " plugin has been recently added!");

	}

	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		plugin.stop();
		view.setStaus("The " + plugin.getId()
				+ " plugin has been recently removed!");
	}

	public void startButtonAction() {
		int index = view.getSelectedIndex();
		if (index == -1) {
			return;
		}
		String id = listModel.elementAt(index);
		Plugin plugin = idToPlugin.get(id).getNewInstance();
		JPanel pluginPanel = new JPanel();
		plugin.layout(pluginPanel);
		tabToPlugin.put(pluginPanel.hashCode(), plugin);
		view.addPlugin(plugin.getId(), pluginPanel);
		plugin.start();
		view.setStaus("The " + plugin.getId() + " is running!");
	}

	public void pauseButtonAction(JButton pauseButton) {
		int hash = view.getSelectedTab().hashCode();
		Plugin p = tabToPlugin.get(hash);
		PluginState state = p.getState();
		if (state == PluginState.RUNNING) {
			p.pause();
			pauseButton.setText("Resume");
		} else if (state == PluginState.PAUSED) {
			p.resume();
			pauseButton.setText("Pause");
		}
		view.setInfo(p.getId() + " is currently\n" + p.getState() + "!");
	}

	public void tabChanged(JButton pauseButton, JButton stopButton) {
		if (view.getTabCount() < 1) {
			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);
			updater.stop();
			updaterThread = null;
			view.setStaus(" ");
			view.setInfo("\n");
			return;
		}

		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		int hash = view.getSelectedTab().hashCode();
		Plugin p = tabToPlugin.get(hash);

		if (updaterThread == null) {
			updater.setPlugin(p);
			updaterThread = new Thread(updater);
			updaterThread.start();
		}
		updater.setPlugin(p);

		PluginState state = p.getState();
		if (state == PluginState.RUNNING) {
			pauseButton.setText("Pause");
		} else if (state == PluginState.PAUSED) {
			pauseButton.setText("Resume");
		}
		view.setInfo(p.getId() + " is currently\n" + p.getState() + "!");
	}

	public void stopButtonAction() {
		int hash = view.getSelectedTab().hashCode();
		Plugin p = tabToPlugin.get(hash);
		p.stop();
		tabToPlugin.remove(p.hashCode());
		view.removeSelectedTab();

	}

	public void sideListChanged(JEditorPane newPluginInfo) {
		int index = view.getSelectedIndex();
		String id = listModel.elementAt(index);
		Plugin plugin = idToPlugin.get(id);
		if (plugin == null)
			return;
		newPluginInfo.setText(plugin.getDescription());
	}

	public ListModel<String> getListModel() {
		return listModel;
	}
}
