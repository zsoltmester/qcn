package zsoltmester.qcnotifications.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NotificationHelper {

	private static final String TAG = NotificationHelper.class.getSimpleName();

	public static void sortNotifications(List<StatusBarNotification> nfs, final String[] rm) {
		Comparator<StatusBarNotification> sorter = new Comparator<StatusBarNotification>() {
			@Override
			public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
				return getRank(rm, lhs) - getRank(rm, rhs);
			}

			private int getRank(String[] rm, StatusBarNotification sbn) {
				for (int j = 0; j < rm.length; ++j ) {
					if (rm[j].equals(sbn.getKey())) {
						return j;
					}
				}

				// it's bad, if it's really get called, but it won't
				return Integer.MIN_VALUE;
			}
		};

		Collections.sort(nfs, sorter);
	}

	public static void selectValidNotifications(List<StatusBarNotification> nfs) {
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

	public static void insertNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn) {
		// TODO check for the same notfication id (then set); DONE: test this
		// TODO insert based on the ranking map
		synchronized (nfs) {
			ListIterator i = nfs.listIterator();
			while (i.hasNext()) {
				// current sbn
				StatusBarNotification csbn = (StatusBarNotification) i.next();
				if (csbn.getId() == sbn.getId()) {
					i.set(sbn);
					return;
				} else if (csbn.getNotification().priority <= sbn.getNotification().priority) {
					break;
				}
			}

			if (i.hasNext()) {
				i.add(sbn);
			} else {
				nfs.add(sbn);
			}
		}
	}

	public static void deleteNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				if (((StatusBarNotification) i.next()).getId() == sbn.getId()) {
					i.remove();
					break;
				}
			}
		}
	}
}
