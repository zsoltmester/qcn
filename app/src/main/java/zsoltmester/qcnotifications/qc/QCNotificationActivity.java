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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import zsoltmester.qcnotifications.R;
import zsoltmester.qcnotifications.notifications.NotificationHelper;
import zsoltmester.qcnotifications.notifications.QCNotificationAdapter;
import zsoltmester.qcnotifications.notifications.QCNotificationListener;
import zsoltmester.qcnotifications.notifications.QCNotificationListener.QCNotificationBinder;

public class QCNotificationActivity extends QCBaseActivity implements ServiceConnection,
		QCNotificationListener.Callback {
	private final String TAG = QCNotificationActivity.class.getSimpleName();

	private List<StatusBarNotification> nfs =
			Collections.synchronizedList(new LinkedList<StatusBarNotification>());

	private RecyclerView rv;
	private QCNotificationAdapter adapter;
	private boolean isListAlreadyInit;

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

		rv = (RecyclerView) findViewById(R.id.notification_list);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		rv.setLayoutManager(layoutManager);

		// TODO maybe init the adapter here?

		initList();
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
		QCNotificationListener listener = ((QCNotificationBinder) service).getService();

		// TODO in AsyncTask
		nfs.addAll(Arrays.asList(listener.getActiveNotifications()));
		listener.setCallback(this);

		initList();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d(TAG, "onServiceDisconnected");
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.d(TAG, "onNotificationPosted");
		Log.d(TAG, sbn.getPackageName());
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d(TAG, "onNotificationRemoved");
		Log.d(TAG, sbn.getPackageName());
	}

	/**
	 * Init the list, run only once.
	 */
	private void initList() {
		// TODO 3 gmail notification appear instead of 1. Maybe CATEGORY?

		if (rv == null || nfs.size() == 0) {
			return;
		}

		if (isListAlreadyInit) {
			return;
		}

		isListAlreadyInit = true;

		NotificationHelper.selectNotNullNotifications(nfs);

		if (nfs.size() == 0) {
			return;
		}

		NotificationHelper.sortNotificationsByPriority(nfs);

		adapter = new QCNotificationAdapter(nfs, getResources());
		rv.setAdapter(adapter);
	}
}
