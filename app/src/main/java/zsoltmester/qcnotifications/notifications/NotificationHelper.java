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
	private static Sorter sorter;

	public static void sortNotifications(List<StatusBarNotification> nfs, String[] rm) {
		if (sorter == null) {
			sorter = new Sorter();
		}
		sorter.setRankingMap(rm);

		Collections.sort(nfs, sorter);
	}

	// TODO @SuppressWarnings("")
	public static void selectValidNotifications(List<StatusBarNotification> nfs) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				if (!isDisplayable((StatusBarNotification) i.next())) {
					i.remove();
				}
			}
		}
	}

	public static boolean isDisplayable(StatusBarNotification sbn) {
		// TODO it's enough for check if it is displayable?
		return sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE) != null;
	}

	// TODO @SuppressWarnings("")
	public static void insertNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn, String[] rm) {
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

		sortNotifications(nfs, rm);
	}

	// TODO @SuppressWarnings("")
	public static void deleteNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn, String[] rm) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				if (((StatusBarNotification) i.next()).getId() == sbn.getId()) {
					i.remove();
					break;
				}
			}
		}

		sortNotifications(nfs, rm);
	}

	private static class Sorter implements Comparator<StatusBarNotification> {

		private String[] rm;

		private void setRankingMap(String[] rm) {
			this.rm = rm;
		}

		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return getRank(lhs) - getRank(rhs);
		}

		private int getRank(StatusBarNotification sbn) {
			for (int j = 0; j < rm.length; ++j) {
				if (rm[j].equals(sbn.getKey())) {
					return j;
				}
			}

			// it's bad, if it's really get called, but it won't
			return Integer.MIN_VALUE;
		}
	}
}
