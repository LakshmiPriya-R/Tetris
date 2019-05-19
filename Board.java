package project;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/*
 * Board.java is responsible for drawing the board and tiles on to the board.
 */
public class Board extends JPanel {

	private Tetris tetris;
	Font font1 = new Font("Courier", Font.BOLD,16);
	Font font2 = new Font("Courier", Font.BOLD,18);
	Font font3 = new Font("Courier", Font.ITALIC,16);
	private static final long serialVersionUID = 19L;
 	private static final int BORDER_WIDTH = 5;
	public static final int COL_COUNT = 15;
	private static final int VISIBLE_ROW_COUNT = 25;
	private static final int HIDDEN_ROW_COUNT = 2;
	public static final int ROW_COUNT = VISIBLE_ROW_COUNT + HIDDEN_ROW_COUNT;
	public static final int TILE_SIZE = 27;
	private static final int CENTER_X = COL_COUNT * TILE_SIZE / 2;
	private static final int CENTER_Y = VISIBLE_ROW_COUNT * TILE_SIZE / 2;
	public static final int PANEL_WIDTH = COL_COUNT * TILE_SIZE + BORDER_WIDTH * 2;
	public static final int PANEL_HEIGHT = VISIBLE_ROW_COUNT * TILE_SIZE + BORDER_WIDTH * 2;
	
	//Tiles that makeup the board.
	private TileType[][] tiles;
		
	//New board instance.
	public Board(Tetris tetris) {
		this.tetris = tetris;
		this.tiles = new TileType[ROW_COUNT][COL_COUNT];
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setBackground(Color.BLACK);
	}
	
	public boolean isValidAndEmpty(TileType type, int x, int y, int rotation) {
		
		//valid column.
		if(x < -type.getLeftSpawn(rotation) || x + type.getDimension() - type.getRightSpawn(rotation) >= COL_COUNT) {
			return false;
		}
		
		//valid row.
		if(y < -type.getTopSpawn(rotation) || y + type.getDimension() - type.getBottomSpawn(rotation) >= ROW_COUNT) {
			return false;
		}
		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation) && isOccupied(x + col, y + row)) {
					return false;
				}
			}
		}
		return true;
	}
	
	//Resets the board.
	public void clear() {
		for(int i = 0; i < ROW_COUNT; i++) {
			for(int j = 0; j < COL_COUNT; j++) {
				tiles[i][j] = null;
			}
	}
	}
	
	public void addPiece(TileType type, int x, int y, int rotation) {
		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation)) {
					setTile(col + x, row + y, type);
				}
			}
		}
	}
	
	private boolean isOccupied(int x, int y) {
		return tiles[y][x] != null;
	}
	private TileType getTile(int x, int y) {
		return tiles[y][x];
	}
	private void setTile(int  x, int y, TileType type) {
		tiles[y][x] = type;
	}
	private boolean checkLine(int line) {
		//returns true if the row is full.
		for(int col = 0; col < COL_COUNT; col++) {
			if(!isOccupied(col, line)) {
				return false;
			}
		}
		//remove the line if it is filled.
		for(int row = line - 1; row >= 0; row--) {
			for(int col = 0; col < COL_COUNT; col++) {
				setTile(col, row + 1, getTile(col, row));
			}
		}
		return true;
	}
	
	//Here this updates score and shifts to next row.
	public int checkLines() {
		int completedLines = 0;
		for(int row = 0; row < ROW_COUNT; row++) {
			if(checkLine(row)) {
				completedLines++;
			}
		}
		return completedLines;
	}
	
	//Graphical part.
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(tetris.isPaused()) {
			g.setFont(font2);
			g.setColor(Color.WHITE);
			String msg = "PAUSED";
			g.drawString(msg, CENTER_X - g.getFontMetrics().stringWidth(msg) / 2, CENTER_Y);
		} 
		else if(tetris.isNewGame() || tetris.isGameOver()) {
			g.setFont(font2);
			g.setColor(Color.WHITE);
			if(tetris.isNewGame()){
				String msg1 = "TETRIS";
				g.drawString(msg1, CENTER_X - g.getFontMetrics().stringWidth(msg1) / 2, 250);
				g.setFont(font3);
				String msg2 = "Press Enter to Play";
				g.drawString(msg2, CENTER_X - g.getFontMetrics().stringWidth(msg2) / 2, 350);
			}
			else{
				String msg = "GAME OVER";
				g.drawString(msg, CENTER_X - g.getFontMetrics().stringWidth(msg) / 2, 250);
				g.setFont(font3);
				String msg2 = "Press Enter to Play Again";
				g.drawString(msg2, CENTER_X - g.getFontMetrics().stringWidth(msg2) / 2, 350);
			}
		} 
		else {
			
			/*
			 * Draw the tiles onto the board.
			 */
			for(int x = 0; x < COL_COUNT; x++) {
				for(int y = HIDDEN_ROW_COUNT; y < ROW_COUNT; y++) {
					TileType tile = getTile(x, y);
					if(tile != null) {
						drawTile(tile, x * TILE_SIZE, (y - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
					}
				}
			}
			
			//Draws piece "ON THE BOARD".
			TileType type = tetris.getPieceType();
			int pieceCol = tetris.getPieceCol();
			int pieceRow = tetris.getPieceRow();
			int rotation = tetris.getPieceRotation();
			for(int col = 0; col < type.getDimension(); col++) {
				for(int row = 0; row < type.getDimension(); row++) {
					if(pieceRow + row >= 2 && type.isTile(col, row, rotation)) {
						drawTile(type, (pieceCol + col) * TILE_SIZE, (pieceRow + row - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
					}
				}
			}
			
			//Draws lines so that tiles can be visualized better.
			g.setColor(Color.DARK_GRAY);
			for(int x = 0; x < COL_COUNT; x++) {
				for(int y = 0; y < VISIBLE_ROW_COUNT; y++) {
					g.drawLine(0, y * TILE_SIZE, COL_COUNT * TILE_SIZE, y * TILE_SIZE);
					g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, VISIBLE_ROW_COUNT * TILE_SIZE);
				}
			}
		}
		//outline
		g.setColor(Color.ORANGE);
		g.drawRect(0, 0, TILE_SIZE * COL_COUNT, TILE_SIZE * VISIBLE_ROW_COUNT);
	}
	private void drawTile(TileType type, int x, int y, Graphics g) {
		drawTile(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, g);
	}
	private void drawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {
		//Fill the entire tile with the base color. 
		g.setColor(base);
		g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
	}
}
