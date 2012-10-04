package stopwatch;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StopPanel extends JPanel {
	private JButton start;
	private JButton reset;
	private JLabel timePane;
	private long time;
	private long startTime;
	private boolean running;
	private boolean state;

	public StopPanel() {
		start = new JButton("Start!");
		reset = new JButton("Reset!");
		timePane = new JLabel();
		timePane.setText("00:00:000");
		Font f = new Font(Font.SANS_SERIF, 1, 80);
		timePane.setFont(f);
		setLayout(new BorderLayout());
		add(start, BorderLayout.NORTH);
		add(reset, BorderLayout.SOUTH);
		add(timePane, BorderLayout.CENTER);
		running = false;
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stuff();
			}
		});
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				time = 0;
				timePane.setText("00:00:000");
			}
		});

	}

	private void stuff() {
		if (running) {
			start.setText("Start!");
			running = false;
		} else {
			running = true;
			start.setText("Pause!");
			new Thread(new Runnable() {
				public void run() {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"mm:ss:SSS");
					startTime = System.currentTimeMillis();
					while (running) {
						time += System.currentTimeMillis() - startTime;
						startTime = System.currentTimeMillis();
						Date date = new Date(time);
						String dateFormatted = formatter.format(date);
						timePane.setText(dateFormatted);

					}
				}
			}).start();
		}
	}
	
	public void stop(){
		state = running;
		running = false;
		start.setEnabled(false);
		reset.setEnabled(false);
	}
	
	public void resume(){
		running = !state;
		stuff();
		start.setEnabled(true);
		reset.setEnabled(true);
	}
}