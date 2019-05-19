/*
 * R.LAKSHMI PRIYA - IMT2015037
 * V.MOHIT - IMT2015046
 * P.VAMSHI KRISHNA - IMT2015516
 */
package project;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JFrame;

//Game logic.
public class Tetris extends JFrame {
	private static final long serialVersionUID = 14L;
	private Board board;
	private SidePanel side;
	private static final int TYPE_COUNT = TileType.values().length;
	private boolean isPaused;
	private boolean isNewGame;
	private boolean isGameOver;
	private int level;
	private int score;
	private Random random;
	private Timer timer;
	private TileType currentType;
	private TileType nextType;
	private int currentCol;
	private int currentRow;
	private int currentRotation;
	private int dropCooldown = 25;
	private float gameSpeed;
	//creates tetris window.
	Tetris() {
		super("Tetris");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setFocusable(true);
		//initialize board and sidepanel.
		this.board = new Board(this);
		this.side = new SidePanel(this);
		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
								
				switch(e.getKeyCode()) {
				case KeyEvent.VK_R:
					resetGame();
					break;
				case KeyEvent.VK_Q:
					System.exit(0);
					break;
				case KeyEvent.VK_M:
					
				//while dropping, it sets the timer to 25cyclespersec.
				case KeyEvent.VK_DOWN:
					if(!isPaused && dropCooldown == 0) {
						timer.setCyclesPerSecond(25.0f);
					}
					break;
				case KeyEvent.VK_LEFT:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
						currentCol--;
					}
					break;
				case KeyEvent.VK_RIGHT:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
						currentCol++;
					}
					break;
				case KeyEvent.VK_UP:
					if(!isPaused) {
						rotatePiece((currentRotation == 0) ? 3 : currentRotation - 1);
					}
					break;
				case KeyEvent.VK_P:
					if(!isGameOver && !isNewGame) {
						isPaused = !isPaused;
						timer.setPaused(isPaused);
					}
					break;
				case KeyEvent.VK_ENTER:
					if(isGameOver || isNewGame) {
						resetGame();
					}
					break;
				}
			}	
		});
		//setting board and side panel relative to each other.
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	void startGame() {
		this.random = new Random();
		this.isNewGame = true;
		this.gameSpeed = 1.0f;
		this.timer = new Timer(gameSpeed);
		timer.setPaused(true);
		while(true) {
			//updates the timer.
			timer.update();
			if(timer.hasElapsedCycle()) {
				updateGame();
			}
			if(dropCooldown > 0) {
				dropCooldown--;
			}
			//force to repaint the board and sidepanel.
			renderGame();
		}
	}
	
	//main part.
	private void updateGame() {
		//to see if the piece can be moved to the next row or not.
		if(board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
			currentRow++;
		} else {
			//reached bottom so we need to add another piece.
			board.addPiece(currentType, currentCol, currentRow, currentRotation);
			//if on reaching the bottom, lines are cleared, then increase score.
			int cleared = board.checkLines();
			// 1 line cleared - 20 points.
			if(cleared > 0) {
				score += 10 << cleared;
			}
			//increase the speed slightly after each frame.
			gameSpeed += 0.025f;
			timer.setCyclesPerSecond(gameSpeed);
			timer.reset();
			level = (int)(gameSpeed * 1.70f);
			spawnPiece();
		}		
	}
	
	// Forces the BoardPanel and SidePanel to repaint. 
	private void renderGame() {
		board.repaint();
		side.repaint();
	}
	
	private void resetGame() {
		this.level = 1;
		this.score = 0;
		this.gameSpeed = 1.0f;
		this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
		this.isNewGame = false;
		this.isGameOver = false;		
		board.clear();
		timer.reset();
		timer.setCyclesPerSecond(gameSpeed);
		spawnPiece();
		}
		
	/**
	 * Spawns a new piece and resets our piece's variables to their default
	 * values.
	 */
	private void spawnPiece() {
		//next piece to use.
		this.currentType = nextType;
		this.currentCol = currentType.getSpawnColumn();
		this.currentRow = currentType.getSpawnRow();
		this.currentRotation = 0;
		this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
		//if spawnpiece is notvalid, then game is over because we reached the top.
		if(!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
			this.isGameOver = true;
			timer.setPaused(true);
		}		
	}
	
	private void rotatePiece(int newRotation) {
		int newColumn = currentCol;
		int newRow = currentRow;
		int left = currentType.getLeftSpawn(newRotation);
		int right = currentType.getRightSpawn(newRotation);
		int top = currentType.getTopSpawn(newRotation);
		int bottom = currentType.getBottomSpawn(newRotation);
		
		//if it is too far from the left or right, move the pieces away s that they won't come out of the board.
		if(currentCol < -left) {
			newColumn -= currentCol - left;
		} else if(currentCol + currentType.getDimension() - right >= Board.COL_COUNT) {
			newColumn -= (currentCol + currentType.getDimension() - right) - Board.COL_COUNT + 1;
		}
		if(currentRow < -top) {
			newRow -= currentRow - top;
		} else if(currentRow + currentType.getDimension() - bottom >= Board.ROW_COUNT) {
			newRow -= (currentRow + currentType.getDimension() - bottom) - Board.ROW_COUNT + 1;
		}
		
		// to see if the new position is correct,then it rotates.
		if(board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
			currentRotation = newRotation;
			currentRow = newRow;
			currentCol = newColumn;
		}
	}
	public boolean isPaused() {
		return isPaused;
	}
	public boolean isGameOver() {
		return isGameOver;
	}
	public boolean isNewGame() {
		return isNewGame;
	}
	public int getScore() {
		return score;
	}
	public int getLevel() {
		return level;
	}
	public TileType getPieceType() {
		return currentType;
	}
	public TileType getNextPieceType() {
		return nextType;
	}
	public int getPieceCol() {
		return currentCol;
	}
	public int getPieceRow() {
		return currentRow;
	}
	public int getPieceRotation() {
		return currentRotation;
	}
	public static void main(String[] args) {
		Tetris tetris = new Tetris();
		tetris.startGame();
	}

}

