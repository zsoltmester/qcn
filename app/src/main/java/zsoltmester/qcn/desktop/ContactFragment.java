package zsoltmester.qcn.desktop;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import zsoltmester.qcn.R;

public class ContactFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact, container, false);

		Button email = (Button) view.findViewById(R.id.email);
		Button github = (Button) view.findViewById(R.id.github);

		email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendEmail();
			}
		});
		github.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openGitHub();
			}
		});

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			email.setBackground(getResources().getDrawable(R.drawable.bg_dt_btn));
			github.setBackground(getResources().getDrawable(R.drawable.bg_dt_btn));
		}

		return view;
	}

	private void sendEmail() {
		Intent i =
				new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.contact_my_email), null));
		i.putExtra(Intent.EXTRA_SUBJECT, '[' + getString(R.string.app_name) + ']');
		startActivity(i);
	}

	private void openGitHub() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_source_link))));
	}

}
