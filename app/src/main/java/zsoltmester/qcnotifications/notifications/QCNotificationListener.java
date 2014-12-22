package zsoltmester.qcnotifications.notifications;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class QCNotificationListener extends NotificationListenerService {

	private final String TAG = QCNotificationListener.class.getSimpleName();

	public static final String ACTION_NOTIFICATION_LISTENER =
			"zsoltmester.qcnotifications.notifications.NOTIFICATION_LISTENER";

	private IBinder binder = new QCNotificationBinder();
	private Callback cb;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");

		return intent.getAction().equals(ACTION_NOTIFICATION_LISTENER) ? binder : super.onBind(intent);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.d(TAG, "onNotificationPosted");
		if (cb != null) {
			cb.onNotificationPosted(sbn);
		}
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d(TAG, "onNotificationRemoved");
		if (cb != null) {
			cb.onNotificationRemoved(sbn);
		}
	}

	public void setCallback(Callback cb) {
		this.cb = cb;
	}

	public interface Callback {
		void onNotificationPosted(StatusBarNotification sbn);
		void onNotificationRemoved(StatusBarNotification sbn);
	}

	/**
	 * {@link Binder} which helps to get the {@link QCNotificationListener} service.
	 */
	public class QCNotificationBinder extends Binder {

		/**
		 * @return The running {@link QCNotificationListener} service instance.
		 */
		public QCNotificationListener getService() {
			return QCNotificationListener.this;
		}
	}
}
