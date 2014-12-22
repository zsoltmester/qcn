package zsoltmester.qcnotifications.notifications;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
		initMargins(holder, position);

		Bundle extras = notifications[position].getNotification().extras;

		boolean isBigPictureStyle = initBigPicture(holder, extras);

		initTitle(holder, extras, isBigPictureStyle);

		initText(holder, extras);

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
		// TODO something wrong with fb notifications (maybe text + inbox)
		holder.text.setVisibility(View.VISIBLE);
		String newLine = System.getProperty("line.separator");

		String text = "";
		List<String> textSss = extractSpannedStrings(extras.getCharSequence(Notification.EXTRA_TEXT));
		for (String textPiece : textSss) {
			text += textPiece;
		}

		String bigText = "";
		List<String> bigTextSss = extractSpannedStrings(extras.getCharSequence(Notification.EXTRA_TEXT));
		for (String textPiece : bigTextSss) {
			bigText += textPiece;
		}

		String inboxText = "";
		CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
		if (lines != null && lines.length > 0) {
			for (CharSequence line : lines) {
				List<String> lineSss = extractSpannedStrings(line);
				for (String textPiece : lineSss) {
					inboxText += textPiece;
				}
			}
		}

		String summaryText = "";
		List<String> summaryTextSss = extractSpannedStrings(extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT));
		for (String textPiece : summaryTextSss) {
			summaryText += textPiece;
		}

		String subText = "";
		List<String> subTextSss = extractSpannedStrings(extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
		for (String textPiece : subTextSss) {
			subText += textPiece;
		}

		String visibleText = "";
		visibleText = appendText(visibleText, newLine, inboxText);

		if (visibleText.isEmpty()) {
			if (!bigText.isEmpty()) {
				visibleText = bigText;
			} else if (!text.isEmpty()) {
				visibleText = text;
			}
		}

		visibleText = appendText(visibleText, newLine, summaryText);
		visibleText = appendText(visibleText, newLine, subText);

		if (!visibleText.isEmpty()) {
			holder.text.setText(visibleText);
		} else {
			holder.text.setVisibility(View.GONE);
		}
	}

	private List<String> extractSpannedStrings(CharSequence charSequence) {
		// This is a google's sample code from:
		// https://gitorious.org/cyandreamproject/android_frameworks_base/source/27ccc880ccde614deba4df9bb97a4ccf2afc359a:core/java/com/android/internal/notification/DemoContactNotificationScorer.java#Lundefined

		if (charSequence == null) {
			return Collections.emptyList();
		}

		if (!(charSequence instanceof SpannableString)) {
			return Arrays.asList(charSequence.toString());
		}

		SpannableString spannableString = (SpannableString) charSequence;

		// get all spans
		Object[] ssArr = spannableString.getSpans(0, spannableString.length(), Object.class);

		// spanned string sequences
		ArrayList<String> sss = new ArrayList<>();
		for (Object spanObj : ssArr) {
			try {
				sss.add(spannableString.subSequence(spannableString.getSpanStart(spanObj),
						spannableString.getSpanEnd(spanObj)).toString());
			} catch (StringIndexOutOfBoundsException e) {
				Log.e(TAG, "Bad indices when extracting spanned subsequence", e);
			}
		}
		return sss;
	}

	private String appendText(String text, String newLine, String stringToAppend) {
		if (stringToAppend != null && !stringToAppend.isEmpty()) {
			return text.isEmpty() ? stringToAppend : text + newLine + stringToAppend;
		} else {
			return text;
		}
	}

	private void initIcon(ViewHolder holder, int position, Bundle extras) {
		holder.icon.setVisibility(View.VISIBLE);
		holder.icon.setBackground(null);

		Integer iconRes = notifications[position].getNotification().icon;
		Object smallIcon = extras.get(Notification.EXTRA_SMALL_ICON);
		Object largeIcon = extras.get(Notification.EXTRA_LARGE_ICON);
		Object bigLargeIcon = extras.get(Notification.EXTRA_LARGE_ICON_BIG);

		if (bigLargeIcon instanceof Bitmap) {
			holder.icon.setImageBitmap((Bitmap) bigLargeIcon);
		} else if (largeIcon instanceof Bitmap) {
			holder.icon.setImageBitmap((Bitmap) largeIcon);
		} else if (smallIcon instanceof Bitmap) {
			holder.icon.setImageBitmap((Bitmap) smallIcon);
		} else {
			try {
				holder.icon.setImageDrawable(holder.card.getContext()
						.createPackageContext(notifications[position].getPackageName(), 0).getResources()
						.getDrawable(iconRes));
				holder.icon.setBackgroundResource(R.drawable.bg_icon);
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
		}
	}
}
