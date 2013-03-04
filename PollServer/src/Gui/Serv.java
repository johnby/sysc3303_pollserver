package Gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
//import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.TextArea;

public class Serv extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Serv frame = new Serv();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Serv() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 636, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 159, 0, 0);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblPollserverV = new JLabel("Server v0.1");
		lblPollserverV.setBounds(12, 13, 146, 16);
		contentPane.add(lblPollserverV);
		
		JButton btnFetchPolls = new JButton("Port");
		btnFetchPolls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnFetchPolls.setBounds(5, 42, 141, 25);
		contentPane.add(btnFetchPolls);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(157, 42, 449, 22);
		contentPane.add(textArea);
		
		JButton btnPolls = new JButton("Polls");
		btnPolls.setBounds(5, 90, 97, 25);
		contentPane.add(btnPolls);
		
		TextArea textArea_1 = new TextArea();
		textArea_1.setBounds(157, 90, 449, 134);
		contentPane.add(textArea_1);
		
		JButton btnDetails = new JButton("Details");
		btnDetails.setBounds(5, 166, 97, 25);
		contentPane.add(btnDetails);
		
		JLabel label = new JLabel("Output");
		label.setBounds(341, 68, 71, 16);
		contentPane.add(label);
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(5, 128, 97, 25);
		contentPane.add(btnStart);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(5, 204, 97, 25);
		contentPane.add(btnStop);
	}
}
