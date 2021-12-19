package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SchachComponent extends JComponent {
	
	private static final long serialVersionUID = 1L;
	public int offsetX;
	public int offsetY;
	ArrayList<BufferedImage> sprites;
	ArrayList<String> sprite_names;
	GameEnvironment ge;
	
	Color selected = new Color(120, 120, 120, 120);
	Color marked = new Color(10, 10, 10, 255);
	Color attackableField = new Color(255, 0, 0, 255); // en passant
	Color attackableEnemie = new Color(255, 0, 0, 120);
	Font time = new Font(Font.SERIF, 30, 80);
	Font winner = new Font(Font.SERIF, 30, 75);
	Rectangle window;
	
	public SchachComponent(Rectangle _window, int _offsetX, int _offsetY, GameEnvironment _ge, SchachFrame sf) {
		super();
		this.ge = _ge;
		this.window = _window;
		this.offsetX = _offsetX;
		this.offsetY = _offsetY;
		sprite_names = new ArrayList<String>();
		sprite_names.add("white_king");
		sprite_names.add("black_king");
		sprite_names.add("white_queen");
		sprite_names.add("black_queen");
		sprite_names.add("white_pawn");
		sprite_names.add("black_pawn");
		sprite_names.add("white_rook");
		sprite_names.add("black_rook");
		sprite_names.add("white_knight");
		sprite_names.add("black_knight");
		sprite_names.add("white_bishop");
		sprite_names.add("black_bishop");
		sprites = new ArrayList<BufferedImage>();
		try {
			for(String s : sprite_names) {
				sprites.add(ImageIO.read(new File("Chess/rsc/" + s + ".png")));
			}
			sprites.add(ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("rsc/background.jpg")));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fehler beim Laden der Bilder");
			e.printStackTrace();
			System.exit(0);
		} 
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(sprites.get(sprites.size()-1), 0, 0, window.width, window.height, null);
		for(int i = 0; i < ge.map[0].length; i++) {
			for(int j = 0; j < ge.map.length; j++) {
				if((i + j) % 2 == 0) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.cyan);
				}
				g.fillRect(offsetX + ge.map[i][j].bounds.x, offsetY + ge.map[i][j].bounds.y, Spielfeld.width, Spielfeld.width);
				g.setColor(Color.black);
				g.drawString(ge.map[i][j].name, offsetX + ge.map[i][j].bounds.x + 2, offsetY + ge.map[i][j].bounds.y + Spielfeld.width - 2);
				if(ge.map[i][j].figure != null) {
					if(!(ge.map[i][j].figure == ge.selectedFigure && ge.dragNDrop)) {
						/*
						 * draw every single piece except the on being moved around by the mouse on their squares
						 */
						for(String str : sprite_names) {
							if(str.contains(ge.map[i][j].figure.name)) {
								g.drawImage(sprites.get(sprite_names.indexOf(str)), offsetX + ge.map[i][j].bounds.x, offsetY + ge.map[i][j].bounds.y, Spielfeld.width, Spielfeld.width, null);
								break;
							}
						}
					}
					
				}
				if(ge.map[i][j].marked) {
					g.setColor(marked);
					g.fillOval(offsetX + ge.map[i][j].bounds.x + Spielfeld.width/4, offsetY + ge.map[i][j].bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
				} else if(ge.map[i][j].attackable) {
					if(ge.map[i][j].figure == null) {
						g.setColor(attackableField);
					} else {
						g.setColor(attackableEnemie);
					}
					g.fillOval(offsetX + ge.map[i][j].bounds.x + Spielfeld.width/4, offsetY + ge.map[i][j].bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
				}
			}
		}
		if(ge.selectedField != null) {
			g.setColor(selected);
			g.fillOval(offsetX + ge.selectedField.bounds.x + Spielfeld.width/4, offsetY + ge.selectedField.bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
		}
		if(ge.selectedFigure != null && ge.dragNDrop) {
			/*
			 * draw the piece being moved around by the mouse at the coordinates of the mouse
			 */
			for(String str : sprite_names) {
				if(str.contains(ge.selectedFigure.name)) {
					g.drawImage(sprites.get(sprite_names.indexOf(str)), ge.mouseX - Spielfeld.width/2, ge.mouseY - Spielfeld.width/2, Spielfeld.width, Spielfeld.width, null);
				}
			}
		}
		/*
		 * drawing the clock
		 */
		g.setColor(Color.DARK_GRAY);
		g.fillRect(20, window.height/2 - 200, 400, 400);
		g.setColor(Color.gray);
		g.fillRect(20, window.height/2 - 20, 400, 40);
		g.setFont(time);
		if(ge.blackTimer != null && ge.whiteTimer != null) {
			g.drawString(ge.blackTimer.time/60000 + " : " + ge.blackTimer.time/1000 % 60, 120, window.height/2 - 100);
			g.drawString(ge.whiteTimer.time/60000 + " : " + ge.whiteTimer.time/1000 % 60, 120, window.height/2 + 130);
		}
		repaint();
	}
}