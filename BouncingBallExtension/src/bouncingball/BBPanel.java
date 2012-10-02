package bouncingball;

//File:  animation/bb/BBPanel.java
//Description: Panel to layout buttons and graphics area.
//Author: Fred Swartz
//Date:   February 2005

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/////////////////////////////////////////////////////////////////// BBPanel
class BBPanel extends JPanel {
	BallInBox m_bb; // The bouncing ball panel
	private JButton startButton;
	private JButton stopButton;

	// ========================================================== constructor
	/** Creates a panel with the controls and bouncing ball display. */
	BBPanel() {
		// ... Create components
		m_bb = new BallInBox();
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");

		// ... Add Listeners
		startButton.addActionListener(new StartAction());
		stopButton.addActionListener(new StopAction());

		// ... Layout inner panel with two buttons horizontally
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		// ... Layout outer panel with button panel above bouncing ball
		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(m_bb, BorderLayout.CENTER);
	}// end constructor

	// //////////////////////////////////// inner listener class StartAction
	class StartAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			m_bb.setAnimation(true);
		}
	}

	// ////////////////////////////////////// inner listener class StopAction
	class StopAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setAnimationState(false);
		}
	}

	public void setAnimationState(boolean b) {
		m_bb.setAnimation(b);
	}

	public void setEnabled(boolean b) {
		startButton.setEnabled(b);
		stopButton.setEnabled(b);
		setAnimationState(b);
	}
}// endclass BBPanel
