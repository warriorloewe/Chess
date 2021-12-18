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
		
		ge = new GameEnvironment();
		sc = new SchachComponent(window, window.width/2 - 500, window.height/2 - 520, ge, this);
		ge.sc = sc;
		sc.setBounds(window);
		sc.setVisible(true);
		add(sc);
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addMouseListener(ge);
		addMouseMotionListener(ge);
	}
}
