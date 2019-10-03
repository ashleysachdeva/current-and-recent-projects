package simpledb.tx.recovery;

import simpledb.file.Page;
import simpledb.log.LogMgr;
import simpledb.tx.Transaction;
import java.util.*;


public class NQCheckpoint implements LogRecord {
	int txnum;
	boolean commited = false;
	public NQCheckpoint(Page p){
		int tpos = Integer.BYTES;
	    txnum = p.getInt(tpos);
	    if(p.getInt(0)== COMMIT) {
	    	commited = true;
	    }
	    
		
	}
	
	@Override
	public int op() {
		return NQCHECKPOINT;
	}

	@Override
	public int txNumber() {
		if(commited) {
			return -1;
		}
		else {
			return txnum;
		}
	}

	@Override
	public void undo(Transaction tx) {}
	
	
	

}
