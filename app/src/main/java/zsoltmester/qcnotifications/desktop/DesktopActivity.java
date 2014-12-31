package zsoltmester.qcnotifications.desktop;

import android.app.Activity;
import android.os.Bundle;

import zsoltmester.qcnotifications.R;

public class DesktopActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desktop);

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container, new ContactFragment())
					.commit();
		}
	}
}
