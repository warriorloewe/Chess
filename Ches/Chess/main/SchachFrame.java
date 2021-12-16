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
	
	public SchachFrame() {
		super();
		setBounds(window);
		
		sc = new SchachComponent(window, window.width/2 - 500, window.height/2 - 520);
		sc.setBounds(window);
		sc.setVisible(true);
		add(sc);
		
		ge = new GameEnvironment(sc);
		addMouseListener(ge);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
