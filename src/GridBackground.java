import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;


/**
 * This Class contains the instructions for creating the grid on which the game takes place.
 * @author Dirglehurbleherb
 *
 */
public class GridBackground extends Parent {
	private int cellSize;
	private double dx;
	private double dy;
	private double minScale;
	private double scale = 1;
	private double lineWidth = 1;
	private boolean adjustLineWidthToScale = true;
	
	public GridBackground(int cellSize, double minScale) {
		super();
		this.cellSize = cellSize;
		this.minScale = minScale;
	}
	
	/**
	 * Constructs the grid based on cellSize and scene dimensions
	 */
	public void construct() {
		//prepare
		Stage stage = (Stage)getScene().getWindow();
		List<Line> buffer = new ArrayList<Line>();
		double width = stage.getWidth() / minScale;
		double height = stage.getHeight() / minScale;
		
		//make lines
		List<Line> vLines = constructLines(true, dx, width, height);
		List<Line> hLines = constructLines(false, dy, height, width);
		
		//put lines on screen
		getChildren().clear();
		getChildren().addAll(vLines);
		getChildren().addAll(hLines);//buffer
	}
	
	private List<Line> constructLines(boolean vertical, double dPos, double dimension1, double dimension2) {
		List<Line> lines = new ArrayList<Line>();
		double pos = dPos - cellSize;
		while (pos < dimension1) {
			Line line;
			if (vertical) {
				line = new Line(pos, -dimension2, pos, dimension2);
			} else {
				line = new Line(-dimension2, pos, dimension2, pos);
			}
			line.setStroke(Color.GREY);
			if (!adjustLineWidthToScale) {
				line.setStrokeWidth(lineWidth / scale);
			} else {
				line.setStrokeWidth(lineWidth);
			}
			lines.add(line);
			pos+= cellSize;
		}
		return lines;
	}
	
	/** 
	 * Scrolls the grid by the specified change in x and y
	 * @param x
	 * @param y
	 */
	public void scroll(double dx, double dy) {
		this.dx = (this.dx + dx) % cellSize;
		this.dy = (this.dy + dy) % cellSize;
		construct();
	}
	
	public void adjustLineWidthToScale(boolean value) {
		adjustLineWidthToScale = value;
	}
	
	/**
	 * Scales the grid whilst maintaining a consistent lineWidth
	 * @param scaleBy
	 */
	public void scale(double scaleBy) {
		if (scale != scaleBy) {
			scale = scaleBy;
			construct();
		}
	}
}
