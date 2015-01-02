package zsoltmester.qcn.desktop;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import zsoltmester.qcn.R;

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

		ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(
				getString(R.string.app_name),
				BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_launcher),
				getResources().getColor(R.color.green_700));
		setTaskDescription(td);
	}
}
