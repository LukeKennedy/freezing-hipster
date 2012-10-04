package plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;

public class PluginTimer implements Runnable {

	private Plugin plugin;
	private JLabel label;
	private DateFormat formatter;
	private boolean threadIsRunning;

	public PluginTimer(Plugin p, JLabel l) {
		threadIsRunning = false;
		plugin = p;
		label = l;
		formatter = new SimpleDateFormat("mm:ss:SSS");
	}

	public void setPlugin(Plugin p) {
		plugin = p;
		threadIsRunning = true;
	}

	public void stop() {
		threadIsRunning = false;
	}

	@Override
	public void run() {
		while (threadIsRunning) {
			Date date = new Date(plugin.getRunningTime());
			String dateFormatted = formatter.format(date);
			label.setText("The " + plugin.getId()
					+ " plugin has been running for " + dateFormatted + "!");
		}
	}

}
