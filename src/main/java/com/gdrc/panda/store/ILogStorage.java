/*
 * Copyright 2016, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.panda.store;

import java.util.List;

import com.gdrc.panda.PandaException;

public interface ILogStorage {
	
	
	public LogEntry getLastLog() throws PandaException;
	
	public boolean appendLog(LogEntry log) throws PandaException;
	
	public boolean commitLog(long term,long lastIndex) throws PandaException;
	
	public boolean isContain(long term ,long lastIndex) throws PandaException;
	
	public LogEntry getLog(long term,long index) throws PandaException;
	
	/**
	 * 
	 * If an existing entry conflicts with a new one (same index
but different terms), delete the existing entry and all that
follow it
	delete from term and index
	
	 * */
	public boolean deleteLogFrom(long term,long logIndex) throws PandaException;
	
	public List<LogEntry> getLogEntrysBetween(long startTerm, long startIndex,long endTerm ,long endIndex ) throws PandaException;
	
	
	
}
