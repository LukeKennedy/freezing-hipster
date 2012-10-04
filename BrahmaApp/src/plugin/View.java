package plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class View {

	private JFrame frame;
	private JPanel contentPane;
	private JLabel bottomLabel;
	private JEditorPane pluginInfo;
	private JTabbedPane pluginTabs;
	private JList<String> sideList;
	private Controller core;

	public View(Controller c) {
		core = c;
		setupUI();
	}

	private void setupUI() {
		bottomLabel = new JLabel("No plugins registered yet!");
		sideList = new JList<String>(core.getListModel());
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(200, 50));

		JPanel newPluginPanel = new JPanel(new BorderLayout());

		final JEditorPane newPluginInfo = new JEditorPane();
		newPluginInfo.setEditable(false);
		newPluginInfo.setText("NEW PLUGIN INFO HERE");
		JButton startNewPluginButton = new JButton();
		startNewPluginButton.setText("Start");
		newPluginPanel.add(newPluginInfo, BorderLayout.CENTER);
		newPluginPanel.add(startNewPluginButton, BorderLayout.SOUTH);
		pluginTabs = new JTabbedPane();

		final JButton pauseButton = new JButton();
		final JButton stopButton = new JButton();
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.add(pauseButton);
		buttonPanel.add(stopButton);
		pauseButton.setText("Pause");
		stopButton.setText("Stop");
		pluginInfo = new JEditorPane();
		pluginInfo.setEditable(false);
		pluginInfo.setText("RUNNING PLUGIN INFO HERE\n");

		JPanel runningPluginOptions = new JPanel(new BorderLayout());
		runningPluginOptions.add(pluginInfo, BorderLayout.NORTH);
		runningPluginOptions.add(buttonPanel, BorderLayout.SOUTH);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(200, 50));
		leftPanel.add(scrollPane, BorderLayout.NORTH);
		leftPanel.add(runningPluginOptions, BorderLayout.SOUTH);
		leftPanel.add(newPluginPanel, BorderLayout.CENTER);

		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = (JPanel) frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		contentPane.add(pluginTabs, BorderLayout.CENTER);
		contentPane.add(leftPanel, BorderLayout.WEST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);

		// Add action listeners
		startNewPluginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				core.startButtonAction();
			}
		});

		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				core.pauseButtonAction(pauseButton);
			}
		});

		pluginTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				core.tabChanged(pauseButton, stopButton);
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				core.stopButtonAction();
			}
		});

		sideList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;
						core.sideListChanged(newPluginInfo);
					}
				});
	}

	public void setVisible(boolean b) {
		frame.pack();
		frame.setVisible(b);
	}

	public void setStaus(String status) {
		this.bottomLabel.setText(status);
	}

	public int getSelectedIndex() {
		return sideList.getSelectedIndex();
	}

	public void addPlugin(String id, JPanel pluginPanel) {
		pluginTabs.addTab(id, pluginPanel);
	}

	public Component getSelectedTab() {
		return pluginTabs.getSelectedComponent();
	}

	public int getTabCount() {
		return pluginTabs.getTabCount();
	}

	public void setInfo(String string) {
		pluginInfo.setText(string);
	}

	public void removeSelectedTab() {
		pluginTabs.remove(getSelectedTab());
	}

	public JLabel getStatusBar() {
		return bottomLabel;
	}
}
