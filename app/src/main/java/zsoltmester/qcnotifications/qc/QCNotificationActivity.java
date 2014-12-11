package zsoltmester.qcnotifications.qc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import zsoltmester.qcnotifications.notifications.QCNotificationListener;
import zsoltmester.qcnotifications.notifications.QCNotificationListener.QCNotificationBinder;

public class QCNotificationActivity extends QCBaseActivity implements ServiceConnection {

	private final String TAG = QCNotificationActivity.class.getSimpleName();

	private QCNotificationListener service;
	private boolean isBound;

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		final Intent intent = new Intent(this, QCNotificationListener.class);
		intent.setAction(QCNotificationListener.ACTION_NOTIFICATION_LISTENER);
		bindService(intent, this, BIND_ADJUST_WITH_ACTIVITY);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		this.service = ((QCNotificationBinder) service).getService();
		isBound = true;

		StatusBarNotification[] notifications = this.service.getActiveNotifications();
		Log.d(TAG, String.valueOf(notifications.length));
		for(StatusBarNotification notification : notifications) {
			Log.d(TAG, notification.getPackageName());
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		isBound = false;
	}
}
