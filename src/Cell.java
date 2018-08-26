import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
/**
 * Class to store info about the cell object and extends Rectangle
 * @author DirgleHurbleHerb
 *
 */
public class Cell extends Rectangle {

	//	private static ColorOption defaultOption = new ColorOption("Lifespan",Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED);
	private int lifespan = 0;
	private Game game;
	private static Color custom = Color.DARKSALMON;
	private static String colorName = "Custom";

	/**
	 * Constructor for the cell class
	 * @param game the Game object the cell has a reference to.
	 * @param cellSize the width and height of the cell
	 * @param x for x pos
	 * @param y for y pos
	 */
	public Cell(Game game, int cellSize, double x, double y) {
		super(cellSize, cellSize);
		setTranslateX(x);
		setTranslateY(y);
		this.game = game;
		setStroke(Color.WHITE);
		setStrokeType(StrokeType.INSIDE);
		setStrokeWidth(cellSize*0.05);
		this.setFill(custom);
	}

	/**
     *  Shows the logic of Conway’s Game of Life. Cells that are born appear yellow,
     *  cells that will die in the next generation appear red
     *  (unless they appear for only one generation in which case they are yellow). 
     *  All other live cells slowly transition in colour until they eventually stabilise and become purple. 
     */	
	public void colourRuleLifespan() {
		int neighbours = this.game.getNumNeighbours(this.getTranslateX(),this.getTranslateY());

		if(lifespan == 1 || lifespan == 0) {
			this.setFill(Color.YELLOW); //Made from green 1.0 and red 1.0, (blue is 0.0).
		} else if (neighbours == 2 || neighbours == 3) {
			Color currentColour = (Color)getFill();
			double red = currentColour.getRed();
			double green = currentColour.getGreen();
			double blue = currentColour.getBlue();
			red -= 0.2;
			if(red < 0 ) {
				red = 0;
				if(red==0) {
					blue+=0.1;
				}
			}
			if(blue>1.0) {
				blue = 1.0;
			}
			if(blue == 1.0) {
				green -= 0.2;
				if(green<0) {
					green = 0;
					red += 0.25; //Note: must be larger than initial red decrement otherwise will not turn purple.
					if(red>1.0) {
						red = 1.0;
					}

				}
			}
			this.setFill(Color.color(red, green, blue));
		} else {
			this.setFill(Color.RED);//NEVER GETS HERE SO POSSIBLY REDUNDANT? NO RED DESPITE VIGOROUS TESTING!!
		}
	}

	/**
	 * Setting the colour based on a selection made with the ColorPicker in the GUI.
	 * @param colorPicker
	 * 
	 */
	public void colourRuleCustom(ColorPicker colorPicker) {
		this.setFill(colorPicker.getValue());
	}

	/**
     * Assigns colours depending on the amount of neighbours a cell has 
     * e.g., 1 neighbour = yellow, 2 neighbours = orange, 
     * and so on up to the maximum number of 8 neighbours).
     */	
	public void colourRuleNeighbours() {
		int neighbours = this.game.getNumNeighbours(this.getTranslateX(),this.getTranslateY());
		//System.out.println(getTranslateX() + " " + getTranslateY() + " "  + lifespan + " " + neighbours);
		this.setFill(Color.YELLOW);
		if(neighbours == 1) {
			this.setFill(Color.YELLOW);
		} else if (neighbours == 2) { 
			this.setFill(Color.DARKORANGE);
		}else if (neighbours == 3){
			this.setFill(Color.RED);
		}if(neighbours == 4) {
			this.setFill(Color.BROWN);
		} else if (neighbours == 5) { 
			this.setFill(Color.PURPLE);
		}else if (neighbours == 6){
			this.setFill(Color.BLUEVIOLET);
		} else if (neighbours == 7) { 
			this.setFill(Color.BLUE);
		}else if (neighbours == 8){
			this.setFill(Color.MAGENTA);
		}
	}

	/**
     * Assigns cells a random placement of colours. The only colours that 
     * colorRuleRandom() does not allow are black and white,
     * as these are the background colours.
     */	
	public void colourRuleRandom(){

		double red = Math.random();
		double green = Math.random();
		double blue = Math.random();

		if((red != 0.0 && green !=0.0 && blue != 0.0)||((red != 1.0 && green !=1.0 && blue != 1.0))){
			this.setFill(Color.color(red, green, blue));
		}
	}
	public void update() {
		lifespan++;
		updateColor();

	}

	public void updateColor() {

		//		colourRuleLifespan();
		if(colorName.equals("Lifespan")) {
			colourRuleLifespan();
		}else if(colorName.equals("Random")) {
			colourRuleRandom();
		}else if(colorName.equals("Neighbours")) {
			colourRuleNeighbours();
		}else if(colorName.equals("Custom")) {
			this.setFill(custom);
		} else {
			System.out.println("No colours selected.");
		}

	}
	/**
	 * Returns a list of all color options for use in a dropbox
	 * @return List<ColorOption> list of all rules
	 */
	public static Map<String,Paint[]> getColorRules() {
		Map<String,Paint[]> colorRules = new TreeMap<String,Paint[]>();
		colorRules.put("Lifespan",new Paint[] {Color.YELLOW, 
				new LinearGradient(0,0,1,1,true, CycleMethod.REPEAT,
						new Stop(0.3,Color.CYAN), 
						new Stop(0.5,Color.BLUE),
						new Stop(0.7,Color.MAGENTA)),
				Color.RED});//Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED
		colorRules.put("Neighbours",new Paint[] {Color.YELLOW, Color.ORANGE, Color.RED});
		colorRules.put("Random", new Paint [] {new LinearGradient(0,0,1,1,true, CycleMethod.REPEAT,
				new Stop(0.2,Color.GOLD),
				new Stop(0.4,Color.FORESTGREEN),
				new Stop(0.6,Color.DARKSALMON),
				new Stop(0.8,Color.AQUAMARINE))});
		colorRules.put("Custom",new Paint[] {custom});
		//colorRules.add(defaultOption);
		return colorRules;
	}
	
	public Position getPos() {
		return new Position(getTranslateX(), getTranslateY());
	}

	public static Color getCustom() {
		return custom;
	}

	public static void setCustom(Color custom) {
		Cell.custom = custom;
	}

	public static void setColorName(String colorName) {
		Cell.colorName = colorName;
	}
}
