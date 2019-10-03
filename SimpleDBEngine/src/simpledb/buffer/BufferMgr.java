package simpledb.buffer;

import simpledb.file.*;
import java.util.*; 
import simpledb.log.LogMgr;
import java.io.*; 
import java.util.HashMap; 
import java.util.Map;
import java.util.LinkedList; 
import java.util.Queue;
/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
public class BufferMgr {
   private int numAvailable;
   private static final long MAX_TIME = 10000; // 10 seconds
   Queue<Buffer> all = new LinkedList(); //this is for my personal life understanding
   Queue<Buffer> tracker = new LinkedList();
   HashMap<BlockId,String> hockey = new HashMap<BlockId, String>();
   Buffer bb;
   
   /**
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on a {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} object.
    * @param numbuffs the number of buffer slots to allocate
    */
   public BufferMgr(FileMgr fm, LogMgr lm, int numbuffs) {
      for(int i = 0; i < numbuffs; i++) {
    	  bb = new Buffer(fm,lm,i); //it should work with his test case 
    	  all.add(bb);
    	  if(!bb.isPinned()) {
    		  tracker.add(bb);
    	  }
    	  if(bb.contents()!=null) {
    		  hockey.put(bb.block(),"allocated");
    	  }
      }
    	  
      }
   
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   public synchronized int available() {
      return numAvailable;
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   public synchronized void flushAll(int txnum) {
      for (Buffer buff : all)
         if (buff.modifyingTx() == txnum)
         buff.flush();
   }
   
   
   /**
    * Unpins the specified data buffer. If its pin count
    * goes to zero, then notify any waiting threads.
    * @param buff the buffer to be unpinned
    */
   public synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned()) {
         numAvailable++;
         tracker.add(buff);
         notifyAll();

      }

   }
   
   /**
    * Pins a buffer to the specified block, potentially
    * waiting until a buffer becomes available.
    * If no buffer becomes available within a fixed 
    * time period, then a {@link BufferAbortException} is thrown.
    * @param blk a reference to a disk block
    * @return the buffer pinned to that block
    */
   public synchronized Buffer pin(BlockId blk) {
      try {
         long timestamp = System.currentTimeMillis();
         Buffer buff = tryToPin(blk);
         while (buff == null && !waitingTooLong(timestamp)) {
            wait(MAX_TIME);
            buff = tryToPin(blk);
         }
         if (buff == null)
            throw new BufferAbortException();
         return buff;
      }
      catch(InterruptedException e) {
         throw new BufferAbortException();
      }
   }  
   
   private boolean waitingTooLong(long starttime) {
      return System.currentTimeMillis() - starttime > MAX_TIME;
   }
   
   /**
    * Tries to pin a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   private Buffer tryToPin(BlockId blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
         hockey.put(buff.block(), "allocated");
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   private Buffer findExistingBuffer(BlockId blk) {
      for (Buffer buff  : all) {
         BlockId b = buff.block();
         if (b != null && b.equals(blk))
            return buff;
      }
      return null;
   }
   
   
   
   private Buffer chooseUnpinnedBuffer() {
	   	if (tracker.size() != 0) {
	   		Buffer head = tracker.peek();
	   		hockey.remove(head.block());
	   		return tracker.remove();
	   	
	   		
	   	}
        
      return null;
   }
   private void printStatus() {
	   ArrayList unpin = new ArrayList();
	   String check = "";
	   String listofUnpinned = "";
	   Set <BlockId> keys = hockey.keySet();
	   System.out.println("Allocated Buffers: /n");
	   for(BlockId key: keys) {
		   for(Buffer item : all) {
		   	if(item.block() == key) {
		   		if(item.isPinned()) {
		   			check = "pinned";
		   			
		   	
		   		}
		   		if(!item.isPinned()) {
		   			check = "unpinned";
		   			unpin.add(key);
		   		}
		   		System.out.println("Buffer "+ key + ":" + item.id() + check); //automatically toStrings it
		   		if (unpin.size()==0) {
		   			listofUnpinned = "0";
		   		}
			   
		   }
	   }
	 }
	   for(int i =0; i< unpin.size();i++) {
  			listofUnpinned += unpin.get(i) + " ";
  		}
  	System.out.println("Unpinned buffers in LRU order:" + listofUnpinned);
	    
		  
	   
	   
	 
	   
   }
   
   
   
}
