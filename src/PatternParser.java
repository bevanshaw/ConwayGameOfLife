import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Parser for pattern files used in Conway's Game of Life
 * <p>
 * Parses out pattern names matched with a list of int[x,y] of coordinates for living cells.
 * Coordinates start at [0,0] for the top left cell of the pattern and increase in single units.<br>
 * Example:<br>
 * glider = [1,0][2,1][0,2][1,2][2,2]<br>
 * <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGkAAABaCAYAAAC7bHg5AAAABHNCSVQICAgIfAhkiAAAAUhJREFUeJzt2sFtg1AURUEcuSBaoxJaogQ6IhvvExkEHDRTAHzpSG91X9u2bQO39nP1A/ibSAEiBYgUIFLA++oH/Mc0TcO6rod/dxzHYZ7nw797tESkdV2HZVmufsZlnLsAkQJEChApQKQAkQJEChApQKQAkQJEChApQKQAkQJEChApQKQAkQJEChApIDFEGccx9d2jvQz278+5C0icuzPceYAp0sedB5jOXYBIASIFiBQgUoBIASIFiBQgUoBIASIFiBQgUoBIASIFiBQgUoBIASIFiBRgiPJx5wGmcWSAcxew+9ydMSp8yj++tTvSGaPCp/zjW85dgEgBIgWIFCBSgEgBIgWIFCBSgEgBIgWIFCBSgEgBIgWIFCBSgEgBIgWIFLB7iHLGqPAp//iWcWSAcxcgUoBIASIFiBQgUoBIASIFiBQgUsAvgslLwwljfF0AAAAASUVORK5CYII=" />
 * @author Dirglehurbleherb
 */
public class PatternParser {
	private Map<String, List<int[]>> contents = new HashMap<String, List<int[]>>();
	private Scanner scan;
	
	/**
	 * @param String filePath; path of a file to parse
	 * @throws IOException
	 */
	public PatternParser(String filePath) throws IOException {
		this(new File(filePath));
	}

	/**
	 * @param Path filePath; path of a file to parse
	 * @throws IOException
	 */
	public PatternParser(Path filePath) throws IOException {
		this(filePath.toFile());
	}

	/**
	 * @param InputStream stream; stream to parse
	 * @throws IOException
	 */
	public PatternParser(InputStream stream) throws IOException {
		scan = new Scanner(stream);
		//parse();
		parseDiagrams();
	}
	
	/**
	 * @param File file; file to parse
	 * @throws IOException
	 */
	public PatternParser(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		scan = new Scanner(file);
		//parse();
		parseDiagrams();
	}
	
	/**
	 * Checks the file for a specific expected value
	 * @param String expected; expected string at this point in the file
	 * @throws IncorrectFormattingException
	 */
	private void expect(String expected) throws IncorrectFormattingException {
		String input = scan.next();
		if (!input.equals(expected)) {
			throw new IncorrectFormattingException("Expected " + expected + " got " + input + " ");
		}
	}
	
	/**
	 * Main logic for parsing pattern files.
	 * @throws IncorrectFormattingException if encountering unexpected formatting
	 */
	private void parse() throws IncorrectFormattingException{
		try {
			while (scan.hasNext()) {
				//get the name
				String key = scan.next();
				if (key.startsWith("//")){
					scan.nextLine();
					continue; //allow comments in pattern file
				}
				expect("=");
				scan.useDelimiter(""); //prepare to get individual characters
				expect(" ");
				
				//get the cell positions for the pattern template
				List<int[]> value = new ArrayList<int[]>();
				while (scan.hasNext() && !scan.hasNext(";")) {
					int[] pos = new int[2];
					expect("[");
					pos[0] = scan.nextInt();
					expect(",");
					pos[1] = scan.nextInt();
					expect("]");
					value.add(pos);
				}
				contents.put(key, value);
				scan.reset(); //reset delimiter to whitespace
				scan.nextLine();
			}
		} catch (InputMismatchException e) {throw new IncorrectFormattingException("");}
		
		scan.close();
	}
	
	private void parseDiagrams() throws IncorrectFormattingException{
		try {
			while (scan.hasNextLine()) {
				//Get the name of the diagram to use as the key.
				String key = scan.nextLine();
				if (key.startsWith("//")){
					scan.nextLine();
					continue; //Allows comments in pattern file.
				}
				
				//Get the cell positions for the pattern template.
				List<int[]> value = new ArrayList<int[]>();
				int x = 0;
				int y = 0;
				//Get each row of the diagram and determine the position of each cell in that row.
				String row = scan.nextLine();
				while (!row.equals(";")) {
					for (int i = 0; i < row.length(); i++) {
						if (row.charAt(i) == '*') {
							int[] pos = {x,y};
							value.add(pos);
						}
						x++;
					}
					x = 0;
					y++;
					row = scan.nextLine();
				}				
				contents.put(key, value);
			}
		} catch (InputMismatchException e) {throw new IncorrectFormattingException("");}
		
		scan.close();
	}
	
	/**
	 * Gets the full map of contents parsed from the pattern file
	 * @return {@literal Map<String, List<int[]>> contents parsed from pattern file }
	 */
	public Map<String, List<int[]>> getContents() {
		return contents;
	}
	
	/**
	 * Gets all pattern names parsed from the pattern file
	 * @return {@literal Set<String> pattern names parsed from pattern file }
	 */
	public Set<String> getNames(){
		return contents.keySet();
	}
	
	/**
	 * Gets all pattern templates parsed from the pattern file
	 * @return {@literal Collection<List<int[]>> pattern templates parsed from pattern file }
	 */
	public Collection<List<int[]>> getPatterns() {
		return contents.values();
	}
	
	/**
	 * Gets a specific pattern template which has the given key parsed from the pattern file
	 * @param String key; name of the pattern
	 * @return {@literal Collection<List<int[]>> pattern templates parsed from pattern file
	 * or null if no pattern was parsed with that name }
	 */
	public List<int[]> getPattern(String key) {
		return contents.get(key);
	}
	
	/**
	 * Exception thrown when parse() does not find the formatting it expects
	 * @author Dirglehurbleherb
	 */
	public class IncorrectFormattingException extends IOException{

		/**
		 * Automatically generated ID
		 */
		private static final long serialVersionUID = -7034452192827048210L;
		
		public IncorrectFormattingException() {
			super();
		}
		
		public IncorrectFormattingException(String message) {
			super(message);
		}
		
	}
}
