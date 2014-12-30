package zsoltmester.qcnotifications.qc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import zsoltmester.qcnotifications.R;

class QCBaseActivity extends Activity {

	private final String TAG = QCBaseActivity.class.getSimpleName();

	// Declared in LGIntent.java of LG Framework
	private final String ACTION_ACCESSORY_COVER_EVENT = "com.lge.android.intent.action.ACCESSORY_COVER_EVENT";

	private int circleWidth;
	private int circleHeight;
	private int circleXpos;
	private int circleYpos;
	private int circleDiameter;

	private boolean isG3;
	protected boolean isRequirePermission;

	private Context applicationContext;
	private ContentResolver contentResolver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setContentView(R.layout.activity_qc);
		View coverView = findViewById(R.id.cover);

		applicationContext = getApplicationContext();
		contentResolver = getContentResolver();

		checkDevice();

		registerIntentReceiver();

		setWindowFlags();

		initializeViewInformationFromDB();

		// Crops a layout for the QuickCircle window
		setCircleLayoutParam(coverView);

		initBackBtn(coverView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		applicationContext.unregisterReceiver(intentReceiver);
	}

	private void checkDevice() {
		String device = android.os.Build.DEVICE;
		Log.d(TAG, "device:" + device);
		isG3 = device.equals("g3") || device.equals("tiger6");
		Log.d(TAG, "isG3:" + isG3);
	}

	private void registerIntentReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ACCESSORY_COVER_EVENT);
		applicationContext.registerReceiver(intentReceiver, filter);
	}

	private void setWindowFlags() {
		Window window = getWindow();
		if (window != null) {
			window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_FULLSCREEN
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private void initializeViewInformationFromDB() {

		if (contentResolver == null) {
			return;
		}

		Log.d(TAG, "initializeViewInformationFromDB");

		// OS QuickCircle Settings DB key
		final String QUICKCOVERSETTINGS_QUICKCOVER_ENABLE = "quick_view_enable";

		// Check the availability of the case
		boolean quickCircleEnabled =
				Settings.Global.getInt(contentResolver, QUICKCOVERSETTINGS_QUICKCOVER_ENABLE, 0) == 0;
		Log.d(TAG, "quickCircleEnabled:" + quickCircleEnabled);

		// Get the QuickCircle window information:

		int id = getResources().getIdentifier("config_circle_window_width", "dimen", "com.lge.internal");
		circleWidth = getResources().getDimensionPixelSize(id);
		Log.d(TAG, "circleWidth:" + circleWidth);

		id = getResources().getIdentifier("config_circle_window_height", "dimen", "com.lge.internal");
		circleHeight = getResources().getDimensionPixelSize(id);
		Log.d(TAG, "circleHeight:" + circleHeight);

		id = getResources().getIdentifier("config_circle_window_x_pos", "dimen", "com.lge.internal");
		circleXpos = getResources().getDimensionPixelSize(id);
		Log.d(TAG, "circleXpos:" + circleXpos);

		id = getResources().getIdentifier("config_circle_window_y_pos", "dimen", "com.lge.internal");
		circleYpos = getResources().getDimensionPixelSize(id);
		Log.d(TAG, "circleYpos:" + circleYpos);

		id = getResources().getIdentifier("config_circle_diameter", "dimen", "com.lge.internal");
		circleDiameter = getResources().getDimensionPixelSize(id);
		Log.d(TAG, "circleDiameter:" + circleDiameter);
	}

	private void setCircleLayoutParam(View view) {

		FrameLayout layout = (FrameLayout) view;
		RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) layout.getLayoutParams();

		if (circleXpos < 0) {
			layoutParam.leftMargin = circleXpos;
		}

		// Set top margin to the offset
		if (isG3) {
			layoutParam.topMargin = circleYpos;
		} else {
			layoutParam.topMargin = circleYpos + (circleHeight - circleDiameter) / 2;
		}

		layout.setLayoutParams(layoutParam);
	}

	private void initBackBtn(View parent) {
		parent.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QCBaseActivity.this.finish();
			}
		});
	}

	private final BroadcastReceiver intentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");

			String action = intent.getAction();

			if (action == null) {
				return;
			}

			Log.d(TAG, action);

			switch (action) {
				case ACTION_ACCESSORY_COVER_EVENT:
					handleCoverEvent(intent);
					break;
			}
		}

		private void handleCoverEvent(Intent intent) {
			// Declared in LGIntent.java of LG Framework
			final String EXTRA_NAME_ACCESSORY_COVER_STATE = "com.lge.intent.extra.ACCESSORY_COVER_STATE";
			final int EXTRA_VALUE_ACCESSORY_COVER_OPENED = 0;
			final int EXTRA_VALUE_ACCESSORY_COVER_CLOSED = 1;

			// Gets the current state of the cover
			int quickCoverState =
					intent.getIntExtra(EXTRA_NAME_ACCESSORY_COVER_STATE, EXTRA_VALUE_ACCESSORY_COVER_OPENED);

			Log.d(TAG, "quickCoverState:" + quickCoverState);

			switch (quickCoverState) {
				case EXTRA_VALUE_ACCESSORY_COVER_CLOSED:
					setWindowFlags();
					break;

				case EXTRA_VALUE_ACCESSORY_COVER_OPENED:
					if (isRequirePermission) {
						startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
					}
					QCBaseActivity.this.finish();
			}
		}
	};
}
