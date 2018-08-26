import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to for creating Game object.
 * @author DirgleHurbleHerb
 *
 */

public class Game {
	private Map<Position, Cell> currentBuffer = new HashMap<Position, Cell>();
	private Map<Position, Cell> backBuffer = new HashMap<Position, Cell>();
	private int cellSize;
	private Map<String, List<int[]>> patterns;

	/**
	 * Constructor for the game.
	 * @param cellSize
	 * cellSize is the length of width and height of each Cell object.
	 */
	public Game(int cellSize) {
		this.cellSize = cellSize;
		parsePatterns();
		defineInitialPattern();
		
		//Test getDeadNeighbours method.
		getDeadNeighbours(100,100);
	}

	/** Getter for currentBuffer */
	public Collection<Cell> getCurrentBuffer() {

		return currentBuffer.values();
	}

	/**
	 * update method first checks the Cells and then swaps the buffers.
	 * update method calls checkCells method and swapbuffer methods.
	 * checkCells checks if the cell will live or die and checks 
	 * swapBuffer creates a copy of backBuffer and sets it into currentBuffer. 
	 * Then backBuffer is cleared.
	 */
	public void update() {
		//defineInitialPattern();//remove for production
		checkCells();
		swapBuffers();
		updateCells();
	}
	public void updateCells() {
		for(Cell cell:currentBuffer.values()) {
			cell.update();
		}
	}

	/**
	 * swapBuffer puts backBuffer into currentBuffer.
	 * It does this by creating a copy of backBuffer and sets it into currentBuffer. 
	 * Then backBuffer is cleared.
	 */
	public void swapBuffers() {
		currentBuffer.clear();
		currentBuffer.putAll(backBuffer);
		backBuffer.clear();
	}

	/**
	 * getNumNeightbours gets Number of neighbours surrounding a cell 
	 * and returns this number. 
	 * @param x
	 * Cell x position is passed in.
	 * @param y
	 * Cell y position is passed in
	 * @return
	 * This int value is the number of neighbours surrounding the given Cell.
	 */
	public int getNumNeighbours(double x,double y) {

		int totalNeighbours = 0;

		/**create list to store cell's neighbouring positions.*/
		List<Position> neighbourPos = new ArrayList<Position>();

		/**construct 8 positions to check around cell in double [] format
		Top row.*/
		Position leftTopPos = new Position (x-cellSize, y-cellSize);
		Position topPos = new Position (x, y-cellSize);
		Position rightTopPos = new Position (x+cellSize, y-cellSize);
		/**Middle row.*/
		Position leftPos = new Position (x-cellSize, y);
		Position rightPos = new Position (x+cellSize, y);
		/**Bottom row.*/
		Position leftBottomPos = new Position (x-cellSize, y+cellSize);
		Position bottomPos = new Position (x, y+cellSize);
		Position rightBottomPos = new Position (x+cellSize, y+cellSize);

		/**adding all 8 positions into the ArrayList neighbourPos.*/
		neighbourPos.addAll(Arrays.asList(leftTopPos,leftPos,leftBottomPos,
				topPos,bottomPos,rightTopPos, rightPos,rightBottomPos));

		/**ask currentBuffer what is in that position in currentBuffer*/
		for(Position pos : neighbourPos) {						
			if(currentBuffer.containsKey(pos)) {
				totalNeighbours++;
			}
		}

		return totalNeighbours;
	}	
	
	/** Checks all cells, by getting neighbours and num of neighbours
	 * recursion magic happens here */
	public void checkCells() {
		
		//Iterate through the currentBuffer map to check each alive cell.
		//Once checked, the position of the dead neighbour is added to the checkedDead list.
		List<Position> checkedDead = new ArrayList<Position>();
		for (Position cellPos: currentBuffer.keySet()) {

			//Get the list of dead neighbours around the cell.
			List<Position> deadNeighbours = getDeadNeighbours(cellPos.getX(),cellPos.getY());
			//Iterate through the cell's deadNeighbours list to check whether it should be born.
			String deadCellPositions = "";
			for (Position deadCellPos: deadNeighbours) {
				if (!checkedDead.contains(deadCellPos) && getNumNeighbours(deadCellPos.getX(),deadCellPos.getY())==3){
					//Cell is born.
					createCell(deadCellPos.getX(),deadCellPos.getY());
				}
				checkedDead.add(deadCellPos);
				deadCellPositions += +deadCellPos.getX()+","+deadCellPos.getY()+"  ";
			}
			
			//Check self to see if it should remain alive or die.
			if (getNumNeighbours(cellPos.getX(),cellPos.getY()) == 2 || getNumNeighbours(cellPos.getX(),cellPos.getY()) == 3) {
				//Cell remains alive. Added to back buffer within createCell method.
				backBuffer.put(cellPos, currentBuffer.get(cellPos));
			} 

		}


	}

