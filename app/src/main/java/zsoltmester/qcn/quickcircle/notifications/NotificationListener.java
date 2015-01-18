package zsoltmester.qcn.quickcircle.notifications;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import zsoltmester.qcn.tools.Logger;

public class NotificationListener extends NotificationListenerService {

	private final Logger logger = Logger.createWithLogTag(NotificationListener.class.getSimpleName());
	public static final String ACTION_NOTIFICATION_LISTENER = "zsoltmester.qcn.notifications.NOTIFICATION_LISTENER";
	private IBinder helperBinder = new HelperBinder();
	private Callback callbackListener;

	@Override
	public IBinder onBind(Intent intent) {
		logger.log("onBind");
		return intent.getAction().equals(ACTION_NOTIFICATION_LISTENER) ? helperBinder : super.onBind(intent);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
		logger.log("onNotificationRemoved");
		if (callbackListener != null) {
			callbackListener.onNotificationRemoved(statusBarNotification, rankingMap.getOrderedKeys());
		}
	}

	@Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification, RankingMap rankingMap) {
		logger.log("onNotificationPosted");
		if (callbackListener != null) {
			callbackListener.onNotificationPosted(statusBarNotification, rankingMap.getOrderedKeys());
		}
	}

	@Override
	public void onNotificationRankingUpdate(RankingMap rankingMap) {
		logger.log("onNotificationRankingUpdate");
		if (callbackListener != null) {
			callbackListener.onNotificationRankingUpdate(rankingMap.getOrderedKeys());
		}
	}

	public void setCallbackListener(Callback callbackListener) {
		this.callbackListener = callbackListener;
	}

	public interface Callback {
		void onNotificationPosted(StatusBarNotification statusBarNotification, String[] orderedRankingMapKeys);

		void onNotificationRemoved(StatusBarNotification statusBarNotification, String[] orderedRankingMapKeys);

		void onNotificationRankingUpdate(String[] orderedRankingMapKeys);
	}

	/**
	 * {@link Binder} which helps to get the {@link NotificationListener} service.
	 */
	public class HelperBinder extends Binder {

		/**
		 * @return The running {@link NotificationListener} service instance.
		 */
		public NotificationListener getRunningNotificationListenerService() {
			return NotificationListener.this;
		}
	}
}
