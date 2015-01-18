package zsoltmester.qcn.quickcircle.notifications;

import android.service.notification.StatusBarNotification;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NotificationHelper {

	private static StatusBarNotificationSorter statusBarNotificationSorter;

	/**
	 * This method runs only once, when the {@link NotificationActivity} bind to the notification listener service.  
	 */
	public static void selectDisplayableNotificationsFromAList(List<StatusBarNotification> statusBarNotifications) {
		Iterator i = statusBarNotifications.iterator();
		while (i.hasNext()) {
			if (!isDisplayable((StatusBarNotification) i.next())) {
				i.remove();
			}
		}
	}

	public static boolean isDisplayable(StatusBarNotification statusBarNotification) {
		return !statusBarNotification.isOngoing();
	}

	public static void sortNotificationsBasedOnRankingMap(List<StatusBarNotification> statusBarNotifications, 
			String[] orderedRankingMapKeys) {
		if (statusBarNotificationSorter == null) {
			statusBarNotificationSorter = new StatusBarNotificationSorter();
		}
		statusBarNotificationSorter.setOrderedRankingMapKeys(orderedRankingMapKeys);
		Collections.sort(statusBarNotifications, statusBarNotificationSorter);
	}

	private static int getRank(StatusBarNotification statusBarNotification, String[] orderedRankingMapKeys) {
		int orderedRankingMapKeysLength =  orderedRankingMapKeys.length;
		for (int index = 0; index < orderedRankingMapKeysLength; ++index) {
			if (orderedRankingMapKeys[index].equals(statusBarNotification.getKey())) {
				return index;
			}
		}
		return orderedRankingMapKeysLength;
	}

	public static void insertNotificationBasedOnRankingMap(List<StatusBarNotification> statusBarNotifications, 
			StatusBarNotification statusBarNotificationToInsert, String[] orderedRankingMapKeys) {
		boolean notificationUpdated = updateNotificationIfItPossible(statusBarNotifications, 
				statusBarNotificationToInsert);
		if (!notificationUpdated) {
			int statusBarNotificationToInsertRank = getRank(statusBarNotificationToInsert, orderedRankingMapKeys);
			statusBarNotifications.add(statusBarNotificationToInsertRank, statusBarNotificationToInsert);
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean updateNotificationIfItPossible(List<StatusBarNotification> statusBarNotifications, 
			StatusBarNotification statusBarNotificationNewVersion) {
		ListIterator listIterator = statusBarNotifications.listIterator();
		while (listIterator.hasNext()) {
			StatusBarNotification currentStatusBarNotification = (StatusBarNotification) listIterator.next();
			if (currentStatusBarNotification.getId() == statusBarNotificationNewVersion.getId()) {
				listIterator.set(statusBarNotificationNewVersion);
				// statusBarNotificationToInsert updated the outdated version of that notification.
				return true;
			}
		}
		return false;
	}

	public static void deleteNotification(List<StatusBarNotification> statusBarNotifications, 
			StatusBarNotification statusBarNotificationToDelete) {
		Iterator statusBarNotificationIterator = statusBarNotifications.iterator();
		while (statusBarNotificationIterator.hasNext()) {
			if (((StatusBarNotification) statusBarNotificationIterator.next()).getId() 
					== statusBarNotificationToDelete.getId()) {
				statusBarNotificationIterator.remove();
				return;
			}
		}
	}

	private static class StatusBarNotificationSorter implements Comparator<StatusBarNotification> {

		private String[] orderedRankingMapKeys;

		private void setOrderedRankingMapKeys(String[] orderedRankingMapKeys) {
			this.orderedRankingMapKeys = orderedRankingMapKeys;
		}

		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return getRank(lhs, orderedRankingMapKeys) - getRank(rhs, orderedRankingMapKeys);
		}
	}
}
