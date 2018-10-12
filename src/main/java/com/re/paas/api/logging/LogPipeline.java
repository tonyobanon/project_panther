package com.re.paas.api.logging;

import java.io.PrintStream;

import com.re.paas.api.logging.Logger.VerboseLevel;

public abstract class LogPipeline {
	
	public abstract void println_out(String line);
	
	public abstract void println_err(String line);
	
	public void println(VerboseLevel level, String line) {
		boolean isError = (level == VerboseLevel.ERROR || level == VerboseLevel.FATAL);
		if(isError) {
			println_err(line);
		} else {
			println_out(line);
		}
	}
	
	public static LogPipeline from(PrintStream outStream, PrintStream errStream) {
		return new LogPipeline() {
			
			@Override
			public void println_out(String line) {
				outStream.println(line);
			}
			
			@Override
			public void println_err(String line) {
				errStream.println(line);
			}
		};
	}
	
}
