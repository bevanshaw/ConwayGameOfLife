import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;


/**
 * CONWAYS GAME OF LIFE
 * 
 * @author dirglehurbleherb
 */

public class GameOfLifeUI extends Application {
	private Bounds initialBounds1;
	private Bounds initialBounds2;

	private int width = 700, height = 500;
	private int padding = 5;

	private BorderPane layout = new BorderPane();
	private Scene scene = new Scene(layout, width, height);
	private Timeline timeline = new Timeline();

	private HBox optionsBox = new HBox();

	// play /pause button and image fields
	private Button playButton = new Button();
	private Image playIcon = new Image(getClass().getResourceAsStream("play.png"));
	private Image pauseIcon = new Image(getClass().getResourceAsStream("pause.png"));
	private ImageView playView = new ImageView(playIcon);	
	private ImageView pauseView = new ImageView(pauseIcon);		

	// next generation button and image fields
	private Image nextGen = new Image(getClass().getResourceAsStream("forwards.png"));
	private ImageView nextGenView = new ImageView(nextGen);	
	private Button nextGenButton = new Button();

	//rotation button and image fields
	private Image restart = new Image(getClass().getResourceAsStream("rotate.png"));
	private ImageView restartView = new ImageView(restart);
	private Button restartButton = new Button();

	// Black and white button
	private Image bw = new Image(getClass().getResourceAsStream("contrast.png"));
	private ImageView contrastView = new ImageView(bw);	
	private Button toggleBackGroundButton = new Button();//toggles the background between black and white

	//rotation button and image fields
	private Button rotateButton = new Button("0\u00B0");

	// String for toggling between background white and background black
	private String backgroundColour = "WHITE"; 

	//For generation count
	private Text genText = new Text("Gen: -1");
	private Text cellsText = new Text();

	private int cellSize = 20;
	private double minScale = 0.1; 
	private double scale = 1;
	private Group displayBuffer = new Group();
	private Game game = new Game(cellSize);
	private GridBackground grid = new GridBackground(cellSize, minScale);
	private Group scaleOffset = new Group(displayBuffer, grid);

	private Slider zoomSlider;
	private Slider speedSlider;
	private Label zoomLabel = new Label("zoom");
	private Label speedLabel = new Label("speed");	
	private Label patternLabel = new Label("patterns");	
	private Label colorLabel = new Label("colour rules");	
	private Label rotateLabel = new Label("rotate");
	HBox colorLabelBox = new HBox(5,colorLabel);

	private ComboBox<String> patternBox = new ComboBox<String>();
	private ComboBox<Map.Entry<String,Paint[]>> colorBox = new ComboBox<Map.Entry<String,Paint[]>>();
	private CellFactory cellFactory = new CellFactory();
	private int patternRotation = 0;


