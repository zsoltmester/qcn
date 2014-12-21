package zsoltmester.qcnotifications.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotificationHelper {

	private static final String TAG = NotificationHelper.class.getSimpleName();

	private static Comparator<StatusBarNotification> priorityComparator = new Comparator<StatusBarNotification>() {
		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return rhs.getNotification().priority - lhs.getNotification().priority;
		}
	};

	public static StatusBarNotification[] sortNotificationsByPriority(StatusBarNotification[] notifications) {
		Arrays.sort(notifications, priorityComparator);
		return notifications;
	}

	public static StatusBarNotification[] selectNotNullNotifications(StatusBarNotification[] notifications) {
		List<StatusBarNotification> selectedNotifications = new ArrayList<>();
		for (StatusBarNotification notification : notifications) {
			String title = notification.getNotification().extras.getString(Notification.EXTRA_TITLE);
			if (title != null && !title.isEmpty()) {
				selectedNotifications.add(notification);
			}
		}

		return selectedNotifications.toArray(new StatusBarNotification[selectedNotifications.size()]);
	}
}
