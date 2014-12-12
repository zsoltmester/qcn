package zsoltmester.qcnotifications.notifications;

import android.service.notification.StatusBarNotification;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import zsoltmester.qcnotifications.R;

public final class QCNotificationAdapter extends RecyclerView.Adapter<QCNotificationAdapter.ViewHolder> {

	private StatusBarNotification[] notifications;

	public QCNotificationAdapter(final StatusBarNotification[] notifications) {
		this.notifications = notifications;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.qc_line, parent, false);
		return new ViewHolder(card);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.testView.setText(notifications[position].getPackageName());
	}

	@Override
	public int getItemCount() {
		return notifications != null ? notifications.length : 0;
	}

	public void updateNotificationsArray(StatusBarNotification[] notifications) {
		this.notifications = notifications;
	}

	public StatusBarNotification[] getNotifications() {
		return notifications;
	}

	final class ViewHolder extends RecyclerView.ViewHolder {
		private CardView card;
		private TextView testView;

		private ViewHolder(CardView card) {
			super(card);
			this.card = card;
			testView = (TextView) card.findViewById(R.id.test_view);
		}
	}
}
