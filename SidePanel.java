package project;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

//This class is responsible for displaying score, level and next piece and so on...
public class SidePanel extends JPanel{
	
	private Tetris tetris;
	Font font1 = new Font("Courier", Font.BOLD,12);
	Font font2 = new Font("Courier", Font.BOLD,16);
	Font font3 = new Font("Courier", Font.BOLD,14);
	Font font4 = new Font("Courier", Font.ITALIC,15);
	private static final long serialVersionUID = 14L;
	private static final int TILE_SIZE = Board.TILE_SIZE >> 1; //on next preview.
	private static final int TILE_COUNT = 8;
	private static final int PREVIEW_X = 100; //center of next preview.
	private static final int PREVIEW_Y = 150; //center of next preview.
	private static final int PREVIEW_SIZE = (TILE_SIZE * TILE_COUNT >> 1);
	private static final int TEXT_GAP = 30;//gap between texts.
	private static final Color PREVIEW_COLOR = new Color(255,215,0);//preview text color

	//New SIDE instance.
	public SidePanel(Tetris tetris) {
		this.tetris = tetris;
		
		setPreferredSize(new Dimension(250, Board.PANEL_HEIGHT));
		setBackground(Color.BLACK);
	}
	
	//GRAPHICS PART.
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(PREVIEW_COLOR);
		//store dimensions(y-coordinate) of strings.
		int offset;
		//for next piece.
		g.setFont(font2);
		g.drawString("NEXT PIECE:", 15, 70);
		g.drawRect(PREVIEW_X - PREVIEW_SIZE, PREVIEW_Y - PREVIEW_SIZE, PREVIEW_SIZE * 2, PREVIEW_SIZE * 2);
		//for level and score.
		g.setFont(font2);
		g.drawString("LEVEL AND SCORE", 15, offset = 250);
		g.setFont(font3);
		g.drawString("Level: " + tetris.getLevel(), 30, offset += TEXT_GAP);
		g.drawString("Score: " + tetris.getScore(), 30, offset += TEXT_GAP);
		//for instructions.
		g.setFont(font2);
		g.drawString("Controls", 15, offset = 375);
		g.setFont(font4);
		g.drawString("P - Pause Game", 25, offset += TEXT_GAP);
		g.drawString("R - Reset Game", 25, offset += TEXT_GAP);
		g.drawString("Q - Quit Game", 25, offset += TEXT_GAP);
		g.drawString("LEFT ARROW - Move Left", 25, offset += TEXT_GAP);
		g.drawString("RIGHT ARROW - Move Right", 25, offset += TEXT_GAP);
		g.drawString("UP ARROW - Rotate", 25, offset += TEXT_GAP);
		g.drawString("DOWN ARROW - Drop", 25, offset += TEXT_GAP);
		
		//for next piece type
		TileType type = tetris.getNextPieceType();
		if(!tetris.isGameOver() && type != null) {
			int cols = type.getCols();
			int rows = type.getRows();
			int dimension = type.getDimension();
			int startX = (PREVIEW_X - (cols * TILE_SIZE / 2));
			int startY = (PREVIEW_Y - (rows * TILE_SIZE / 2));
			//we use rotation zero because default is set to be zero.
			int top = type.getTopSpawn(0);
			int left = type.getLeftSpawn(0);
			//drawing piece on the preview.
			for(int row = 0; row < dimension; row++) {
				for(int col = 0; col < dimension; col++) {
					if(type.isTile(col, row, 0)) {
						drawTile(type, startX + ((col - left) * TILE_SIZE), startY + ((row - top) * TILE_SIZE), g);
					}
				}
			}
		}
	}
	private void drawTile(TileType type, int x, int y, Graphics g) {
		g.setColor(type.getBaseColor());
		g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
	}
}

