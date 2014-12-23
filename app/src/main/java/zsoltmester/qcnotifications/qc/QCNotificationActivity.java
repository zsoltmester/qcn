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

	private final List<StatusBarNotification> nfs =
			Collections.synchronizedList(new LinkedList<StatusBarNotification>());
	private QCNotificationListener nl;
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

		RecyclerView rv = (RecyclerView) findViewById(R.id.notification_list);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		rv.setLayoutManager(layoutManager);

		adapter = new QCNotificationAdapter(nfs, getResources());
		rv.setAdapter(adapter);

		// TODO set animator for recycle view
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

		nfs.addAll(Arrays.asList(nl.getActiveNotifications()));

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

		nl.setCallback(this);
	}
}
