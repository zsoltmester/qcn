package zsoltmester.qcn.quickcircle;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import zsoltmester.qcn.R;
import zsoltmester.qcn.tools.Logger;

import static android.widget.RelativeLayout.LayoutParams;

public abstract class QuickCircleBaseActivity extends Activity implements CoverEventReceiver.CoverEventListener {

	private static final String COVER_RESOURCE_TYPE = "dimen";
	private static final String COVER_RESOURCE_PACKAGE = "com.lge.internal";

	private final Logger logger = Logger.createWithLogTag(QuickCircleBaseActivity.class.getSimpleName());
	private CoverEventReceiver coverEventReceiver = CoverEventReceiver.createWithCoverEventListener(this);
	private Context applicationContext;
	private boolean isG3;

	private Resources resources;
	private FrameLayout coverView;
	private int circleHeight;
	private int circleXpos;
	private int circleYpos;
	private int circleDiameter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applicationContext = getApplicationContext();
		resources = getResources();
		checkDevice();
		registerCoverEventReceiver();
		initWindow();
		initCoverLayout();
	}

	private void checkDevice() {
		String device = android.os.Build.DEVICE;
		isG3 = device.equals("g3") || device.equals("tiger6");
		logger.log("isG3:" + isG3);
	}

	private void registerCoverEventReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CoverEventReceiver.ACTION_ACCESSORY_COVER_EVENT);
		applicationContext.registerReceiver(coverEventReceiver, filter);
	}

	private void initWindow() {
		Window window = getWindow();
		if (window != null) {
			window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					| WindowManager.LayoutParams.FLAG_FULLSCREEN
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private void initCoverLayout() {
		initCoverInformationFromDB();
		setContentView(getCoverLayoutIdentifier());
		coverView = (FrameLayout) findViewById(R.id.cover);
		cropLayoutForTheCircleSize();
	}

	private void initCoverInformationFromDB() {
		circleHeight = getSizeResourceInPixelFromDB("config_circle_window_height");
		circleXpos = getSizeResourceInPixelFromDB("config_circle_window_x_pos");
		circleYpos = getSizeResourceInPixelFromDB("config_circle_window_y_pos");
		circleDiameter = getSizeResourceInPixelFromDB("config_circle_diameter");
	}

	private int getSizeResourceInPixelFromDB(String resourceName) {
		int resourceIdentifier = resources.getIdentifier(resourceName, COVER_RESOURCE_TYPE, COVER_RESOURCE_PACKAGE);
		return resources.getDimensionPixelSize(resourceIdentifier);
	}

	/**
	 * A layout id for the activity.
	 * It has to contain a {@link FrameLayout} with id "cover".
	 * That {@link FrameLayout} has to contain the layout of the circle.
	 *
	 * @return The activity layout identifier.
	 */
	protected abstract int getCoverLayoutIdentifier();

	private void cropLayoutForTheCircleSize() {
		LayoutParams layoutParam = (LayoutParams) coverView.getLayoutParams();
		layoutParam.leftMargin = circleXpos < 0 ? circleXpos : layoutParam.leftMargin;
		layoutParam.topMargin = isG3 ? circleYpos : circleYpos + (circleHeight - circleDiameter) / 2;
		coverView.setLayoutParams(layoutParam);
	}

	@Override
	protected void onDestroy() {
		logger.log("onDestroy");
		super.onDestroy();
		applicationContext.unregisterReceiver(coverEventReceiver);
	}

	@Override
	public void onCoverClosed() {
		initWindow();
	}

	@Override
	public void onCoverOpened() {
		finish();
	}
}
