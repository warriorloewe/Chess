package main;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SchachFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	public static Rectangle window = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	JPanel contentPane;
	GameEnvironment ge;
	SchachComponent sc;
	
	public SchachFrame(int time, int increment, boolean dad) {
		super();
		setBounds(window);
		
		sc = new SchachComponent(window, window.width/2 - 500, window.height/2 - 520, ge);
		sc.setBounds(window);
		sc.setVisible(true);
		add(sc);
		
		ge = new GameEnvironment(sc, time, increment, dad);
		sc.ge = ge;
		addMouseListener(ge);
		addMouseMotionListener(ge);
		
		JButton close = new JButton();
		close.setBounds(window.width - 200, window.height - 200, 100, 50);
		close.setText("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ge.finalize();
				sc.finalize();
			}
		});
		//add(close);
		close.setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