	@Override
	public void start(Stage primaryStage) throws Exception {
		//TIMELINE
		//________________
		KeyFrame frame = new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				update();
			}
		});

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(frame);

		//SCROLLING
		//____________________
		MouseListener listener = new MouseListener();
		scene.setOnMousePressed(listener);
		scene.setOnMouseDragged(listener);
		scene.setOnMouseMoved(listener);
		scene.setOnScroll(this::doMouseScroll);
		scene.setOnKeyPressed(this::doKeyPress);

		//LAYOUT
		//____________________
		layout.getChildren().add(scaleOffset);

		playButton.setStyle(
				"-fx-base: #353535;-fx-text-fill: white; -fx-pref-width: 28px; -fx-pref-height: 28px;");
		nextGenButton.setStyle(
				"-fx-font: 8 arial; -fx-base: #353535;-fx-text-fill: white; -fx-pref-width: 28px; -fx-pref-height: 28px;");
		restartButton.setStyle(
				"-fx-font: 10 arial; -fx-base: #353535;-fx-text-fill: white; -fx-pref-width: 28px; -fx-pref-height: 28px;");
		toggleBackGroundButton.setStyle(
				"-fx-font: 9 arial; -fx-base: #353535;-fx-text-fill: white; -fx-pref-width: 28px; -fx-pref-height: 28px;");

		//play button
		playButton.setGraphic(playView);
		playButton.setFocusTraversable(false);
		playView.setFitHeight(10);
		playView.setFitWidth(10);
		pauseView.setFitHeight(10);
		pauseView.setFitWidth(10);
		playButton.setOnAction(this::doPlay);
		playButton.setFocusTraversable(false);
		
		//restart button
		restartView.setFitHeight(13);
		restartView.setFitWidth(13);
		restartButton.setGraphic(restartView);
		restartButton.setOnAction(this::doRestart);
		restartButton.setFocusTraversable(false);

		//next gen button
		nextGenView.setFitHeight(10);
		nextGenView.setFitWidth(10);
		nextGenButton.setGraphic(nextGenView);
		nextGenButton.setFocusTraversable(false);
		nextGenButton.setOnAction(this::nextGen);

		//contrast button
		contrastView.setFitHeight(10);
		contrastView.setFitWidth(10);
		toggleBackGroundButton.setGraphic(contrastView);
		toggleBackGroundButton.setFocusTraversable(false);
		toggleBackGroundButton.setOnAction(this::doBlackAndWhite);
		
		//rotate button
		rotateButton.setOnAction(this::toggleRotation);
		rotateButton.setMinWidth(50);
		VBox rotateBox = new VBox(rotateLabel, rotateButton);
		rotateBox.setAlignment(Pos.CENTER);

		/**
		 * Generation and Lifespan statistics
		 */
		VBox statBox = new VBox();
		statBox.setAlignment(Pos.CENTER_RIGHT);
		statBox.setSpacing(padding);
		statBox.setPadding(new Insets(padding, padding, padding, padding));
		statBox.getChildren().addAll(genText, cellsText);
		statBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		genText.setFill(Color.RED);
		cellsText.setFill(Color.RED);
		//genText.setTextAlignment(TextAlignment.JUSTIFY);
		genText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		cellsText.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		layout.setTop(statBox);		


		//BOTTOM LAYOUT
		//_____________
		optionsBox.setAlignment(Pos.CENTER_LEFT);
		optionsBox.setSpacing(padding);
		optionsBox.setPadding(new Insets(padding, padding, padding, padding));
		
		GridPane patternPane = new GridPane(); //patterns grid
		optionsBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		patternBox.setItems(FXCollections.observableArrayList(game.getPatternNames()));
		patternBox.getSelectionModel().select("cell");
		patternBox.setFocusTraversable(false);

		GridPane colourPane = new GridPane(); //colours grid

		for (Map.Entry<String, Paint[]> entry : Cell.getColorRules().entrySet()) {
			colorBox.getItems().add(entry);
		}

		colorBox.setCellFactory(cellFactory);
		colorBox.setConverter(new Convertor());
		colorBox.getSelectionModel().selectFirst();
		colorBox.getSelectionModel().selectedItemProperty().addListener(this::updateLabelColors);
		colorBox.setFocusTraversable(false);
		colorLabelBox.setAlignment(Pos.CENTER);

		ColorPicker colorPicker = new ColorPicker(Cell.getCustom());
		colorLabelBox.getChildren().add(colorPicker);
		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateCustomColor(colorPicker);
			}
		});
		colorPicker.setFocusTraversable(false);
		colorPicker.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		colorPicker.setStyle("-fx-color-label-visible: false ;");
		colorPicker.setMaxHeight(15);
		
		GridPane sliderPane = new GridPane();

		zoomSlider = new Slider();
		zoomSlider.setMin(minScale);
		zoomSlider.setMax(3);
		zoomSlider.setValue(1);
		//zoomSlider.setMajorTickUnit(minScale);
		//zoomSlider.setMinorTickCount(0);
		//zoomSlider.setBlockIncrement(minScale);
		//zoomSlider.setShowTickLabels(true);
		//zoomSlider.setShowTickMarks(true);
		zoomSlider.valueProperty().addListener(this::doZoom);
		zoomSlider.setFocusTraversable(false);

		speedSlider = new Slider();
		speedSlider.setMin(0.1);
		speedSlider.setMax(3);
		speedSlider.setValue(1);
		//speedSlider.setShowTickLabels(true);
		speedSlider.valueProperty().addListener(this::setSpeed);
		speedSlider.setFocusTraversable(false);
		
		patternPane.addColumn(0, patternLabel, patternBox);
		colourPane.addColumn(1, colorLabelBox, colorBox);		
		sliderPane.addColumn(2, zoomLabel, zoomSlider);
		sliderPane.addColumn(3, speedLabel, speedSlider);

		GridPane.setHalignment(zoomLabel, HPos.CENTER);
		GridPane.setHalignment(speedLabel, HPos.CENTER);
		GridPane.setHalignment(patternLabel, HPos.CENTER);
		GridPane.setHalignment(colorLabel, HPos.CENTER);
		optionsBox.getChildren().addAll(patternPane, rotateBox, colourPane, sliderPane, nextGenButton, restartButton, toggleBackGroundButton, playButton);
		
		layout.setBottom(optionsBox);

		displayBuffer.getChildren().addAll(game.getCurrentBuffer());

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> grid.construct());
		primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> grid.construct());
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		scrollGame(width*0.5, height*0.5);//must be after stage is shown
		updateStats();
		Bounds scaleBounds = scaleOffset.getBoundsInLocal();
		initialBounds1 = new BoundingBox(scaleBounds.getMinX(), scaleBounds.getMinY(), scaleBounds.getWidth(), scaleBounds.getHeight());//DEBUG
		initialBounds2 = new BoundingBox(scaleBounds.getMinX(), scaleBounds.getMinY(), scaleBounds.getWidth(), scaleBounds.getHeight());
	}

	private void nextGen(ActionEvent act){
		update();
	}
	
	/**
	 * An attempt to fix the issue where cells going outside the scaleOffset's bounds causes translation issues
	 * Doesn't actually work - tinker with it later maybe.
	 */
	private void offsetByBoundsDelta() {
		Bounds scaleBounds = scaleOffset.getBoundsInLocal();
		double deltaX=0;
		double deltaY=0;
		String generation = genText.getText().substring(5);
		int gen = Integer.parseInt(generation);
		if (gen % 2 != 0) {
			deltaX = initialBounds1.getMinX() - scaleBounds.getMinX();
			deltaY = initialBounds1.getMinY() - scaleBounds.getMinY();
			initialBounds2 = new BoundingBox(scaleBounds.getMinX(), scaleBounds.getMinY(), scaleBounds.getWidth(), scaleBounds.getHeight());
		} else {
			deltaX = initialBounds2.getMinX() - scaleBounds.getMinX();
			deltaY = initialBounds2.getMinY() - scaleBounds.getMinY();
			initialBounds1 = new BoundingBox(scaleBounds.getMinX(), scaleBounds.getMinY(), scaleBounds.getWidth(), scaleBounds.getHeight());
		}
		if (deltaX != 0 || deltaY != 0) {
			displayBuffer.setTranslateX(displayBuffer.getTranslateX() + deltaX);
			displayBuffer.setTranslateY(displayBuffer.getTranslateY() + deltaY);
			//scrollGame(deltaX, deltaY);
			//grid.scroll(-deltaX, -deltaY);
		}
	}
	
	public void update() {
		updateStats();
		game.update();
		refreshBuffer();
		//offsetByBoundsDelta();
	}

	public void setSpeed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
		layout.requestFocus();
		timeline.setRate(newVal.doubleValue());
	}

	public void refreshBuffer() {
		displayBuffer.getChildren().clear();
		displayBuffer.getChildren().addAll(game.getCurrentBuffer());
	}

	public void doZoom(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
		layout.requestFocus();
		double scaleVal = Math.round(newVal.doubleValue() * 2.0) * 0.5;
		scaleVal = newVal.doubleValue();
		scaleOffset.setScaleX(scaleVal);
		scaleOffset.setScaleY(scaleVal);
		grid.scale(scaleVal);
		scale = scaleVal;
		//zoomSlider.setValue(scaleVal);
	}

	public void scrollGame(double dx, double dy) {
		displayBuffer.setTranslateX(displayBuffer.getTranslateX() + dx);
		displayBuffer.setTranslateY(displayBuffer.getTranslateY() + dy);
		grid.scroll(dx, dy);
	}

	public void doMouseScroll(ScrollEvent event) {
		double dx = event.getDeltaX();// + cellSize*0.3;
		double dy = event.getDeltaY();// + cellSize*0.3;
		scrollGame(dx, dy);
	}

	public void doKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.DOWN) {
			scrollGame(0, -20 - cellSize*0.3);
		}
		if (event.getCode() == KeyCode.UP) {
			scrollGame(0, 20 + cellSize*0.3);
		}
		if (event.getCode() == KeyCode.LEFT) {
			scrollGame(20 + cellSize*0.3, 0);
		}
		if (event.getCode() == KeyCode.RIGHT) {
			scrollGame(-20 - cellSize*0.3, 0);
		}
	}

	/**
	 * Play/ Pause Toggle Button
	 * switches image to pause when playing and playing when paused 
	 * @param act
	 * Action Event
	 */
	private void doPlay(ActionEvent act){
		if (timeline.getStatus() == Animation.Status.RUNNING) {
			timeline.pause();
			playButton.setGraphic(playView);
		} else {
			timeline.play();
			playButton.setGraphic(pauseView);
		}
	}

	/**
	 * Method for user to toggle between a black background and a white background
	 */			
	private void doBlackAndWhite(ActionEvent act) {
		if (backgroundColour.equals("WHITE")) {
			backgroundColour = "BLACK";
			layout.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			optionsBox.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			zoomLabel.setTextFill(Color.WHITE);
			speedLabel.setTextFill(Color.WHITE);
			patternLabel.setTextFill(Color.WHITE);
			colorLabel.setTextFill(Color.WHITE);
			rotateLabel.setTextFill(Color.WHITE);
		} else {
			backgroundColour = "WHITE";
			layout.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
			optionsBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
			zoomLabel.setTextFill(Color.BLACK);
			speedLabel.setTextFill(Color.BLACK);
			patternLabel.setTextFill(Color.BLACK);
			colorLabel.setTextFill(Color.BLACK);
			rotateLabel.setTextFill(Color.BLACK);
		}
	}	
	
	/**
	 * Method to refresh the application on button
	 */		
	public void doRestart(ActionEvent act) {
		timeline.pause();
		playButton.setGraphic(playView);
		game.restart();
		resetTranslation();
		refreshBuffer();
		genText.setText("Gen: -1");
		updateStats();
	}
	
	
	/**
	 * Method to rotate the patterns
	 */	
	public void toggleRotation(ActionEvent act) {
		patternRotation = (patternRotation + 90) % 360;
		rotateButton.setText(patternRotation + "\u00B0");
	}

	/**
	 * Method to count the generation of the cell in the top-left 
	 * of the screen by using the subString method and change the int into String.
	 */
	public void updateStats() {
		String generation = genText.getText().substring(5);
		int gen = Integer.parseInt(generation);
		gen++;
		genText.setText("Gen: " + gen);
		int numCells = displayBuffer.getChildren().size();
		cellsText.setText("Cells: " + numCells);
	}	

	private void resetTranslation() {
		scrollGame(-displayBuffer.getTranslateX(), -displayBuffer.getTranslateY());
		scrollGame(scene.getWidth()*0.5, scene.getHeight()*0.5);
	}

	public static void main(String[] args) {
		launch();
	}

	/**
	 * Handles mouse dragging for infinite scrolling
	 */
	private class MouseListener implements EventHandler<MouseEvent>{
		private double prevX;
		private double prevY;
		private boolean isScrolling;
		private List<Cell> placedPattern = new ArrayList<Cell>();

		@Override
		public void handle(MouseEvent event) {
			double offsetX = event.getX()/scale - displayBuffer.getTranslateX();
			double offsetY = event.getY()/scale - displayBuffer.getTranslateY();
			double snapX = Math.round(offsetX/cellSize) * cellSize;
			double snapY = Math.round(offsetY/cellSize) * cellSize;
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				//scrolling
				prevX = offsetX;
				prevY = offsetY;
				
				//pattern placing
				placedPattern.clear();
				placedPattern.addAll(game.placePattern(patternBox.getValue(),snapX,snapY, patternRotation));
				refreshBuffer();	
			}

			else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				//scrolling
				isScrolling = true;
				double dx = (prevX - offsetX) *0.7;
				double dy = (prevY - offsetY) *0.7;
				scrollGame(dx, dy);
				prevX = offsetX;
				prevY = offsetY;
				
				game.removeCells(placedPattern);
				refreshBuffer();
				isScrolling = false;
			}
			/* Method for tracking the mouse with an outline of the pattern about to be placed,
			   and then once clicked add the pattern to the view*/
			if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
				//pattern placing
				if (!isScrolling) {
					refreshBuffer();
					createTemporaryPattern(patternBox.getValue(),snapX,snapY);
				}
			}
		}

	}
	
	private void updateCellColors() {
		for (Node node : displayBuffer.getChildren()) {
			Cell cell = (Cell) node;
			cell.updateColor();
		}
	}
	
	private void updateCustomColor(ColorPicker colorPicker) {
		Color newValue = colorPicker.getValue();
		Cell.setCustom(newValue);
		cellFactory.setCustomRectanglePaint(newValue);
		updateCellColors();
	}

	public void updateLabelColors(ObservableValue<? extends Map.Entry<String,Paint[]>> observable, Map.Entry<String,Paint[]> oldValue, Map.Entry<String,Paint[]> newValue) {
		Cell.setColorName(newValue.getKey());
		updateCellColors();
				
		colorLabelBox.getChildren().clear();
		colorLabelBox.getChildren().add(colorLabel);
		
		if(!newValue.getKey().equals("Custom")){
			for (Paint p : newValue.getValue()){
				Rectangle r = new Rectangle(15,15,p);
				colorLabelBox.getChildren().add(r);
			}
		}else {
			ColorPicker colorPicker = new ColorPicker(Cell.getCustom());
			colorLabelBox.getChildren().add(colorPicker);
			colorPicker.setFocusTraversable(false);
			colorPicker.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			colorPicker.setStyle("-fx-color-label-visible: false;");
			colorPicker.setMaxHeight(15);
			colorPicker.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					updateCustomColor(colorPicker);
				}
			});
		}
	}

	/** Creates temporary cell that follows the mouse*/
	public void createTemporaryCell(double x, double y) {
		Cell cell = new Cell(game, cellSize, x, y);
		displayBuffer.getChildren().add(cell);
	}

	/** Creates temporary cell pattern that follows the mouse*/
	public void createTemporaryPattern(String patternKey,double mouseX,double mouseY) {
		List<int[]> pattern = game.rotatePattern(patternKey, patternRotation);
		for (int[] position : pattern) {
			double x = mouseX + position[0]*cellSize;
			double y = mouseY + position[1]*cellSize;
			createTemporaryCell(x,y);
		}
	}

	private class Convertor extends StringConverter<Map.Entry<String, Paint[]>>{

		@Override
		public Map.Entry<String, Paint[]> fromString(String arg0) {
			Map<String, Paint[]> d = new HashMap<String, Paint[]>();
			d.put("TEST", new Paint[] {Color.RED});
			for (Map.Entry<String, Paint[]> d1 : d.entrySet()) {
				return d1;
			}
			return null;
		}

		@Override
		public String toString(Map.Entry<String, Paint[]> object) {
			return object.getKey();
		}

	}

	private class CellFactory implements Callback<ListView<Map.Entry<String,Paint[]>>, ListCell<Map.Entry<String,Paint[]>>>{
		private Rectangle customRectangle;
		private HBox box;

		@Override 
		public ListCell<Map.Entry<String,Paint[]>> call(ListView<Map.Entry<String,Paint[]>> p) {
			return new ListCell<Map.Entry<String,Paint[]>>() {
				{ 
					setContentDisplay(ContentDisplay.LEFT); 
					box = new HBox(5);
				}

				@Override 
				protected void updateItem(Map.Entry<String,Paint[]> item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setGraphic(null);
						setText(null);
					} else {
						if (item.getKey().equals("Custom")) {
							Paint p = Cell.getCustom();
							customRectangle = new Rectangle(15,15,p);
							box.getChildren().add(customRectangle);
						} else {
							for (Paint p : item.getValue()){
								Rectangle r = new Rectangle(15,15,p);
								box.getChildren().add(r);
							}
						}
						setGraphic(box);
						setText(item.getKey());
					}
				}
			};
		}

		public void setCustomRectanglePaint(Paint paint) {
			customRectangle.setFill(paint);
		}
	}
}
