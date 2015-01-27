package zsoltmester.qcn.quickcircle.notifications;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import zsoltmester.qcn.R;
import zsoltmester.qcn.quickcircle.QuickCircleBaseActivity;
import zsoltmester.qcn.quickcircle.notifications.NotificationListener.HelperBinder;
import zsoltmester.qcn.tools.Logger;
import zsoltmester.qcn.ui.SimpleOnGestureListenerWithDoubleTapSupport;
import zsoltmester.qcn.ui.SwipeDismissRecyclerViewTouchListener;

import static android.view.GestureDetector.OnGestureListener;

// TODO Az egész notification helpert hívogató részt át lehetne tenni a listenerbe.
public class NotificationActivity extends QuickCircleBaseActivity implements ServiceConnection,
		NotificationListener.Callback, SimpleOnGestureListenerWithDoubleTapSupport.OnDoubleTapListener {

	private Logger logger = Logger.createWithLogTag(NotificationActivity.class.getSimpleName());
	private RecyclerView notificationsListView;
	private NotificationAdapter notificationAdapter;
	private NotificationListener notificationListener;
	private boolean doNotHaveAccessToNotifications;
	private boolean doNotHaveAccessForLockTheScreen;
	private final List<StatusBarNotification> statusBarNotifications = new LinkedList<>();
	private ComponentName deviceAdminReceiverComponentName;

	@Override
	protected int getCoverLayoutIdentifier() {
		return R.layout.activity_notification;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		logger.log("onCreate");
		super.onCreate(savedInstanceState);
		initNotificationsAdapter();
		initNotificationsContainerView();
		initBackButton();
		deviceAdminReceiverComponentName = new ComponentName(this, DeviceAdminListener.class);
	}

	private void initNotificationsAdapter() {
		notificationAdapter =
				NotificationAdapter.createFromNotificationsAndResources(statusBarNotifications, getResources());
	}

	private void initNotificationsContainerView() {
		notificationsListView = (RecyclerView) findViewById(R.id.notification_list_container);
		notificationsListView.setLayoutManager(new LinearLayoutManager(this));
		initTouchListener();
		notificationsListView.setAdapter(notificationAdapter);
	}

	private void initBackButton() {
		findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initTouchListener() {
		SwipeDismissRecyclerViewTouchListener touchListener = getSwipeDismissRecyclerViewTouchListener();
		touchListener.setGestureDetector(getGestureDetector());
		notificationsListView.setOnTouchListener(touchListener);
		notificationsListView.setOnScrollListener(touchListener.makeScrollListener());
	}

	private SwipeDismissRecyclerViewTouchListener getSwipeDismissRecyclerViewTouchListener() {
		return new SwipeDismissRecyclerViewTouchListener(notificationsListView,
				new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							notificationListener.cancelNotification(statusBarNotifications.get(position).getKey());
						}
					}
				});
	}
	
	private GestureDetector getGestureDetector() {
		OnGestureListener simpleOnGestureListenerWithDoubleTapSupport =
				SimpleOnGestureListenerWithDoubleTapSupport.createWithOnDoubleTapListener(this);
		return new GestureDetector(this, simpleOnGestureListenerWithDoubleTapSupport);
	}

	@Override
	protected void onStart() {
		logger.log("onStart");
		super.onStart();
		bindThisActivityToTheNotificationListenerService();
	}

	private void bindThisActivityToTheNotificationListenerService() {
		Intent intentToBindToTheNotificationListenerService = new Intent(this, NotificationListener.class);
		intentToBindToTheNotificationListenerService.setAction(NotificationListener.ACTION_NOTIFICATION_LISTENER);
		bindService(intentToBindToTheNotificationListenerService, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		logger.log("onDestroy");
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		logger.log("onServiceConnected");
		synchronized (statusBarNotifications) {
			notificationListener = ((HelperBinder) service).getRunningNotificationListenerService();
			initStatusBarNotifications();
			displayStatusBarNotifications();
		}
	}

	private void initStatusBarNotifications() {
		try {
			notificationListener.setCallbackListener(this);
			statusBarNotifications.addAll(Arrays.asList(notificationListener.getActiveNotifications()));
		} catch (NullPointerException e) {
			requirePermissionForAccessNotifications();
		}
	}

	private void requirePermissionForAccessNotifications() {
		logger.log("requirePermissionForAccessNotifications");
		displayErrorViewWithTheGivenText(R.string.error_no_permission_for_access_notifications);
		doNotHaveAccessToNotifications = true;
	}

	private void displayErrorViewWithTheGivenText(int textResourceIdentifier) {
		if (notificationsListView != null) {
			notificationsListView.setVisibility(View.GONE);
		}
		TextView errorView = (TextView) findViewById(R.id.error_view);
		errorView.setVisibility(View.VISIBLE);
		errorView.setText(textResourceIdentifier);
	}

	private void displayStatusBarNotifications() {
		NotificationHelper.selectDisplayableNotificationsFromAList(statusBarNotifications);
		if (statusBarNotifications.size() == 0) {
			// Nothing to display.
			return;
		}
		sortStatusBarNotifications();
		notificationAdapter.notifyDataSetChanged();
	}

	private void sortStatusBarNotifications() {
		String[] orderedRankingMapKeys = notificationListener.getCurrentRanking().getOrderedKeys();
		NotificationHelper.sortNotificationsBasedOnRankingMap(statusBarNotifications, orderedRankingMapKeys);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		logger.log("onServiceDisconnected");
	}

	@Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification, String[] orderedRankingMapKeys) {
		logger.log("onNotificationPosted");
		synchronized (statusBarNotifications) {
			if (!NotificationHelper.isDisplayable(statusBarNotification)) {
				// Nothing to add.
				return;
			}
			NotificationHelper.insertNotificationBasedOnRankingMap(statusBarNotifications, statusBarNotification,
					orderedRankingMapKeys);
			NotificationHelper.sortNotificationsBasedOnRankingMap(statusBarNotifications, orderedRankingMapKeys);
			notifyAdapterFromANonUiThread();
		}
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification, String[] orderedRankingMapKeys) {
		logger.log("onNotificationRemoved");
		synchronized (statusBarNotifications) {
			NotificationHelper.deleteNotification(statusBarNotifications, statusBarNotification);
			NotificationHelper.sortNotificationsBasedOnRankingMap(statusBarNotifications, orderedRankingMapKeys);
			notifyAdapterFromANonUiThread();
		}
	}

	@Override
	public void onNotificationRankingUpdate(String[] orderedRankingMapKeys) {
		logger.log("onNotificationRankingUpdate");
		synchronized (statusBarNotifications) {
			NotificationHelper.sortNotificationsBasedOnRankingMap(statusBarNotifications, orderedRankingMapKeys);
			notifyAdapterFromANonUiThread();
		}
	}

	private void notifyAdapterFromANonUiThread() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notificationAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onDoubleTap() {
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		if (devicePolicyManager.isAdminActive(deviceAdminReceiverComponentName)) {
			devicePolicyManager.lockNow();
		} else {
			requirePermissionForLockTheScreen();
		}
	}

	private void requirePermissionForLockTheScreen() {
		logger.log("requirePermissionForLockTheScreen");
		displayErrorViewWithTheGivenText(R.string.error_no_permission_for_lock_the_screen);
		doNotHaveAccessForLockTheScreen = true;
	}

	@Override
	public void onCoverOpened() {
		if (doNotHaveAccessToNotifications) {
			openNotificationListenerSettings();
		}
		if (doNotHaveAccessForLockTheScreen) {
			addThisAppAsDeviceAdmin();
		}
		super.onCoverOpened();
	}

	private void openNotificationListenerSettings() {
		startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
	}

	private void addThisAppAsDeviceAdmin() {
		Intent intentToAddThisAppAsDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intentToAddThisAppAsDeviceAdmin.putExtra(
				DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiverComponentName);
		startActivity(intentToAddThisAppAsDeviceAdmin);
	}

	/**
	 * Must have class for register this app as device admin.
	 */
	public static class DeviceAdminListener extends DeviceAdminReceiver {
	}
}