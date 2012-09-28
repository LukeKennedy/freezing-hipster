package plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PluginCore {
	// GUI Widgets that we will need
	private JFrame frame;
	private JPanel contentPane;
	private JLabel bottomLabel;
	private JList sideList;
	private DefaultListModel<String> listModel;
//	private JPanel centerEnvelope;
	
	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private HashMap<Integer, Plugin> tabToPlugin;
//	private Plugin currentPlugin;
	
	// Plugin manager
	PluginManager pluginManager;
	private JTabbedPane pluginTabs;
	
	public PluginCore() {
		idToPlugin = new HashMap<String, Plugin>();
		tabToPlugin = new HashMap<Integer, Plugin>();
		
		// Lets create the elements that we will need
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		contentPane = (JPanel)frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");
		
		listModel = new DefaultListModel<String>();
		sideList = new JList(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(200, 50));
		
		// Create center display area
//		centerEnvelope = new JPanel(new BorderLayout());
//		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(200, 50));
		
		
		
		
		
		JPanel newPluginPanel = new JPanel(new BorderLayout());
		JButton startNewPluginButton = new JButton();
		JEditorPane newPluginInfo = new JEditorPane();
		newPluginInfo.setEditable(false);
		newPluginInfo.setText("NEW PLUGIN INFO HERE");
		startNewPluginButton.setText("Start");
		newPluginPanel.add(newPluginInfo, BorderLayout.CENTER);
		newPluginPanel.add(startNewPluginButton, BorderLayout.SOUTH);
		
		JPanel runningPluginOptions = new JPanel(new BorderLayout());
		JEditorPane pluginInfo = new JEditorPane();
		JButton pauseButton = new JButton();
		JButton stopButton = new JButton();
		JPanel buttonPanel = new JPanel(new GridLayout());
		buttonPanel.add(pauseButton);
		buttonPanel.add(stopButton);
		pauseButton.setText("Pause/Start");
		stopButton.setText("Stop");
		pluginInfo.setEditable(false);
		pluginInfo.setText("RUNNING PLUGIN INFO HERE");
		runningPluginOptions.add(pluginInfo, BorderLayout.NORTH);
		runningPluginOptions.add(buttonPanel, BorderLayout.SOUTH);
//		runningPluginOptions.add(pauseButton, BorderLayout.SOUTH);
		
		pluginTabs = new JTabbedPane();
		
		leftPanel.add(scrollPane, BorderLayout.NORTH);
		leftPanel.add(runningPluginOptions, BorderLayout.SOUTH); 
		leftPanel.add(newPluginPanel, BorderLayout.CENTER);
		
		// Lets lay them out, contentPane by default has BorderLayout as its layout manager
		contentPane.add(pluginTabs, BorderLayout.CENTER);
		contentPane.add(leftPanel, BorderLayout.WEST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);
		
		// Add action listeners
		startNewPluginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				 List has finalized selection, let's process further
				int index = sideList.getSelectedIndex();
				if(index == -1)
				{
					return;
				}
				String id = listModel.elementAt(index);
				Plugin plugin = idToPlugin.get(id);
				JPanel pluginPanel = new JPanel();
				pluginTabs.addTab(plugin.getId(), pluginPanel);
				plugin.layout(pluginPanel);
				contentPane.revalidate();
				contentPane.repaint();
				tabToPlugin.put(pluginPanel.hashCode(), plugin);
				// Start the plugin
				plugin.start();
				
				bottomLabel.setText("The " + plugin.getId() + " is running!");
				
			}
		});
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int hash = pluginTabs.getSelectedComponent().hashCode();
				Plugin p = tabToPlugin.get(hash);
				System.out.println(p.getId());
				p.pause();
			}
		});
		
		
		
		sideList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// If the list is still updating, return
//				if(e.getValueIsAdjusting())
//					return;
//				
//				// List has finalized selection, let's process further
//				int index = sideList.getSelectedIndex();
//				String id = listModel.elementAt(index);
//				Plugin plugin = idToPlugin.get(id);
//				
//				if(plugin == null || plugin.equals(currentPlugin))
//					return;
//				
//				// Stop previously running plugin
//				if(currentPlugin != null)
//					currentPlugin.stop();
//				
//				// The newly selected plugin is our current plugin
//				currentPlugin = plugin;
//				
//				// Clear previous working area
////				centerEnvelope.removeAll();
//				
//				// Create new working area
//				JPanel centerPanel = new JPanel();
////				centerEnvelope.add(centerPanel, BorderLayout.CENTER); 
//				
//				// Ask plugin to layout the working area
//				currentPlugin.layout(centerPanel);
//				contentPane.revalidate();
//				contentPane.repaint();
//				
//				// Start the plugin
//				currentPlugin.start();
//				
//				bottomLabel.setText("The " + currentPlugin.getId() + " is running!");
			}
		});
		
		// Start the plugin manager now that the core is ready
		try {
			this.pluginManager = new PluginManager(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Thread thread = new Thread(this.pluginManager);
		thread.start();
	}
	
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.setVisible(false);
			}
		});
	}
	
	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently added!");
	}
	
	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		
		// Stop the plugin if it is still running
		plugin.stop();

		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently removed!");
	}
}
