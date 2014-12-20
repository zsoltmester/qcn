package zsoltmester.qcnotifications.qc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import zsoltmester.qcnotifications.R;
import zsoltmester.qcnotifications.notifications.NotificationHelper;
import zsoltmester.qcnotifications.notifications.QCNotificationAdapter;
import zsoltmester.qcnotifications.notifications.QCNotificationListener;
import zsoltmester.qcnotifications.notifications.QCNotificationListener.QCNotificationBinder;

public class QCNotificationActivity extends QCBaseActivity implements ServiceConnection {

	private final String TAG = QCNotificationActivity.class.getSimpleName();

	private StatusBarNotification[] notifications;

	private RecyclerView notificationListView;
	private QCNotificationAdapter adapter;

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

		notificationListView = (RecyclerView) findViewById(R.id.notification_list);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		notificationListView.setLayoutManager(layoutManager);

		updateList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");

		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		QCNotificationListener listener = ((QCNotificationBinder) service).getService();

		notifications = listener.getActiveNotifications();

		Log.d(TAG, String.valueOf(notifications.length));

		updateList();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	}

	private void updateList() {
		if (notifications != null && notifications.length > 0) {
			StatusBarNotification[] updatedNotifications = NotificationHelper.selectNotNullNotifications(notifications);

			if (updatedNotifications.length == 0) {
				return;
			}

			updatedNotifications = NotificationHelper.sortNotificationsByPriority(updatedNotifications);

			if (adapter == null) {
				adapter = new QCNotificationAdapter(updatedNotifications, getResources());
				notificationListView.setAdapter(adapter);
			} else {
				adapter.updateNotificationsArray(updatedNotifications);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
