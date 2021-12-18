package main;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
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
		contentPane = new JPanel();
		contentPane.setBounds(window);
		contentPane.setVisible(true);
		
		sc = new SchachComponent(window, window.width/2 - 500, window.height/2 - 520, ge);
		sc.setBounds(window);
		sc.setVisible(true);
		add(sc);
		
		ge = new GameEnvironment(sc, time, increment, dad);
		sc.ge = ge;
		addMouseListener(ge);
		addMouseMotionListener(ge);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
