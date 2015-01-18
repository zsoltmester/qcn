package zsoltmester.qcn.desktop;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import zsoltmester.qcn.R;

import static android.app.ActivityManager.TaskDescription;

public class DesktopActivity extends Activity {

	private Bundle savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		initHomeScreen();
		initTaskDescription();
	}

	private void initHomeScreen() {
		setContentView(R.layout.activity_desktop);
		initHomeScreenFragment();
	}

	private void initHomeScreenFragment() {
		if (savedInstanceState != null) {
			// Already initialized.
			return;
		}

		getFragmentManager()
				.beginTransaction()
				.add(R.id.container, getHomeScreenFragment())
				.commit();
	}

	/**
	 * Here you can set which is the current home screen fragment.
	 *
	 * @return The current home screen fragment.
	 */
	private Fragment getHomeScreenFragment() {
		return new ContactFragment();
	}

	private void initTaskDescription() {
		TaskDescription taskDescriptionForTheDesktopApp = getTaskDescriptionForTheDesktopApp();
		setTaskDescription(taskDescriptionForTheDesktopApp);
	}

	/**
	 * Here you can customise the task description for the desktop app.
	 *
	 * @return The customised task description.
	 */
	private TaskDescription getTaskDescriptionForTheDesktopApp() {
		return new TaskDescription(
				getString(R.string.app_name),
				BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
				getResources().getColor(R.color.green_700));
	}
}
