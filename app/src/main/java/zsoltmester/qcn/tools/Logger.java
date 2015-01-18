package zsoltmester.qcn.tools;

import android.util.Log;

public class Logger {
	
	private final boolean IS_RELEASE_MODE = false;
	
	private String logTag;
	
	private Logger(String logTag) {
		this.logTag = logTag;
	}
	
	public static Logger createWithLogTag(String logTag) {
		return new Logger(logTag);
	}
	
	public void log(String message) {
		if (IS_RELEASE_MODE) {
			// Nothing to log in release.
			return;
		}
		
		Log.d(logTag, message);
	}
}
