package zsoltmester.qcnotifications.notifications;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import zsoltmester.qcnotifications.R;

public final class QCNotificationAdapter extends RecyclerView.Adapter<QCNotificationAdapter.ViewHolder> {

	private static final SimpleDateFormat todayFormat = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");

	// TODO have to sort this
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
		Bundle extras = notifications[position].getNotification().extras;

		// big picture
		holder.bigPictureFrame.setVisibility(View.VISIBLE);
		holder.bigPicture.setVisibility(View.VISIBLE);

		Bitmap bigPicture = extras.getParcelable(Notification.EXTRA_PICTURE);

		if (bigPicture != null) {
			holder.bigPicture.setImageBitmap(bigPicture);
		} else {
			holder.bigPictureFrame.setVisibility(View.GONE);
			holder.bigPicture.setVisibility(View.GONE);
		}

		// title and big title
		holder.bigTitle.setVisibility(View.VISIBLE);
		holder.title.setVisibility(View.VISIBLE);

		String bigTitle = extras.getString(Notification.EXTRA_TITLE_BIG);
		String title = extras.getString(Notification.EXTRA_TITLE);

		if (bigPicture != null) {
			if (bigTitle != null) {
				holder.bigTitle.setText(bigTitle);
			} else if (title != null) {
				holder.bigTitle.setText(title);
			} else {
				holder.bigTitle.setVisibility(View.GONE);
			}
			holder.title.setVisibility(View.GONE);
		} else {
			holder.bigTitle.setVisibility(View.GONE);
			if (bigTitle != null) {
				holder.title.setText(bigTitle);
			} else if (title != null) {
				holder.title.setText(title);
			} else {
				holder.title.setVisibility(View.GONE);
			}
		}

		// text
		// TODO lines doesn't appear in gmail notification
		holder.text.setVisibility(View.VISIBLE);
		String newline = System.getProperty("line.separator");

		String text = extras.getString(Notification.EXTRA_TEXT);
		String infoText = extras.getString(Notification.EXTRA_INFO_TEXT);
		String summaryText = extras.getString(Notification.EXTRA_SUMMARY_TEXT);
		CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

		String visibleText = "";
		visibleText += text != null ? text + newline : "";
		if (lines != null) {
			for (CharSequence lineCharSequence : lines) {
				String line = lineCharSequence.toString();
				visibleText += line + newline;
			}
		}
		visibleText += infoText != null ? infoText + newline : "";
		visibleText += summaryText != null ? summaryText : "";
		if (!visibleText.isEmpty()) {
			holder.text.setText(visibleText);
		} else {
			holder.text.setVisibility(View.GONE);
		}

		// buttons
		holder.buttonsFrame.setVisibility(View.GONE);
		holder.btn1.setVisibility(View.GONE);
		holder.btn2.setVisibility(View.GONE);
		holder.divider.setVisibility(View.GONE);

		final Notification.Action[] actions = notifications[position].getNotification().actions;

		if (actions != null && actions.length > 0) {
			switch (actions.length) {
				case 2:
					holder.btn2.setVisibility(View.VISIBLE);
					holder.btn2.setText(actions[1].title);
					// TODO perform action
				case 1:
					holder.buttonsFrame.setVisibility(View.VISIBLE);
					holder.divider.setVisibility(View.VISIBLE);
					holder.btn1.setVisibility(View.VISIBLE);
					holder.btn1.setText(actions[0].title);
					// TODO perform action
					break;
			}
		}

		// icon
		// TODO background bug sometimes
		holder.icon.setVisibility(View.VISIBLE);

		Integer iconRes = notifications[position].getNotification().icon;
		Bitmap smallIconBitmap = null;
		Bitmap largeIconBitmap = null;
		try {
			smallIconBitmap = extras.getParcelable(Notification.EXTRA_SMALL_ICON);
			largeIconBitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
		} catch (ClassCastException e) {
			// Don't worry, I handle this with iconRes.
		}

		if (largeIconBitmap != null) {
			holder.icon.setImageBitmap(largeIconBitmap);
		} else if (smallIconBitmap != null) {
			holder.icon.setImageBitmap(smallIconBitmap);
		} else {
			try {
				holder.icon.setImageDrawable(holder.card.getContext()
						.createPackageContext(notifications[position].getPackageName(), 0).getResources()
						.getDrawable(iconRes));
			} catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
				e.printStackTrace();
				holder.icon.setVisibility(View.GONE);
			}
		}

		// date
		Date date = new Date(notifications[position].getPostTime());

		if (DateUtils.isToday(date.getTime())) {
			holder.date.setText(todayFormat.format(date));
		} else {
			holder.date.setText(dateFormat.format(date));
		}
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
		private View bigPictureFrame;
		private ImageView bigPicture;
		private TextView bigTitle;
		private ImageView icon;
		private TextView date;
		private TextView title;
		private TextView text;
		private View divider;
		private View buttonsFrame;
		private Button btn1;
		private Button btn2;

		private ViewHolder(CardView card) {
			super(card);
			this.card = card;
			bigPictureFrame = card.findViewById(R.id.big_picture_frame);
			bigPicture = (ImageView) card.findViewById(R.id.big_picture);
			bigTitle = (TextView) card.findViewById(R.id.big_title);
			icon = (ImageView) card.findViewById(R.id.icon);
			date = (TextView) card.findViewById(R.id.date);
			title = (TextView) card.findViewById(R.id.title);
			text = (TextView) card.findViewById(R.id.text);
			divider = card.findViewById(R.id.divider);
			buttonsFrame = card.findViewById(R.id.buttons_frame);
			btn1 = (Button) card.findViewById(R.id.btn_1);
			btn2 = (Button) card.findViewById(R.id.btn_2);
		}
	}
}
