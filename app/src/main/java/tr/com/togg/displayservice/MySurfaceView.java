package tr.com.togg.displayservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.util.Log;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.view.InputEvent;


public class MySurfaceView extends SurfaceView {


    private InputManager mInputManager;

    private int displayId = -1;

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);

        setOnTouchListener(mOnTouchListener);
        mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);

    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    // Map touch events from real displays to virtual displays
    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        float lastX;
        float lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(getDisplayId() == -1 ){
                return false;
            }
            int action = event.getAction();
            lastX = event.getRawX() - v.getLeft();
            lastY = event.getRawY() - v.getTop() - 80;
            Log.i(DisplayManagerConstants.LOGTAG,"lastX : " + lastX + " lastY : " + lastY + " v.getId : " + v.getId() + " v.getLeft : " + v.getLeft() + " v.getTop " + v.getTop());
            // Only displayId 3 is adapted here.
            // If you want to adapt to the other two virtualDisplay, you need to modify the coordinate values.
            float mirrorViewX = lastX;
            float mirrorViewY = lastY;

            MotionEvent motionEvent;
            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    Log.d(DisplayManagerConstants.LOGTAG, "onTouch: send Key Event ACTION_DOWN");
                    motionEvent = mockMotionEvent(mirrorViewX, mirrorViewY, MotionEvent.ACTION_DOWN);
                    callSetDisplayIdViaReflection(motionEvent,displayId);
                    sendKeyEvent(motionEvent);
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(DisplayManagerConstants.LOGTAG, "onTouch: send Key Event ACTION_MOVE");
                    motionEvent = mockMotionEvent(mirrorViewX, mirrorViewY, MotionEvent.ACTION_MOVE);
                    callSetDisplayIdViaReflection(motionEvent,displayId);
                    sendKeyEvent(motionEvent);
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d(DisplayManagerConstants.LOGTAG, "onTouch: send Key Event ACTION_UP");
                    motionEvent = mockMotionEvent(mirrorViewX, mirrorViewY, MotionEvent.ACTION_UP);
                    callSetDisplayIdViaReflection(motionEvent,displayId);
                    sendKeyEvent(motionEvent);
                    break;

                default:
                    break;

            }
            return true;

        }

    };

    private void callSetDisplayIdViaReflection(MotionEvent motionEvent,int displayId) {
        try {
            Log.i(DisplayManagerConstants.LOGTAG,"callSetDisplayIdViaReflection : " + displayId);
            // Get the setDisplayId method from MotionEvent class
            Method setDisplayIdMethod = MotionEvent.class.getMethod("setDisplayId", int.class);

            // Make the method accessible
            setDisplayIdMethod.setAccessible(true);

            // Set the displayId using reflection
            setDisplayIdMethod.invoke(motionEvent, displayId);
        } catch (Exception e) {
            Log.e(DisplayManagerConstants.LOGTAG, "error on callSetDisplayIdViaReflection : " + e.toString());
        }
    }

    private void sendKeyEvent(InputEvent inputEvent) {
        try {


            Method injectEventMethod = mInputManager.getClass().getDeclaredMethod("injectInputEvent",
                    new Class[]{InputEvent.class, Integer.TYPE});
            injectEventMethod.setAccessible(true);
            Field modeField = mInputManager.getClass().getDeclaredField("INJECT_INPUT_EVENT_MODE_ASYNC");
            modeField.setAccessible(true);
            int mode = modeField.getInt(mInputManager.getClass());

            Log.i(DisplayManagerConstants.LOGTAG,"sendKeyEvent : " + displayId + " mode : " + mode);
            injectEventMethod.invoke(mInputManager, inputEvent, mode);

        } catch (Exception ex) {
            Log.e(DisplayManagerConstants.LOGTAG, "sendKeyEvent failed! " + ex.toString());

        }

    }

    private MotionEvent mockMotionEvent(float x, float y, int action) {
        MotionEvent event = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                action, x, y, 0 /* metaState */);
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        return event;

    }

    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }
}