package zsoltmester.qcn.quickcircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zsoltmester.qcn.tools.Logger;

public class CoverEventReceiver extends BroadcastReceiver {

	private final Logger logger = Logger.createWithLogTag(CoverEventReceiver.class.getSimpleName());
	// Declared in LGIntent.java of LG Framework
	public static final String ACTION_ACCESSORY_COVER_EVENT = "com.lge.android.intent.action.ACCESSORY_COVER_EVENT";
	private CoverEventListener coverEventListener;

	private CoverEventReceiver(CoverEventListener coverEventListener) {
		this.coverEventListener = coverEventListener;
	}

	public static CoverEventReceiver createWithCoverEventListener(CoverEventListener coverEventListener) {
		return new CoverEventReceiver(coverEventListener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		logger.log("onReceive");
		String action = intent.getAction();
		if (action != null && action.equals(ACTION_ACCESSORY_COVER_EVENT)) {
			handleCoverEvent(intent);
		}
	}

	private void handleCoverEvent(Intent intent) {
		// Declared in LGIntent.java of LG Framework
		final String EXTRA_NAME_ACCESSORY_COVER_STATE = "com.lge.intent.extra.ACCESSORY_COVER_STATE";
		final int EXTRA_VALUE_ACCESSORY_COVER_OPENED = 0;
		final int EXTRA_VALUE_ACCESSORY_COVER_CLOSED = 1;
		
		int coverState = intent.getIntExtra(EXTRA_NAME_ACCESSORY_COVER_STATE, EXTRA_VALUE_ACCESSORY_COVER_OPENED);
		logger.log("coverState: " + coverState);
		switch (coverState) {
			case EXTRA_VALUE_ACCESSORY_COVER_CLOSED:
				coverEventListener.onCoverClosed();
				break;
			case EXTRA_VALUE_ACCESSORY_COVER_OPENED:
				coverEventListener.onCoverOpened();
				break;
		}
	}

	public interface CoverEventListener {
		public void onCoverClosed();
		public void onCoverOpened();
	}
}