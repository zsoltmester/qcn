package zsoltmester.qcn.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NotificationHelper {

	private static SorterV21 sorterV21;
	private static Comparator<StatusBarNotification> sorterV19;

	@TargetApi(21)
	public static void sortNotificationsV21(List<StatusBarNotification> nfs, String[] rm) {
		if (sorterV21 == null) {
			sorterV21 = new SorterV21();
		}
		sorterV21.setRankingMap(rm);

		Collections.sort(nfs, sorterV21);
	}

	@TargetApi(19)
	public static void sortNotificationsV19(List<StatusBarNotification> nfs) {
		if (sorterV19 == null) {
			sorterV19 = new SorterV19();
		}

		Collections.sort(nfs, sorterV19);
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
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

	@SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
	public static void insertNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn, String[] rm) {
		synchronized (nfs) {
			ListIterator i = nfs.listIterator();
			while (i.hasNext()) {
				StatusBarNotification csbn = (StatusBarNotification) i.next(); // current sbn
				if (csbn.getId() == sbn.getId()) {
					i.set(sbn);
					return;
				}
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				nfs.add(getRank(sbn, rm), sbn);
				sortNotificationsV21(nfs, rm);
			} else {
				nfs.add(sbn);
				sortNotificationsV19(nfs);
			}
		}
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			sortNotificationsV21(nfs, rm);
		} else {
			sortNotificationsV19(nfs);
		}
	}

	@TargetApi(21)
	private static int getRank(StatusBarNotification sbn, String[] rm) {
		for (int j = 0; j < rm.length; ++j) {
			if (rm[j].equals(sbn.getKey())) {
				return j;
			}
		}

		return rm.length;
	}

	@TargetApi(21)
	private static class SorterV21 implements Comparator<StatusBarNotification> {

		private String[] rm;

		private void setRankingMap(String[] rm) {
			this.rm = rm;
		}

		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return getRank(lhs, rm) - getRank(rhs, rm);
		}
	}

	@TargetApi(19)
	private static class SorterV19 implements Comparator<StatusBarNotification> {

		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return rhs.getNotification().priority - lhs.getNotification().priority;
		}
	}
}