	/** Returns a list of dead neighbours in arrays
	 * 
	 * @param x
	 * 
	 * @param y
	 * 
	 * @return
	 * 
	 */
	public List<Position> getDeadNeighbours(double x, double y){

		//Search currentBuffer map using cell position as key.		
		List<Position> checkedDeadNeighbours = new ArrayList<Position>();
		double cellX = x;
		double cellY = y;
		Position key = new Position (cellX,cellY);

		//Check the neighbour to the left.
		key.setX(key.getX()-cellSize);
		if (!currentBuffer.containsKey(key)) {
			Position position = new Position (key.getX(),key.getY());
			checkedDeadNeighbours.add(position);
		}
		//Check the 3 neighbours above from left to right.
		key.setY(key.getY()-cellSize);
		for (int i = 0; i < 3; i++) {
			if (!currentBuffer.containsKey(key)) {
				Position position = new Position (key.getX(),key.getY());
				checkedDeadNeighbours.add(position);
			}
			if (i != 2) {
				key.setX(key.getX()+cellSize);
			}
		}
		//Check the neighbour to the right.
		key.setY(key.getY()+cellSize);
		if (!currentBuffer.containsKey(key)) {
			Position position = new Position (key.getX(),key.getY());

			checkedDeadNeighbours.add(position);
		}
		//Check the 3 neighbours below from right to left.
		key.setY( key.getY()+ cellSize);

		for (int i = 0; i < 3; i++) {
			if (!currentBuffer.containsKey(key)) {
				Position position = new Position (key.getX(),key.getY());

				checkedDeadNeighbours.add(position);
			}
			key.setX(key.getX()-cellSize);
		}
		

		return checkedDeadNeighbours;
	}
	
	/** creates cell */
	public void createCell(double x, double y) {
		Cell cell = new Cell(this, cellSize, x, y);
		backBuffer.put(new Position(x,y), cell);
	}
	
	public int[] findPatternDimensions(String patternName) {
		int width = 0;
		int height = 0;
		for (int[] pos : patterns.get(patternName)) {
			if (pos[0] > width) {
				width = pos[0];
			}
			if (pos[1] > height) {
				height = pos[1];
			}
		}
		return new int[] {width, height};
	}
	
	public List<int[]> rotatePattern(String patternName, int rotation){
		List<int[]> rotatedPattern = new ArrayList<int[]>();
		int[] patternDimensions = findPatternDimensions(patternName);
		switch (rotation){
		case 90:
			//x=y, y=-x+w
			for (int[] pos : patterns.get(patternName)) {
				int[] rotatedPos = new int[2];
				rotatedPos[0] = pos[1];
				rotatedPos[1] = -pos[0] + patternDimensions[0];
				rotatedPattern.add(rotatedPos);
			}
			break;
		case 180:
			//x=y, y=x
			for (int[] pos : patterns.get(patternName)) {
				int[] rotatedPos = new int[2];
				rotatedPos[0] = -pos[0] + patternDimensions[0];
				rotatedPos[1] = -pos[1] + patternDimensions[1];
				rotatedPattern.add(rotatedPos);
			}
			break;
		case 270:
			//x=-y+h, y=x
			for (int[] pos : patterns.get(patternName)) {
				int[] rotatedPos = new int[2];
				rotatedPos[0] = -pos[1] + patternDimensions[1];
				rotatedPos[1] = pos[0];
				rotatedPattern.add(rotatedPos);
			}
			break;
		default:
			rotatedPattern = patterns.get(patternName);
		}
		return rotatedPattern;
	}
	
	/** Places cells in a defined pattern. Adds it to the lastPatternAdded field*/
	public List<Cell> placePattern(String patternKey,double mouseX,double mouseY, int patternRotation) {
		List<Cell> placedCells = new ArrayList<Cell>();
		List<int[]> pattern = rotatePattern(patternKey, patternRotation);
		for (int[] position : pattern) {
			double x = mouseX + position[0]*cellSize;
			double y = mouseY + position[1]*cellSize;
			Position pos = new Position(x, y);
			
			if (currentBuffer.get(pos) == null) {
				Cell cell = new Cell(this, cellSize, x, y);
				currentBuffer.put(new Position(x,y), cell);
				placedCells.add(cell);
			}
		}
		return placedCells;
	}
	
	/**
	 * Removes a list of cells from the current buffer
	 */
	public void removeCells(List<Cell> cells) {
		for (Cell cell : cells) {
			currentBuffer.remove(cell.getPos());
		}
	}
	
	public List<int[]> getPattern(String patternKey) {
		return patterns.get(patternKey);
	}
	
	/** places cells randomly
	 * possibly replaced later with specific patterns */
	public void placeRandom() {
		
	}
	

	/** places cells with defined initial patterns */
	public void defineInitialPattern() {
		int patternIndex;
		do {
			patternIndex = (int)(Math.random() * patterns.size());
		} while (new ArrayList<String>(patterns.keySet()).get(patternIndex).equalsIgnoreCase("cell")); // do not allow "cell" as initial pattern
		List<int[]> pattern = /*patterns.get("snacker");*/(List<int[]>) patterns.values().toArray()[patternIndex];
		for (int[] position : pattern) {
			double x = position[0]*cellSize;
			double y = position[1]*cellSize;
			createCell(x, y);
		}
		swapBuffers();
	}
	
	/** Takes pattern templates from file and parse them to map
	 *  */
	private void parsePatterns() {
		try {
			PatternParser parser = new PatternParser(getClass().getResourceAsStream("/patterns.gol"));
			patterns = parser.getContents();
		} catch(IOException e) {System.out.println(e);}
		
	}
	
	public Set<String> getPatternNames(){
		return patterns.keySet();
	}
	
	public void restart() {
		swapBuffers();
		defineInitialPattern();
	}
	
}
