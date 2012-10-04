package plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import plugin.Plugin.PluginState;

public class PluginCore {
	private JFrame frame;
	private JPanel contentPane;
	private JLabel bottomLabel;
	private JList<String> sideList;
	private DefaultListModel<String> listModel;
	private HashMap<String, Plugin> idToPlugin;
	private HashMap<Integer, Plugin> tabToPlugin;
	PluginManager pluginManager;
	private JTabbedPane pluginTabs;
	RunningTimeThing updater;
	Thread updaterThread;
	private JEditorPane pluginInfo;

	public PluginCore() {
		idToPlugin = new HashMap<String, Plugin>();
		tabToPlugin = new HashMap<Integer, Plugin>();

		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = (JPanel) frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");

		listModel = new DefaultListModel<String>();
		sideList = new JList<String>(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(200, 50));

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(200, 50));

		JPanel newPluginPanel = new JPanel(new BorderLayout());
		JButton startNewPluginButton = new JButton();
		final JEditorPane newPluginInfo = new JEditorPane();
		newPluginInfo.setEditable(false);
		newPluginInfo.setText("NEW PLUGIN INFO HERE");
		startNewPluginButton.setText("Start");
		newPluginPanel.add(newPluginInfo, BorderLayout.CENTER);
		newPluginPanel.add(startNewPluginButton, BorderLayout.SOUTH);

		JPanel runningPluginOptions = new JPanel(new BorderLayout());
		pluginInfo = new JEditorPane();
		final JButton pauseButton = new JButton();
		final JButton stopButton = new JButton();
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.add(pauseButton);
		buttonPanel.add(stopButton);
		pauseButton.setText("Pause");
		stopButton.setText("Stop");
		pluginInfo.setEditable(false);
		pluginInfo.setText("RUNNING PLUGIN INFO HERE\n");
		runningPluginOptions.add(pluginInfo, BorderLayout.NORTH);
		runningPluginOptions.add(buttonPanel, BorderLayout.SOUTH);

		pluginTabs = new JTabbedPane();

		leftPanel.add(scrollPane, BorderLayout.NORTH);
		leftPanel.add(runningPluginOptions, BorderLayout.SOUTH);
		leftPanel.add(newPluginPanel, BorderLayout.CENTER);

		contentPane.add(pluginTabs, BorderLayout.CENTER);
		contentPane.add(leftPanel, BorderLayout.WEST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);

		updater = new RunningTimeThing(null, bottomLabel);

		// Add action listeners
		startNewPluginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// List has finalized selection, let's process further
				int index = sideList.getSelectedIndex();
				if (index == -1) {
					return;
				}
				String id = listModel.elementAt(index);
				Plugin plugin = idToPlugin.get(id).getNewInstance();
				JPanel pluginPanel = new JPanel();
				plugin.layout(pluginPanel);
				tabToPlugin.put(pluginPanel.hashCode(), plugin);
				pluginTabs.addTab(plugin.getId(), pluginPanel);
				plugin.start();

				bottomLabel.setText("The " + plugin.getId() + " is running!");
			}
		});

		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int hash = pluginTabs.getSelectedComponent().hashCode();
				Plugin p = tabToPlugin.get(hash);
				PluginState state = p.getState();
				if (state == PluginState.RUNNING) {
					p.pause();
					pauseButton.setText("Resume");
				} else if (state == PluginState.PAUSED) {
					p.resume();
					pauseButton.setText("Pause");
				}
				pluginInfo.setText(p.getId() + " is currently\n" + p.getState() + "!");
			}
		});

		pluginTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				if (pluginTabs.getTabCount() < 1) {
					pauseButton.setEnabled(false);
					stopButton.setEnabled(false);
					updater.stop();
					updaterThread=null;
					bottomLabel.setText(" ");
					pluginInfo.setText("\n");
					return;
				}

				pauseButton.setEnabled(true);
				stopButton.setEnabled(true);
				int hash = pluginTabs.getSelectedComponent().hashCode();
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
				pluginInfo.setText(p.getId() + " is currently\n" + p.getState() + "!");
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int hash = pluginTabs.getSelectedComponent().hashCode();
				Plugin p = tabToPlugin.get(hash);
				p.stop();
				tabToPlugin.remove(p.hashCode());
				pluginTabs.remove(pluginTabs.getSelectedComponent());

			}
		});

		sideList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {

						// If the list is still updating, return
						if (e.getValueIsAdjusting())
							return;
						int index = sideList.getSelectedIndex();
						String id = listModel.elementAt(index);
						Plugin plugin = idToPlugin.get(id);
						if (plugin == null)
							return;
						newPluginInfo.setText(plugin.getDescription());
					}
				});

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
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(false);
			}
		});
	}

	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		this.bottomLabel.setText("The " + plugin.getId()
				+ " plugin has been recently added!");
	}

	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		plugin.stop();
		this.bottomLabel.setText("The " + plugin.getId()
				+ " plugin has been recently removed!");
	}
}
