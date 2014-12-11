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
import zsoltmester.qcnotifications.notifications.QCNotificationAdapter;
import zsoltmester.qcnotifications.notifications.QCNotificationListener;
import zsoltmester.qcnotifications.notifications.QCNotificationListener.QCNotificationBinder;

public final class QCNotificationActivity extends QCBaseActivity implements ServiceConnection {

	private final String TAG = QCNotificationActivity.class.getSimpleName();

	private boolean isBound;
	private String[] packageNames;

	private RecyclerView notificationList;
	private QCNotificationAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		final Intent intent = new Intent(this, QCNotificationListener.class);
		intent.setAction(QCNotificationListener.ACTION_NOTIFICATION_LISTENER);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		notificationList = (RecyclerView) findViewById(R.id.notification_list);

		layoutManager = new LinearLayoutManager(this);
		notificationList.setLayoutManager(layoutManager);

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
		final QCNotificationListener listener = ((QCNotificationBinder) service).getService();
		isBound = true;

		StatusBarNotification[] notifications = listener.getActiveNotifications();

		Log.d(TAG, String.valueOf(notifications.length));

		packageNames = new String[notifications.length];

		for(int i = 0; i < notifications.length; ++i) {
			Log.d(TAG, notifications[i].getPackageName());
			packageNames[i] = notifications[i].getPackageName();
		}

		updateList();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		isBound = false;
	}

	private void updateList() {
		if (adapter == null) {
			adapter = new QCNotificationAdapter(packageNames);
			notificationList.setAdapter(adapter);
		} else if (adapter.getPackageNames() != packageNames){
			adapter.updatePackageNames(packageNames);
			adapter.notifyDataSetChanged();
		}
	}
}
