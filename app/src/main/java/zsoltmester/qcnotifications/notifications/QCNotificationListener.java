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

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");

		return intent.getAction().equals(ACTION_NOTIFICATION_LISTENER) ? binder : super.onBind(intent);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		// TODO refresh
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		// TODO delete
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
