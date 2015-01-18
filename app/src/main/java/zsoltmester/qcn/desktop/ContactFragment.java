package zsoltmester.qcn.desktop;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zsoltmester.qcn.R;

import static android.view.View.OnClickListener;

public class ContactFragment extends Fragment {

	private View fragmentLayout;
	private View buttonToChanges;
	private View buttonToRate;
	private View buttonToEmail;
	private View buttonToSourceCode;
	private OnClickListenerForButtons onClickListenerForButtons = new OnClickListenerForButtons();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentLayout = inflater.inflate(R.layout.fragment_contact, container, false);
		initButtons();
		return fragmentLayout;
	}

	private void initButtons() {
		findButtonsOnLayout();
		initButtonsOnClickListener();
	}

	private void findButtonsOnLayout() {
		buttonToChanges = fragmentLayout.findViewById(R.id.changes);
		buttonToRate = fragmentLayout.findViewById(R.id.rate);
		buttonToEmail = fragmentLayout.findViewById(R.id.email);
		buttonToSourceCode = fragmentLayout.findViewById(R.id.github);
	}

	private void initButtonsOnClickListener() {
		buttonToChanges.setOnClickListener(onClickListenerForButtons);
		buttonToRate.setOnClickListener(onClickListenerForButtons);
		buttonToEmail.setOnClickListener(onClickListenerForButtons);
		buttonToSourceCode.setOnClickListener(onClickListenerForButtons);
	}

	private class OnClickListenerForButtons implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.changes:
					openChangeLog();
					break;
				case R.id.rate:
					openOnPlayStore();
					break;
				case R.id.email:
					openEmailApp();
					break;
				case R.id.github:
					openSourceCode();
					break;
			}
		}

		private void openChangeLog() {
			String changesLink = getString(R.string.contact_changes_link);
			Intent intentToOpenChangeLog = new Intent(Intent.ACTION_VIEW, Uri.parse(changesLink));
			startActivity(intentToOpenChangeLog);
		}

		private void openOnPlayStore() {
			String playStoreLink = getString(R.string.contact_rate_link);
			Intent intentToOpenOnPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
			startActivity(intentToOpenOnPlayStore);
		}

		private void openEmailApp() {
			String emailScheme = "mailto";
			String developerEmail = getString(R.string.contact_my_email);
			String emailSubject = '[' + getString(R.string.app_name) + ']';
			Intent intentToSendEmail =
					new Intent(Intent.ACTION_SENDTO, Uri.fromParts(emailScheme, developerEmail, null));
			intentToSendEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
			startActivity(intentToSendEmail);
		}

		private void openSourceCode() {
			String sourceCodeLink = getString(R.string.contact_source_link);
			Intent intentToOpenSourceCode = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceCodeLink));
			startActivity(intentToOpenSourceCode);
		}
	}
}
