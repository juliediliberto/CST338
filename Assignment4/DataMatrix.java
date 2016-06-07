public class DataMatrix implements BarcodeIO {

   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   private final int OFFSET = 1;

   //a single internal copy of any image scanned-in OR passed-into
   //the constructor OR created by BarcodeIO's generateImageFromText().
   private BarcodeImage image;

   // a single internal copy of any text read-in OR passed-into the
   //constructor OR created by BarcodeIO's translateImageToText().
   private String text;

   //actualWidth and actualHeight - two ints that are typically less
   //than BarcodeImage.MAX_WIDTH and BarcodeImage.MAX_HEIGHT which
   //represent the actual portion of the BarcodeImage that has the
   //real signal.  This is dependent on the data in the image, and
   //can change as the image changes through mutators.  It can be
   //computed from the "spine" of the image.
   private int actualWidth;
   private int actualHeight;


   // constructs an empty, but non-null, image and text value.
   //The initial image should be all white, however, actualWidth and
   //actualHeight should start at 0, so it won't really matter what's
   //in this default image, in practice.  The text can be set to blank,
   //"", or something like "undefined".
   public DataMatrix() {
      this.image = new BarcodeImage();
      this.actualHeight = 0;
      this.actualWidth = 0;
      this.text = "null";
   }


   //sets the image but leaves the text at its default value. 
   //Call scan() and avoid duplication of code here.
   public DataMatrix(BarcodeImage image) {
      scan(image);
      this.text = "";
   }


   //sets the text but leaves the image at its default value. Call
   //readText() and avoid duplication of code here.
   public DataMatrix(String text) {
      this();
      readText(text);
   }


   //a mutator for text.  Like the constructor;  in fact it is called by the constructor
   public Boolean readText(String text) {
      if (text != null){
         this.text = text;
         return true;
      }
      return false;
   }


   //a mutator for image.  Like the constructor;  in fact it is called by the constructor.
   //Besides calling the clone() method of the BarcodeImage class, this method will do a
   //couple of things including set the actualWidth and actualHeight and call cleanImage().
   //Because scan() calls clone(), it should deal with the CloneNotSupportedException by
   //embeddingthe clone() call within a try/catch block.  Don't attempt to hand-off the
   //exception using a "throws" clause in the function header since that will not be 
   //compatible with the underlying BarcodeIO interface.  The catches(...) clause can have
   //an empty body that does nothing.
   public Boolean scan(BarcodeImage image) {
      try {
         if (this.image != null)
            this.text = "";
         this.image = (BarcodeImage)image.clone();
         this.actualWidth = computeSignalWidth();
         this.actualHeight = computeSignalHeight();
         return true;
      } catch (CloneNotSupportedException e) {
         System.out.println(e.toString());
         return false;
      }
   }


   //Accessors for actualWidth and actualHeight
   public int actualWidth() { return this.actualWidth; }
   public int actualHeight() { return this.actualHeight; }



   //Assuming that the image is correctly situated in the lower-left corner of the
   //larger boolean array, these methods use the "spine" of the array (left and bottom BLACK)
   //to determine the actual size.
   private int computeSignalWidth() {
      return this.image.width();
   }

   private int computeSignalHeight() {
      return BarcodeImage.MAX_HEIGHT - this.image.start();
   }



   public Boolean generateImageFromText() 
   {
      String colString;
      this.actualWidth = this.text.length()+2;
      this.actualHeight = 10;
      
      
      //create left limitation line
      for (int row=0; row<actualHeight; row++)
      {
         this.image.setPixel(row, 0, true);
      }
      
      //create cols from binary string starting with * and ending with alternating
      int next=0;
      for (int col=1; col<this.actualWidth-1; col++){
         colString=charToCol(this.text.charAt(col-1));
         next=0;
         for (int row=9; row>1; row--)
         {
            switch (row)
            {
            case 9:
               this.image.setPixel(row,col,true);
            case 0:
               if (col%2==0)
                  this.image.setPixel (row,col,true);
            default:
               this.image.setPixel(row,col,binToBool(colString.charAt(next)));
               next++;
            }
         }
      }
         
     //create right open borderline
      for (int row=0; row < actualHeight; row++)
      {
         if ((row+1)%2==1)
            this.image.setPixel(row,this.actualWidth-1,true);
         else 
            this.image.setPixel(row,this.actualWidth-1,false);
      }
      return true;
   }

   private static String strToBinStr(String text)
   {
       String bString="";
       String temp="";
       for(int i=0;i<text.length();i++)
       {
           temp=Integer.toBinaryString(text.charAt(i));
           bString+=temp;
       }
       return bString;
   }

   private Boolean binToBool(char next)
   {
      if (next == '1')
         return true;
      else
         return false;
   }


   public Boolean translateImageToText() {
      if (image == null)
         return false;

      int character;
      for (int i = OFFSET; i < this.actualWidth-1; ++i){
         character = 0;
         for (int k = OFFSET; k < this.actualHeight-1; ++k) {
            if (this.image.getPixel(k+this.image.start(),i)) {
               character+=key(k);
            }
         }
         this.text += asciiToChar(character);
      }
      return true;
   }


   public void displayTextToConsole() {
      System.out.println(this.text);
   }


   public void displayImageToConsole() {

      printBorder();
      for (int i = this.image.start(); i < BarcodeImage.MAX_HEIGHT; ++i){
         System.out.print('|');
         for (int k = 0; k < this.actualWidth; ++k){
            if (this.image.getPixel(i,k)){
               System.out.print(DataMatrix.BLACK_CHAR);
            }
            else{
               System.out.print(DataMatrix.WHITE_CHAR);
            }
         }
         System.out.print("|\n");
      }
      printBorder();
   }


   private void printBorder(){
      for (int i = 0; i < this.actualWidth+2; ++i)
               System.out.print('-');
            System.out.println();
   }



   private int key(int num) {
      switch(num){
         case 1:
            return 128;
         case 2:
            return 64;
         case 3:
            return 32;
         case 4:
            return 16;
         case 5:
            return 8;
         case 6:
            return 4;
         case 7:
            return 2;
         default:
            return 1;
      }
   }


   private char asciiToChar(int num) {
      return (char)num;
   }



   //This private method will make no assumption about the placement
   //of the "signal" within a passed-in BarcodeImage.  In other words,
   //the in-coming BarcodeImage may not be lower-left justified.
   
   public void clearImage() {
      for (int row =0;row<BarcodeImage.MAX_HEIGHT;row++)
         for (int column = 0; column<BarcodeImage.MAX_WIDTH; column++)
         {
            this.image.setPixel(row,column,false);
         }
      this.actualHeight=0;
      this.actualWidth=0;
   }  
 

   public String charToCol(char nextChar)
   {
      String temp = Integer.toBinaryString(nextChar);
      while (temp.length()<8)
      {
         temp="0"+temp;
      }
      return temp;
   }


}