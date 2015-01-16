package zsoltmester.qcn.qc;

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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import zsoltmester.qcn.R;
import zsoltmester.qcn.notifications.NotificationHelper;
import zsoltmester.qcn.notifications.QCNotificationAdapter;
import zsoltmester.qcn.notifications.QCNotificationListener;
import zsoltmester.qcn.notifications.QCNotificationListener.QCNotificationBinder;

public class QCNotificationActivity extends QCBaseActivity implements ServiceConnection,
		QCNotificationListener.Callback {
	private final String TAG = QCNotificationActivity.class.getSimpleName();

	private final List<StatusBarNotification> nfs =
			Collections.synchronizedList(new LinkedList<StatusBarNotification>());
	private QCNotificationListener nl;
	private QCNotificationAdapter adapter;

	private RecyclerView notificationListDisplayerView;
	private GestureDetector gestureDetector;

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		Intent intent = new Intent(this, QCNotificationListener.class);
		intent.setAction(QCNotificationListener.ACTION_NOTIFICATION_LISTENER);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		notificationListDisplayerView = (RecyclerView) findViewById(R.id.notification_list);

		gestureDetector = new GestureDetector(this, new SimpleOnGestureListenerWithDoubleTapHandler());
		notificationListDisplayerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		notificationListDisplayerView.setLayoutManager(layoutManager);

		adapter = new QCNotificationAdapter(nfs, getResources());
		notificationListDisplayerView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.d(TAG, "onServiceConnected");

		nl = ((QCNotificationBinder) service).getService();

		initList();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d(TAG, "onServiceDisconnected");
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn, String[] rm) {
		Log.d(TAG, "onNotificationPosted");

		if (NotificationHelper.isDisplayable(sbn)) {
			NotificationHelper.insertNotification(nfs, sbn, rm);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn, String[] rm) {
		Log.d(TAG, "onNotificationRemoved");

		NotificationHelper.deleteNotification(nfs, sbn, rm);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onNotificationRankingUpdate(String[] rm) {
		Log.d(TAG, "onNotificationRankingUpdate");

		NotificationHelper.sortNotifications(nfs, rm);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Init the list, run only once.
	 */
	private void initList() {
		// TODO more gmail notification init instead of 1.

		try {
			nfs.addAll(Arrays.asList(nl.getActiveNotifications()));
			nl.setCallback(this);
		} catch (NullPointerException e) {
			requirePermissionForAccessNotifications();
			return;
		}

		synchronized (nfs) {
			if (nfs.size() == 0) {
				return;
			}

			NotificationHelper.selectValidNotifications(nfs);

			if (nfs.size() == 0) {
				return;
			}

			String[] rm = nl.getCurrentRanking().getOrderedKeys();
			NotificationHelper.sortNotifications(nfs, rm);

			adapter.notifyDataSetChanged();
		}
	}

	private void requirePermissionForAccessNotifications() {
		Log.d(TAG, "requirePermissionForAccessNotifications");

		if (notificationListDisplayerView != null) {
			notificationListDisplayerView.setVisibility(View.GONE);
		}

		TextView errorView = (TextView) findViewById(R.id.error);
		errorView.setVisibility(View.VISIBLE);
		errorView.setText(R.string.error_no_permission_for_access_notifications);

		isRequirePermissionForAccessNotifications = true;
	}
	
	private void requirePermissionForLockTheScreen() {
		Log.d(TAG, "requirePermissionForLockTheScreen");

		if (notificationListDisplayerView != null) {
			notificationListDisplayerView.setVisibility(View.GONE);
		}

		TextView errorView = (TextView) findViewById(R.id.error);
		errorView.setVisibility(View.VISIBLE);
		errorView.setText(R.string.error_no_permission_for_lock_the_screen);
		
		isRequirePermissionForLockTheScreen = true;
	}


	public class SimpleOnGestureListenerWithDoubleTapHandler extends GestureDetector.SimpleOnGestureListener {

		private final String TAG = SimpleOnGestureListenerWithDoubleTapHandler.class.getSimpleName();

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d(TAG, "onDoubleTap");

			DevicePolicyManager devicePolicyManager = 
					(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			
			if (devicePolicyManager.isAdminActive(deviceAdminReceiverComponentName)) {
				devicePolicyManager.lockNow();
			} else {
				requirePermissionForLockTheScreen();
			}

			return true;
		}
	}
	
	public static class DeviceAdminListener extends DeviceAdminReceiver {
	}
}
