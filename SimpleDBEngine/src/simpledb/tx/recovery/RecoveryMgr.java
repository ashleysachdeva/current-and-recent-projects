package simpledb.tx.recovery;

import java.lang.reflect.Array;
import java.util.*;
import simpledb.file.*;
import simpledb.log.*;
import simpledb.buffer.*;
import simpledb.tx.Transaction;
import static simpledb.tx.recovery.LogRecord.*;

/**
 * The recovery manager.  Each transaction has its own recovery manager.
 * @author Edward Sciore
 */
public class RecoveryMgr {
   private LogMgr lm;
   private BufferMgr bm;
   private Transaction tx;
   private int txnum;

   /**
    * Create a recovery manager for the specified transaction.
    * @param txnum the ID of the specified transaction
    */
   public RecoveryMgr(Transaction tx, int txnum, LogMgr lm, BufferMgr bm) {
      this.tx = tx;
      this.txnum = txnum;
      this.lm = lm;
      this.bm = bm;
      StartRecord.writeToLog(lm, txnum);
   }

   /**
    * Write a commit record to the log, and flushes it to disk.
    */
   public void commit() {
      bm.flushAll(txnum);
      int lsn = CommitRecord.writeToLog(lm, txnum);
      lm.flush(lsn);
   }

   /**
    * Write a rollback record to the log and flush it to disk.
    */
   public void rollback() {
      doRollback();
      bm.flushAll(txnum);
      int lsn = RollbackRecord.writeToLog(lm, txnum);
      lm.flush(lsn);
   }

   /**
    * Recover uncompleted transactions from the log
    * and then write a quiescent checkpoint record to the log and flush it.
    */
   public void recover() {
      doRecover();
      bm.flushAll(txnum);
      int lsn = CheckpointRecord.writeToLog(lm);
      lm.flush(lsn);
   }

   /**
    * Write a setint record to the log and return its lsn.
    * @param buff the buffer containing the page
    * @param offset the offset of the value in the page
    * @param newval the value to be written
    */
   public int setInt(Buffer buff, int offset, int newval) {
      int oldval = buff.contents().getInt(offset);
      BlockId blk = buff.block();
      return SetIntRecord.writeToLog(lm, txnum, blk, offset, oldval);
   }

   /**
    * Write a setstring record to the log and return its lsn.
    * @param buff the buffer containing the page
    * @param offset the offset of the value in the page
    * @param newval the value to be written
    */
   public int setString(Buffer buff, int offset, String newval) {
      String oldval = buff.contents().getString(offset);
      BlockId blk = buff.block();
      return SetStringRecord.writeToLog(lm, txnum, blk, offset, oldval);
   }

   /**
    * Rollback the transaction, by iterating
    * through the log records until it finds 
    * the transaction's START record,
    * calling undo() for each of the transaction's
    * log records.
    */
   private void doRollback() {
      Iterator<byte[]> iter = lm.iterator();
      while (iter.hasNext()) {
         byte[] bytes = iter.next();
         LogRecord rec = LogRecord.createLogRecord(bytes); 
         if (rec.txNumber() == txnum) {
            if (rec.op() == START)
               return;
            rec.undo(tx);
         }
      }
   }

   /**
    * Do a complete database recovery.
    * The method iterates through the log records.
    * Whenever it finds a log record for an unfinished
    * transaction, it calls undo() on that record.
    * The method stops when it encounters a CHECKPOINT record
    * or the end of the log.
    */
   private void doRecover() {
      Collection<Integer> finishedTxs = new ArrayList<>(); //finished txs 
      Iterator<byte[]> iter = lm.iterator(); //iterates through the log manager
      ArrayList<Integer> nqpts = new ArrayList<>();
      while(iter.hasNext()) { //goes thru the logmgr
         byte[] bytes = iter.next();
         LogRecord rec = LogRecord.createLogRecord(bytes);
         if (rec.op() == CHECKPOINT) { //if log record is a checkpoint you just return
            return;
         }
         if (rec.op() == COMMIT || rec.op() == ROLLBACK) {
             finishedTxs.add(rec.txNumber());
         }
         if(rec.op() == SETSTRING || rec.op()== SETINT) {
        	if(finishedTxs.contains(rec.txNumber())){
        		bytes = iter.next();
        	}
        	else {
        		rec.undo(tx);
        	}
         }

         if(rec.op() == NQCHECKPOINT) {
        	 Page p = new Page(bytes);
        	  for(int i =0; i< finishedTxs.size();i++) {
        		  if(!finishedTxs.contains(p.getInt(Integer.BYTES+i))){
             		 nqpts.add(rec.txNumber());
             	 }
    	      }
        	if(nqpts.isEmpty()) {
        		return;
        	}
         }
         if(rec.op() == START) {
        	if(!finishedTxs.contains(rec.txNumber()) && nqpts.contains(rec.txNumber())){
        		return; //if its in the NQcheckpoint and not commited you want to be done
        	}
        	else {
        		bytes = iter.next(); //else keep going 
        	}
 
         }
         else if (!finishedTxs.contains(rec.txNumber()) || finishedTxs.isEmpty()) //if its not commited 
            rec.undo(tx);
      }
   }
   
   private void checkpoint(LogMgr lm, ArrayList<Integer> running) {
	      byte[] rec = new byte[Integer.BYTES];
	      Page p = new Page(rec);
	      p.setInt(0, NQCHECKPOINT);
	      for(int i =0; i< running.size();i++) {
	    	  p.setInt(Integer.BYTES + i, running.get(i) ); //puts the array list into the coinciding stuff
	      }
	      lm.append(rec);
	      
   }
}
