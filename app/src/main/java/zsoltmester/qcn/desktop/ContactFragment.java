package zsoltmester.qcn.desktop;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zsoltmester.qcn.R;

public class ContactFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact, container, false);
		configureButtons(view);
		return view;
	}

	private void configureButtons(View view) {
		view.findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendEmail();
			}
		});
		view.findViewById(R.id.github).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openGitHub();
			}
		});
		view.findViewById(R.id.changes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openChanges();
			}
		});
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

	private void openChanges() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_changes_link))));
	}

}
