package simpledb.file;

import java.nio.ByteBuffer;
import java.nio.charset.*;

public class Page {
   private ByteBuffer bb;

   // For creating data buffers
   public Page(int blocksize) {
      bb = ByteBuffer.allocateDirect(blocksize);
   }
   
   // For creating log pages
   public Page(byte[] b) {
      bb = ByteBuffer.wrap(b);
   }

   public static Charset CHARSET = StandardCharsets.US_ASCII;

   public int getInt(int offset) {
      return bb.getInt(offset);
   }

   public void setInt(int offset, int n) {
	  int fit = bb.capacity();
	  int sum = offset + n;
	  if(sum > fit) {
		  System.out.print("ERROR: The integer" + n + "does not fit at the location" + offset + "page");
	  }
	  else {
		  bb.putInt(offset, n); 
	  }
     
   }

   public byte[] getBytes(int offset) {
      bb.position(offset);
      int length = bb.getInt();
      byte[] b = new byte[length];
      bb.get(b);
      return b; //getting glob of bytes sitting on page
   }

   public void setBytes(int offset, byte[] b) {
      bb.position(offset);
     //writes the integer
      int sum = b.length + offset;
      int fit = bb.capacity();
      if(sum > fit) {
    	  System.out.print("ERROR: The integer" + b.length + "does not fit at the location" + offset + "page");
      }
      else {
    	  bb.putInt(b.length); //writes the integer
          bb.put(b); //writes the bytes
      }
      
   }
   
   public String getString(int offset) {
	   //getChar method read char method
	  bb.position(offset);
	  String all = "";
      char end = '\0';
      char current = bb.getChar();
      while(current != (end)) {
    	  all = all + current;
    	  current = bb.getChar();
      }
      return all; //get string is implemented 
   }

   public void setString(int offset, String s) {
      //converts string to ASCII 2 bytes per char //still a blob of bytes
      //write a char at a time with /0 to the page so get rid of getbytes and set bytes
      //get rid of getbytes and set bytes
      //write each char in string putChar method   
	 //setBytes(offset, b); //for loop going through byte array and appending /0 to every byte
  
      int sum = s.length() + offset;
      int fit = bb.capacity();
      if(sum > fit) {
    	  System.out.print("ERROR: The integer" + s.length() + "does not fit at the location" + offset + "page");
      }
      else {
    	  char end = '\0';
    	  String appended = "";
    	  int size = maxLength(s.length());
    	  for(int i = 0; i < size; i++) {
    		  appended += s.charAt(i);
    	  }
    	  appended = appended + end;
    	  bb.position(offset);
    	  for(int i =0; i<appended.length();i++) {
        	  bb.putChar(appended.charAt(i));
        	 
          }
      }
    
   }

   public static int maxLength(int strlen) {
      float bytesPerChar = CHARSET.newEncoder().maxBytesPerChar();
      return Integer.BYTES + (2*strlen * (int)bytesPerChar);
 
   }

   // a package private method, needed by FileMgr
   ByteBuffer contents() {
      bb.position(0);
      return bb;
   }
}
