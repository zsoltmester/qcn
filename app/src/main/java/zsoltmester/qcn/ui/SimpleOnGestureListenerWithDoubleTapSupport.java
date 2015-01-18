package zsoltmester.qcn.ui;

import android.view.MotionEvent;

import zsoltmester.qcn.tools.Logger;

import static android.view.GestureDetector.SimpleOnGestureListener;

public class SimpleOnGestureListenerWithDoubleTapSupport extends SimpleOnGestureListener {

	private Logger logger = Logger.createWithLogTag(SimpleOnGestureListenerWithDoubleTapSupport.class.getSimpleName());
	private OnDoubleTapListener onDoubleTapListener;

	private SimpleOnGestureListenerWithDoubleTapSupport(OnDoubleTapListener onDoubleTapListener) {
		this.onDoubleTapListener = onDoubleTapListener;
	}
	
	public static SimpleOnGestureListener createWithOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
		return new SimpleOnGestureListenerWithDoubleTapSupport(onDoubleTapListener);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		logger.log("onDoubleTap");
		onDoubleTapListener.onDoubleTap();
		return true;
	}

	public interface OnDoubleTapListener {
		public void onDoubleTap();
	}
}