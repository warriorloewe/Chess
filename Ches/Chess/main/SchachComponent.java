package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SchachComponent extends JComponent {
	
	private static final long serialVersionUID = 1L;
	Spielfeld[][] map;
	Rectangle window;
	public int offsetX;
	public int offsetY;
	ArrayList<BufferedImage> sprites;
	ArrayList<String> sprite_names;
	Color selected = new Color(120, 120, 120, 120);
	Color marked = new Color(10, 10, 10, 255);
	Color attackableField = new Color(255, 0, 0, 255); // en passant
	Color attackableEnemie = new Color(255, 0, 0, 120);
	public SchachComponent(Rectangle _window, int _offsetX, int _offsetY) {
		super();
		this.window = _window;
		this.offsetX = _offsetX;
		this.offsetY = _offsetY;
		sprites = new ArrayList<BufferedImage>();
		try {
			sprites.add(ImageIO.read(new File("Chess/rsc/background.jpg")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_king.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_king.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_queen.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_queen.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_pawn.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_pawn.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_rook.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_rook.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_knight.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_knight.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/white_bishop.png")));
			sprites.add(ImageIO.read(new File("Chess/rsc/black_bishop.png")));
		} catch (IOException e) {
			e.printStackTrace();
		} 
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
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(sprites.get(0), 0, 0, window.width, window.height, null);
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if((i + j) % 2 == 0) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.cyan);
				}
				g.fillRect(offsetX + map[i][j].bounds.x, offsetY + map[i][j].bounds.y, Spielfeld.width, Spielfeld.width);
				g.setColor(Color.black);
				g.drawString(map[i][j].name, offsetX + map[i][j].bounds.x + 2, offsetY + map[i][j].bounds.y + Spielfeld.width - 2);
				if(map[i][j].figur != null) {
					for(String str : sprite_names) {
						if(str.contains(map[i][j].figur.name)) {
							g.drawImage(sprites.get(sprite_names.indexOf(str) + 1), offsetX + map[i][j].bounds.x, offsetY + map[i][j].bounds.y, Spielfeld.width, Spielfeld.width, null);
							break;
						}
					}
				}
				if(map[i][j].marked) {
					g.setColor(marked);
					g.fillOval(offsetX + map[i][j].bounds.x + Spielfeld.width/4, offsetY + map[i][j].bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
				} else if(map[i][j].attackable) {
					if(map[i][j].figur == null) {
						g.setColor(attackableField);
					} else {
						g.setColor(attackableEnemie);
					}
					g.fillOval(offsetX + map[i][j].bounds.x + Spielfeld.width/4, offsetY + map[i][j].bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
				}
			}
		}
		if(GameEnvironment.selectedField != null) {
			g.setColor(selected);
			g.fillOval(offsetX + GameEnvironment.selectedField.bounds.x + Spielfeld.width/4, offsetY + GameEnvironment.selectedField.bounds.y + Spielfeld.width/4, Spielfeld.width/2, Spielfeld.width/2);
		}
	}
}