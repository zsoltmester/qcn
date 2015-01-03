package zsoltmester.qcn.notifications;

import android.app.Notification;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import zsoltmester.qcn.R;

public class QCNotificationAdapter extends RecyclerView.Adapter<QCNotificationAdapter.ViewHolder> {

	private static final String TAG = QCNotificationAdapter.class.getSimpleName();

	private static final SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d");

	private final List<StatusBarNotification> nfs;
	private Resources res;

	public QCNotificationAdapter(List<StatusBarNotification> nfs, Resources res) {
		this.nfs = nfs;
		this.res = res;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.qc_line, parent, false);
		return new ViewHolder(card);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		synchronized (nfs) {
			Bundle extras = nfs.get(position).getNotification().extras;
			String newLine = System.getProperty("line.separator");

			initMargins(holder, position);

			initBigPicture(holder, extras);

			initIcon(holder, position, extras);

			initDate(holder, position);

			initTitle(holder, extras, newLine);

			initText(holder, extras, newLine);

			initSub(holder, position, extras, newLine);
		}
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
					res.getDimensionPixelSize(R.dimen.qc_medium_margin)
			);
		} else {
			topMargin = 0;
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
			lp.setMargins(
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					topMargin,
					res.getDimensionPixelSize(R.dimen.qc_card_margins),
					res.getDimensionPixelSize(R.dimen.qc_medium_margin)
			);
		}

		// last element
		if (position == nfs.size() - 1) {
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
					res.getDimensionPixelSize(R.dimen.qc_medium_margin)
			);
		}
	}

	private void initBigPicture(ViewHolder holder, Bundle extras) {
		holder.bigPicture.setVisibility(View.VISIBLE);

		Bitmap bigPicture = extras.getParcelable(Notification.EXTRA_PICTURE);

		if (bigPicture != null) {
			holder.bigPicture.setImageBitmap(bigPicture);
		} else {
			holder.bigPicture.setVisibility(View.GONE);
		}
	}

	private void initIcon(ViewHolder holder, int position, Bundle extras) {
		holder.icon.setVisibility(View.VISIBLE);
		holder.icon.setBackground(null);

		Integer iconRes = nfs.get(position).getNotification().icon;
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
						.createPackageContext(nfs.get(position).getPackageName(), 0).getResources()
						.getDrawable(iconRes));
				holder.icon.setBackgroundResource(R.drawable.bg_icon);
			} catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
				e.printStackTrace();
				holder.icon.setVisibility(View.GONE);
			}
		}
	}

	private void initDate(ViewHolder holder, int position) {
		Date date = new Date(nfs.get(position).getPostTime());

		if (DateUtils.isToday(date.getTime())) {
			holder.date.setText(TODAY_FORMAT.format(date));
		} else {
			holder.date.setText(DATE_FORMAT.format(date));
		}
	}

	private void initTitle(ViewHolder holder, Bundle extras, String newLine) {
		holder.title.setVisibility(View.VISIBLE);

		StringBuilder sb = new StringBuilder();

		appendText(sb, newLine, extras, Notification.EXTRA_TITLE_BIG);

		if (sb.length() == 0) {
			appendText(sb, newLine, extras, Notification.EXTRA_TITLE);
		}

		if (sb.length() > 0) {
			holder.title.setText(sb.toString());
		} else {
			holder.title.setVisibility(View.GONE);
		}
	}

	private void initText(ViewHolder holder, Bundle extras, String newLine) {
		holder.text.setVisibility(View.VISIBLE);

		StringBuilder sb = new StringBuilder();

		CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
		if (lines != null && lines.length > 0) {
			for (CharSequence line : lines) {
				List<String> lineSss = extractSpannedStrings(line);
				for (String textPiece : lineSss) {
					if (sb.length() > 0) {
						sb.append(newLine).append(textPiece);
					} else {
						sb.append(textPiece);
					}
				}
			}
		}

		if (sb.length() == 0) {
			appendText(sb, newLine, extras, Notification.EXTRA_BIG_TEXT);

			if (sb.length() == 0) {
				appendText(sb, newLine, extras, Notification.EXTRA_TEXT);
			}
		}

		appendText(sb, newLine, extras, Notification.EXTRA_SUMMARY_TEXT);

		if (sb.length() > 0) {
			holder.text.setText(sb.toString());
		} else {
			holder.text.setVisibility(View.GONE);
		}
	}

	private void initSub(ViewHolder holder, int position, Bundle extras, String newLine) {
		// TODO check design with real notifications, then redesign
		holder.sub.setVisibility(View.VISIBLE);

		StringBuilder sb = new StringBuilder();

		appendText(sb, newLine, extras, Notification.EXTRA_SUB_TEXT);
		appendText(sb, newLine, extras, Notification.EXTRA_INFO_TEXT);

		int number = nfs.get(position).getNotification().number;
		if (number > 0) {
			if (sb.length() > 0) {
				sb.append(newLine);
			}
			sb.append(number);
		}

		if (sb.length() > 0) {
			holder.sub.setText(sb.toString());
		} else {
			holder.sub.setVisibility(View.GONE);
		}
	}

	private void appendText(StringBuilder sb, String newLine, Bundle extras, String res) {
		List<String> sss = extractSpannedStrings(extras.getCharSequence(res));
		for (String textPiece : sss) {
			if (sb.length() > 0) {
				sb.append(newLine).append(textPiece);
			} else {
				sb.append(textPiece);
			}
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

	@Override
	public int getItemCount() {
		return nfs != null ? nfs.size() : 0;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private CardView card;
		private ImageView bigPicture;
		private ImageView icon;
		private TextView date;
		private TextView title;
		private TextView text;
		private TextView sub;

		private ViewHolder(CardView card) {
			super(card);
			this.card = card;
			bigPicture = (ImageView) card.findViewById(R.id.big_picture);
			icon = (ImageView) card.findViewById(R.id.icon);
			date = (TextView) card.findViewById(R.id.date);
			title = (TextView) card.findViewById(R.id.title);
			text = (TextView) card.findViewById(R.id.text);
			sub = (TextView) card.findViewById(R.id.sub);

			sub.setTypeface(sub.getTypeface(), Typeface.ITALIC);
		}
	}
}