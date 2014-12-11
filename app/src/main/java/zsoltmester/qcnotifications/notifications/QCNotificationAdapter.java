package zsoltmester.qcnotifications.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zsoltmester.qcnotifications.R;

public final class QCNotificationAdapter extends RecyclerView.Adapter<QCNotificationAdapter.ViewHolder> {

	private String[] packageNames;

	public QCNotificationAdapter(final String[] packageNames) {
		this.packageNames = packageNames;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qc_line, parent, false);
		return new ViewHolder((TextView) view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.testView.setText(packageNames[position]);
	}

	@Override
	public int getItemCount() {
		return packageNames != null ? packageNames.length : 0;
	}

	public void updatePackageNames(String[] packageNames) {
		this.packageNames = packageNames;
	}

	public String[] getPackageNames() {
		return packageNames;
	}

	final class ViewHolder extends RecyclerView.ViewHolder {
		private TextView testView;
		private ViewHolder(TextView testView) {
			super(testView);
			this.testView = testView;
		}
	}
}
