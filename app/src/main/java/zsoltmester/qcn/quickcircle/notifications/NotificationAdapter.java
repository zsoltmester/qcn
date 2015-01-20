package zsoltmester.qcn.quickcircle.notifications;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zsoltmester.qcn.R;

import static android.support.v7.widget.RecyclerView.LayoutParams;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

	private final List<StatusBarNotification> statusBarNotifications;
	private Resources resources;
	private ViewHolder currentViewHolder;
	private int currentPosition;
	private Notification currentNotification;
	private Bundle currentExtras;
	private String newLine = System.getProperty("line.separator");
	private static final SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat REGULAR_DATE_FORMAT = new SimpleDateFormat("MMM d");

	private NotificationAdapter(List<StatusBarNotification> statusBarNotifications, Resources resources) {
		this.statusBarNotifications = statusBarNotifications;
		this.resources = resources;
	}

	public static NotificationAdapter createFromNotificationsAndResources(
			List<StatusBarNotification> statusBarNotifications, Resources resources) {
		return new NotificationAdapter(statusBarNotifications, resources);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
		return new ViewHolder(card);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		synchronized (statusBarNotifications) {
			currentViewHolder = holder;
			currentPosition = position;
			currentNotification = statusBarNotifications.get(position).getNotification();
			currentExtras = currentNotification.extras;
			initTheCard();
		}
	}

	private void initTheCard() {
		initMargins();
		initBigPicture();
		initIcon();
		initDate();
		initTitle();
		initText();
		initSubText();
	}

	private void initMargins() {
		int topMargin = currentPosition == 0 ? resources.getDimensionPixelSize(R.dimen.qc_first_card_top_margin) : 0;
		int bottomMargin = currentPosition == statusBarNotifications.size() - 1
				? resources.getDimensionPixelSize(R.dimen.qc_last_card_bottom_margin)
				: resources.getDimensionPixelSize(R.dimen.qc_medium_margin);
		LayoutParams cardViewLayoutParams = (LayoutParams) currentViewHolder.cardView.getLayoutParams();
		cardViewLayoutParams.setMargins(resources.getDimensionPixelSize(R.dimen.qc_card_margins), topMargin,
				resources.getDimensionPixelSize(R.dimen.qc_card_margins), bottomMargin);
	}

	private void initBigPicture() {
		Bitmap bigPicture = currentExtras.getParcelable(Notification.EXTRA_PICTURE);
		if (bigPicture != null) {
			currentViewHolder.bigPictureView.setVisibility(View.VISIBLE);
			currentViewHolder.bigPictureView.setImageBitmap(bigPicture);
		} else {
			currentViewHolder.bigPictureView.setVisibility(View.GONE);
		}
	}

	private void initIcon() {
		currentViewHolder.iconView.setVisibility(View.VISIBLE);
		int iconResourceId = currentNotification.icon;
		Object smallIcon = currentExtras.get(Notification.EXTRA_SMALL_ICON);
		Object largeIcon = currentExtras.get(Notification.EXTRA_LARGE_ICON);
		Object bigLargeIcon = currentExtras.get(Notification.EXTRA_LARGE_ICON_BIG);
		if (bigLargeIcon instanceof Bitmap) {
			currentViewHolder.iconView.setBackground(null);
			currentViewHolder.iconView.setImageBitmap((Bitmap) bigLargeIcon);
		} else if (largeIcon instanceof Bitmap) {
			currentViewHolder.iconView.setBackground(null);
			currentViewHolder.iconView.setImageBitmap((Bitmap) largeIcon);
		} else if (smallIcon instanceof Bitmap) {
			currentViewHolder.iconView.setBackground(null);
			currentViewHolder.iconView.setImageBitmap((Bitmap) smallIcon);
		} else {
			try {
				initIconFromResource(iconResourceId);
			} catch (Resources.NotFoundException | PackageManager.NameNotFoundException e) {
				currentViewHolder.iconView.setVisibility(View.GONE);
				e.printStackTrace();
			}
		}
	}

	private void initIconFromResource(int iconResourceId)
			throws Resources.NotFoundException, PackageManager.NameNotFoundException {
		Drawable iconFromResource = currentViewHolder.cardView.getContext()
				.createPackageContext(statusBarNotifications.get(currentPosition).getPackageName(), 0)
				.getResources().getDrawable(iconResourceId);
		currentViewHolder.iconView.setImageDrawable(iconFromResource);
		GradientDrawable background = (GradientDrawable) resources.getDrawable(R.drawable.bg_icon);
		int backgroundColor = currentNotification.color;
		if (backgroundColor != Notification.COLOR_DEFAULT) {
			background.setColor(backgroundColor);
		} else {
			background.setColor(resources.getColor(R.color.iconBg));
		}
		currentViewHolder.iconView.setBackground(background);
	}

	private void initDate() {
		long notificationWhenParameter = currentNotification.when;
		if (notificationWhenParameter > 0) {
			currentViewHolder.dateView.setVisibility(View.VISIBLE);
			formatGivenDateAndInit(new Date(notificationWhenParameter));
		} else {
			currentViewHolder.dateView.setVisibility(View.GONE);
		}
	}

	private void formatGivenDateAndInit(Date date) {
		if (DateUtils.isToday(date.getTime())) {
			currentViewHolder.dateView.setText(TODAY_FORMAT.format(date));
		} else {
			currentViewHolder.dateView.setText(REGULAR_DATE_FORMAT.format(date));
		}
	}

	private void initTitle() {
		StringBuilder titleBuilder = new StringBuilder();
		appendTextToABuilderFromAResource(titleBuilder, Notification.EXTRA_TITLE_BIG);
		if (titleBuilder.length() == 0) {
			appendTextToABuilderFromAResource(titleBuilder, Notification.EXTRA_TITLE);
		}
		if (titleBuilder.length() > 0) {
			currentViewHolder.titleView.setVisibility(View.VISIBLE);
			currentViewHolder.titleView.setText(titleBuilder.toString());
		} else {
			currentViewHolder.titleView.setVisibility(View.GONE);
		}
	}

	private void appendTextToABuilderFromAResource(StringBuilder builder, String resourceId) {
		CharSequence text = currentExtras.getCharSequence(resourceId);
		if (text == null || text.length() == 0) {
			return;
		}
		if (builder.length() > 0) {
			builder.append(newLine).append(text);
		} else {
			builder.append(text);
		}
	}

	private void initText() {
		StringBuilder textBuilder = new StringBuilder();
		appendInboxStyleTextToABuilder(textBuilder);
		if (textBuilder.length() == 0) {
			appendTextToABuilderFromAResource(textBuilder, Notification.EXTRA_BIG_TEXT);
			if (textBuilder.length() == 0) {
				appendTextToABuilderFromAResource(textBuilder, Notification.EXTRA_TEXT);
			}
		}
		appendTextToABuilderFromAResource(textBuilder, Notification.EXTRA_SUMMARY_TEXT);
		if (textBuilder.length() > 0) {
			currentViewHolder.textView.setVisibility(View.VISIBLE);
			currentViewHolder.textView.setText(textBuilder.toString());
		} else {
			currentViewHolder.textView.setVisibility(View.GONE);
		}
	}

	private void appendInboxStyleTextToABuilder(StringBuilder builder) {
		CharSequence[] lines = currentExtras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
		if (lines == null || lines.length == 0) {
			return;
		}
		for (CharSequence line : lines) {
			if (builder.length() > 0) {
				builder.append(newLine).append(line);
			} else {
				builder.append(line);
			}
		}
	}

	private void initSubText() {
		StringBuilder subTextBuilder = new StringBuilder();
		appendTextToABuilderFromAResource(subTextBuilder, Notification.EXTRA_SUB_TEXT);
		appendTextToABuilderFromAResource(subTextBuilder, Notification.EXTRA_INFO_TEXT);
		appendNumberToABuilder(subTextBuilder);
		if (subTextBuilder.length() > 0) {
			currentViewHolder.subTextView.setVisibility(View.VISIBLE);
			currentViewHolder.subTextView.setText(subTextBuilder.toString());
		} else {
			currentViewHolder.subTextView.setVisibility(View.GONE);
		}
	}

	private void appendNumberToABuilder(StringBuilder builder) {
		int number = currentNotification.number;
		if (number < 2) {
			return;
		}
		if (builder.length() > 0) {
			builder.append(newLine);
		}
		builder.append(number);
	}

	@Override
	public int getItemCount() {
		return statusBarNotifications != null ? statusBarNotifications.size() : 0;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private CardView cardView;
		private ImageView bigPictureView;
		private ImageView iconView;
		private TextView dateView;
		private TextView titleView;
		private TextView textView;
		private TextView subTextView;

		private ViewHolder(CardView cardView) {
			super(cardView);
			this.cardView = cardView;
			findViews();
			initSubTextView();
		}

		private void findViews() {
			bigPictureView = (ImageView) cardView.findViewById(R.id.big_picture);
			iconView = (ImageView) cardView.findViewById(R.id.icon);
			dateView = (TextView) cardView.findViewById(R.id.date);
			titleView = (TextView) cardView.findViewById(R.id.title);
			textView = (TextView) cardView.findViewById(R.id.text);
			subTextView = (TextView) cardView.findViewById(R.id.sub);
		}

		private void initSubTextView() {
			subTextView.setTypeface(subTextView.getTypeface(), Typeface.ITALIC);
		}
	}
}
