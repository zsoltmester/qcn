package zsoltmester.qcn.notifications;

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
	public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
		Log.d(TAG, "onNotificationRemoved");
		if (cb != null) {
			cb.onNotificationRemoved(sbn, rankingMap.getOrderedKeys());
		}
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
		Log.d(TAG, "onNotificationPosted");
		if (cb != null) {
			cb.onNotificationPosted(sbn, rankingMap.getOrderedKeys());
		}
	}

	@Override
	public void onNotificationRankingUpdate(RankingMap rankingMap) {
		Log.d(TAG, "onNotificationRankingUpdate");
		if (cb != null) {
			cb.onNotificationRankingUpdate(rankingMap.getOrderedKeys());
		}
	}

	public void setCallback(Callback cb) {
		this.cb = cb;
	}

	public interface Callback {
		void onNotificationPosted(StatusBarNotification sbn, String[] rm);

		void onNotificationRemoved(StatusBarNotification sbn, String[] rm);

		void onNotificationRankingUpdate(String[] rm);
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
