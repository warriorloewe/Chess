package main;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;

public class SchachFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static Rectangle window = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	GameEnvironment ge;
	SchachComponent sc;
	
	public SchachFrame() {
		super();
		setBounds(window);
		setTitle("Leo's Chess Game");
		
		ge = new GameEnvironment();
		sc = new SchachComponent(window, window.width/2 - 500, window.height/2 - 520, ge, this);
		sc.setBounds(window);
		sc.setVisible(true);
		add(sc);
		ge.sc = sc;
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		addMouseListener(ge);
		addMouseMotionListener(ge);
	}
}
