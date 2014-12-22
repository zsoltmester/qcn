package zsoltmester.qcnotifications.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class NotificationHelper {

	private static final String TAG = NotificationHelper.class.getSimpleName();

	private static Comparator<StatusBarNotification> priorityComparator = new Comparator<StatusBarNotification>() {
		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return rhs.getNotification().priority - lhs.getNotification().priority;
		}
	};

	public static void sortNotificationsByPriority(List<StatusBarNotification> nfs) {
		Collections.sort(nfs, priorityComparator);
	}

	public static void selectNotNullNotifications(List<StatusBarNotification> nfs) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				// TODO it's enough for check if it is valid?
				if (((StatusBarNotification) i.next()).getNotification().extras
						.getCharSequence(Notification.EXTRA_TITLE) == null) {
					i.remove();
				}
			}
		}
	}
}
