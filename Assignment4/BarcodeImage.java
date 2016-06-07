import java.util.Arrays;
import java.util.ArrayList;

public class BarcodeImage implements Cloneable {

	//The exact internal dimensions of 2D data. 
	public static final int MAX_HEIGHT = 30;
	public static final int MAX_WIDTH = 65;

	//This is where to store your image.
	//If the incoming data is too large,
	//instantiate memory anyway, but leave
	//it blank (white). This data will be false
	//for elements that are white, and true for elements that are black.
	private Boolean[][] image_data;

	private int start;
	public int start() { return this.start; }

	private int width;
	public int width() { return this.width; }


	// Default Constructor
	//instantiates a 2D array (MAX_HEIGHT x MAX_WIDTH) and 
	//stuffs it all with blanks (false).
	public BarcodeImage() {
		this.image_data = new Boolean[BarcodeImage.MAX_HEIGHT][BarcodeImage.MAX_WIDTH];
		this.start = 0;
		this.width = 0;
		for (Boolean[] row : this.image_data)
			Arrays.fill(row,false);
	}


	// Default Constructor
	//takes a 1D array of Strings and converts it to the internal 2D array of booleans. 
	public BarcodeImage(String[] str_data) {
		//do shit here matt
		this.image_data = new Boolean[BarcodeImage.MAX_HEIGHT][BarcodeImage.MAX_WIDTH];
		ArrayList<String> strDataTemp = new ArrayList<String>();
		for (String str : str_data)
			if (!str.trim().equals(""))
				strDataTemp.add(str.trim()); // Requires Error checking
		String[] strData = strDataTemp.toArray(new String[strDataTemp.size()]);
		falseParityCC(strData);
		computeSignalWidthAndScanningStart(strData);
		insertImageData(strData);
	}


	// Accessor for each individual pixel.
	public Boolean getPixel(int row, int col) {
		if (!indexInvalid(row,col)){
			return false;
		}
		return this.image_data[row][col];
	}


	// mutator for each individual pixel.
	public Boolean setPixel(int row, int col, Boolean value) {
		if (indexInvalid(row,col))
			return false;
		this.image_data[row][col] = value;
		return true;
	}


	// Checks if index row and column values are invalid
	// Helper function
	private Boolean indexInvalid(int row, int col) {
		if (row > BarcodeImage.MAX_HEIGHT || row < 0 || 
			col > BarcodeImage.MAX_WIDTH || col < 0)
			return false;
		return true;
	}

	// Checks to see if image is too large or null.
	// Returns false if thats the case
	private Boolean checkImageSize(String[] data) {
		if (data.length >= BarcodeImage.MAX_HEIGHT || data == null)
			return false;
		else
			for(String dStr : data)
				if (dStr.length() >= BarcodeImage.MAX_WIDTH)
					return false;
		return true;
	}


	// Error checking for lost data from the trim() method
	// Could be implemented in the for loop above, but this
	// was easier for me at the time and runs quick-enough.
	private void falseParityCC(String[] strData) {
		int width = strData[strData.length-1].length();
		for (int i = 0; i < strData.length; ++i){
			while(strData[i].length() < width) {
				strData[i] += " ";
			}
		}
	}



	// Helper function, inserts the string into the Boolean
	// Format required for this data_image
	private void insertImageData(String[] imgData) {
		for (int i = this.start; i < BarcodeImage.MAX_HEIGHT; ++i)
			for (int k = 0; k < this.width; ++k) {
				if (imgData[i-this.start].charAt(k) == ' ') {
					this.image_data[i][k] = false;
				} else {
					this.image_data[i][k] = true;
				}
			}

	}


	// debug help... never actually used it except to see if
	// the displayToConsole function actually worked...
	// May be useful in the future though.
	public void displayToConsole() {
		for (int i = this.start; i < MAX_HEIGHT; ++i){
			for (int k = 0; k < width; ++k){
				if (this.image_data[i][k])
					System.out.print('*');
				else
					System.out.print(' ');
			}
			System.out.println();
		}
	}


	private void computeSignalWidthAndScanningStart(String[] imgData) {
		this.start = BarcodeImage.MAX_HEIGHT - imgData.length;
		this.width = imgData[imgData.length-1].length();
	}


	// I still think a copy-constructor is a better implementation
	// The fact that clone() can return a CloneNotSupportedException
	// is proof that this function CAN be broken.
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}