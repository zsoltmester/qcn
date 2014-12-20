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

public class QCNotificationAdapter extends RecyclerView.Adapter<QCNotificationAdapter.ViewHolder> {

	private static final String TAG = QCNotificationAdapter.class.getSimpleName();

	private static final SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d");

	private StatusBarNotification[] notifications;
	private Resources res;

	public QCNotificationAdapter(StatusBarNotification[] notifications, Resources res) {
		this.notifications = notifications;
		this.res = res;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.qc_line, parent, false);
		return new ViewHolder(card);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO split this to some method

		initMargins(holder, position);

		Bundle extras = notifications[position].getNotification().extras;

		boolean isBigPictureStyle = initBigPicture(holder, extras);

		initTitle(holder, extras, isBigPictureStyle);

		initText(holder, extras);

		initButtons(holder, position);

		initIcon(holder, position, extras);

		initDate(holder, position);

		initInfo(holder, position, extras);
	}

	private void initMargins(ViewHolder holder, int position) {
		int topMargin;

		// first element
		if (position == 0) {
			topMargin = res.getDimensionPixelSize(R.dimen.qc_first_card_top_margin);
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
			lp.setMargins(
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					topMargin,
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					res.getDimensionPixelSize(R.dimen.qc_default_margin)
			);
		} else {
			topMargin = 0;
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
			lp.setMargins(
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					topMargin,
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					res.getDimensionPixelSize(R.dimen.qc_default_margin)
			);
		}

		// last element
		if (position == notifications.length - 1) {
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
			lp.setMargins(
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					topMargin,
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					res.getDimensionPixelSize(R.dimen.qc_last_card_bottom_margin)
			);
		} else {
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
			lp.setMargins(
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					topMargin,
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					res.getDimensionPixelSize(R.dimen.qc_default_margin)
			);
		}
	}

	private boolean initBigPicture(ViewHolder holder, Bundle extras) {
		holder.bigPictureFrame.setVisibility(View.VISIBLE);
		holder.bigPicture.setVisibility(View.VISIBLE);

		Bitmap bigPicture = extras.getParcelable(Notification.EXTRA_PICTURE);

		if (bigPicture != null) {
			holder.bigPicture.setImageBitmap(bigPicture);
			return true;
		} else {
			holder.bigPictureFrame.setVisibility(View.GONE);
			holder.bigPicture.setVisibility(View.GONE);
			return false;
		}
	}

	private void initTitle(ViewHolder holder, Bundle extras, boolean isBigPictureStyle) {
		holder.bigTitle.setVisibility(View.VISIBLE);
		holder.title.setVisibility(View.VISIBLE);

		String bigTitle = extras.getString(Notification.EXTRA_TITLE_BIG);
		String title = extras.getString(Notification.EXTRA_TITLE);

		if (isBigPictureStyle) {
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
	}

	private void initText(ViewHolder holder, Bundle extras) {
		// TODO lines don't appear in inbox style notification
		holder.text.setVisibility(View.VISIBLE);
		String newline = System.getProperty("line.separator");

		String text = extras.getString(Notification.EXTRA_TEXT);
		String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);
		CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
		String inboxText = "";
		if (lines != null) {
			for (CharSequence line : lines) {
				if (inboxText.isEmpty()) {
					inboxText += line;
				} else {
					inboxText += newline + line;
				}
			}
		}
		String subText = extras.getString(Notification.EXTRA_SUB_TEXT);

		String visibleText = "";
		visibleText += text != null ? text : "";
		if ((text == null || text.isEmpty()) && bigText != null && !bigText.isEmpty()) {
			visibleText = bigText;
		} else if (visibleText.isEmpty()) {
			visibleText = inboxText;
		}

		visibleText = subText != null ? visibleText.isEmpty() ? subText : visibleText + newline + subText : visibleText;

		if (!visibleText.isEmpty()) {
			holder.text.setText(visibleText);
		} else {
			holder.text.setVisibility(View.GONE);
		}
	}

	private void initButtons(ViewHolder holder, int position) {
		// TODO add +1 button
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
	}

	private void initIcon(ViewHolder holder, int position, Bundle extras) {
		holder.icon.setVisibility(View.VISIBLE);
		holder.icon.setBackground(null);

		Integer iconRes = notifications[position].getNotification().icon;
		Bitmap smallIconBitmap = null;
		Bitmap largeIconBitmap = null;
		Bitmap bigLargeIconBitmap = null;
		try {
			smallIconBitmap = extras.getParcelable(Notification.EXTRA_SMALL_ICON);
			largeIconBitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
			bigLargeIconBitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG);
		} catch (ClassCastException e) {
			// Don't worry, I handle this with iconRes.
		}

		if (bigLargeIconBitmap != null) {
			holder.icon.setImageBitmap(bigLargeIconBitmap);
		} else if (largeIconBitmap != null) {
			holder.icon.setImageBitmap(largeIconBitmap);
		} else if (smallIconBitmap != null) {
			holder.icon.setImageBitmap(smallIconBitmap);
		} else {
			try {
				holder.icon.setImageDrawable(holder.card.getContext()
						.createPackageContext(notifications[position].getPackageName(), 0).getResources()
						.getDrawable(iconRes));
				holder.icon.setBackgroundResource(R.drawable.icon_background);
			} catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
				e.printStackTrace();
				holder.icon.setVisibility(View.GONE);
			}
		}
	}

	private void initDate(ViewHolder holder, int position) {
		Date date = new Date(notifications[position].getPostTime());

		if (DateUtils.isToday(date.getTime())) {
			holder.date.setText(TODAY_FORMAT.format(date));
		} else {
			holder.date.setText(DATE_FORMAT.format(date));
		}
	}

	private void initInfo(ViewHolder holder, int position, Bundle extras) {
		holder.info.setVisibility(View.VISIBLE);
		String infoText = extras.getString(Notification.EXTRA_INFO_TEXT);
		if (infoText != null && !infoText.isEmpty()) {
			holder.info.setText(infoText);
		} else {
			int number = notifications[position].getNotification().number;
			if (number > 0) {
				holder.info.setText(Integer.toString(number));
			} else {
				holder.info.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public int getItemCount() {
		return notifications != null ? notifications.length : 0;
	}

	public void updateNotificationsArray(StatusBarNotification[] notifications) {
		this.notifications = notifications;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private CardView card;
		private View bigPictureFrame;
		private ImageView bigPicture;
		private TextView bigTitle;
		private ImageView icon;
		private TextView date;
		private TextView info;
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
			info = (TextView) card.findViewById(R.id.info);
			title = (TextView) card.findViewById(R.id.title);
			text = (TextView) card.findViewById(R.id.text);
			divider = card.findViewById(R.id.divider);
			buttonsFrame = card.findViewById(R.id.buttons_frame);
			btn1 = (Button) card.findViewById(R.id.btn_1);
			btn2 = (Button) card.findViewById(R.id.btn_2);
		}
	}
}
