package tr.com.togg.displayservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DisplayManager mDisplayManager;
    /*
     * 0 : Control Screen
     * 1 : Main Screen
     * 2 : Cluster
     * */
    private Display[] mDisplays;
    //    private Closeable foregroundActivityEnforcer;

    MySurfaceView surfaceViewContainer1;
    MySurfaceView surfaceViewContainer2;
    MySurfaceView surfaceViewContainer3;

    VirtualDisplay vd1;
    VirtualDisplay vd2;
    VirtualDisplay vd3;

    final String packageName1 = "tr.com.togg.displayservice";
    final String activityNAme1 = "tr.com.togg.displayservice.SecondActivity";

    final String packageName2 = "tr.com.togg.displayservice";
    final String activityNAme2 = "tr.com.togg.displayservice.ThirdActivity";

    final String packageName3 = "tr.com.togg.displayservice";
    final String activityNAme3 = "tr.com.togg.displayservice.FourthActivity";

    final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 1 << 3;
    final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1 << 0;
    final int VIRTUAL_DISPLAY_FLAG_TRUSTED = 1 << 10;
    final int VIRTUAL_DISPLAY_FLAG_SUPPORTS_TOUCH = 1 << 6;
    final int VIRTUAL_DISPLAY_FLAG_DESTROY_CONTENT_ON_REMOVAL = 1 << 8;
    final int virtualDisplayFlags = VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | VIRTUAL_DISPLAY_FLAG_DESTROY_CONTENT_ON_REMOVAL  | VIRTUAL_DISPLAY_FLAG_PUBLIC | VIRTUAL_DISPLAY_FLAG_TRUSTED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button display1Btn = findViewById(R.id.display1Btn);
        Button display2Btn = findViewById(R.id.display2Btn);
        Button display3Btn = findViewById(R.id.display3Btn);

        display1Btn.setOnClickListener(this);
        display2Btn.setOnClickListener(this);
        display3Btn.setOnClickListener(this);

        initAll();
    }

    private void initAll() {
        try {
            mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        } catch (Exception e) {
            Log.e(DisplayManagerConstants.LOGTAG, "MainActivity : initAll : " + e.toString());
        }

        surfaceViewContainer1 = findViewById(R.id.surfaceViewContainer1);
        surfaceViewContainer2 = findViewById(R.id.surfaceViewContainer2);
        surfaceViewContainer3 = findViewById(R.id.surfaceViewContainer3);

        surfaceViewContainer1.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try{
                    surfaceViewContainer1.setZOrderOnTop(true);
                    vd1 = mDisplayManager.createVirtualDisplay("virtualDisplay1",
                            surfaceViewContainer1.getWidth(),
                            surfaceViewContainer1.getHeight(),
                            160,
                            surfaceHolder.getSurface(),
                            virtualDisplayFlags);
                    printDisplay(vd1.getDisplay());
                    surfaceViewContainer1.setDisplayId(vd1.getDisplay().getDisplayId());
                }catch (Exception e){
                    Log.e(DisplayManagerConstants.LOGTAG,e.toString());

                }

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                Log.i(DisplayManagerConstants.LOGTAG, "surfaceDestroyed virtualDisplay1");
                vd1.release();
            }
        });

        surfaceViewContainer2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try{
                    surfaceViewContainer2.setZOrderOnTop(true);
                    vd2 = mDisplayManager.createVirtualDisplay("virtualDisplay2",
                            surfaceViewContainer2.getWidth(),
                            surfaceViewContainer2.getHeight(),
                            160,
                            surfaceHolder.getSurface(),
                            virtualDisplayFlags);
                    printDisplay(vd2.getDisplay());
                    surfaceViewContainer2.setDisplayId(vd2.getDisplay().getDisplayId());
                }catch (Exception e){
                    Log.e(DisplayManagerConstants.LOGTAG,e.toString());

                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                Log.i(DisplayManagerConstants.LOGTAG, "surfaceDestroyed virtualDisplay2");
                vd2.release();
            }
        });

        surfaceViewContainer3.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try{
                    surfaceViewContainer3.setZOrderOnTop(true);
                    vd3 = mDisplayManager.createVirtualDisplay("virtualDisplay3",
                            surfaceViewContainer3.getWidth(),
                            surfaceViewContainer3.getHeight(),
                            160,
                            surfaceHolder.getSurface(),
                            virtualDisplayFlags);

                    printDisplay(vd3.getDisplay());
                    surfaceViewContainer3.setDisplayId(vd3.getDisplay().getDisplayId());
                }catch (Exception e){
                    Log.e(DisplayManagerConstants.LOGTAG,e.toString());

                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                Log.i(DisplayManagerConstants.LOGTAG, "surfaceDestroyed virtualDisplay3");
                vd3.release();
            }
        });


        mDisplays = mDisplayManager.getDisplays();
        for (Display display : mDisplays) {
            printDisplay(display);
        }

    }
    private void printDisplay(Display display) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Display id : ");
            sb.append(display.getDisplayId());
            sb.append("\n");
            sb.append("Name : ");
            sb.append(display.getName());
            sb.append("\n");
            sb.append("flag : ");
            sb.append(display.getFlags());
            sb.append("\n");
            Display.HdrCapabilities mHdrCapabilities = display.getHdrCapabilities();
            if (mHdrCapabilities != null) {
                sb.append("HdrCapabilities DesiredMaxLuminance :");
                sb.append(mHdrCapabilities.getDesiredMaxLuminance());
                sb.append("\n");
                sb.append("HdrCapabilities DesiredMinLuminance : ");
                sb.append(mHdrCapabilities.getDesiredMinLuminance());
                sb.append("\n");
                int[] mSupportedHdrTypes = mHdrCapabilities.getSupportedHdrTypes();
                if (mSupportedHdrTypes.length > 0) {
                    for (int i = 0; i < mSupportedHdrTypes.length; i++) {
                        sb.append("SupportedHdrTypes for ");
                        sb.append(i);
                        sb.append(mSupportedHdrTypes[i]);
                        sb.append("\n");
                    }
                } else {
                    sb.append("SupportedHdrTypes not found ");
                    sb.append("\n");
                }
            } else {
                sb.append("HdrCapabilities not found ");
                sb.append("\n");

            }
            sb.append("is valid : ");
            sb.append(display.isValid());
            sb.append("\n");
            Display.Mode mMode = display.getMode();
            sb.append("mode : id : ");
            sb.append(mMode.getModeId());
            sb.append(" PhysicalHeight : ");
            sb.append(mMode.getPhysicalHeight());
            sb.append(" PhysicalWidth : ");
            sb.append(mMode.getPhysicalWidth());
            sb.append("\n");
            sb.append("Refresh Rate : ");
            sb.append(mMode.getRefreshRate());
            sb.append("\n");
            sb.append("*******************************************************");
            sb.append("\n");
            Log.i(DisplayManagerConstants.LOGTAG, sb.toString());
        } catch (Exception e) {
            Log.e(DisplayManagerConstants.LOGTAG, e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.display1Btn) {
            displayButtonClicked(1, vd1, packageName1, activityNAme1);
        } else if (view.getId() == R.id.display2Btn) {
            displayButtonClicked(2, vd2, packageName2, activityNAme2);
        } else if (view.getId() == R.id.display3Btn) {
            displayButtonClicked(3, vd3, packageName3, activityNAme3);
        }
    }


    private void displayButtonClicked(int i,VirtualDisplay vd, String packageName, String activityNAme) {

        Log.i(DisplayManagerConstants.LOGTAG, "displayButtonClicked i : " + i + " virtualdisplay id : " + vd.getDisplay().getDisplayId());

        Intent in = new Intent(Intent.ACTION_VIEW);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.setComponent(new ComponentName(packageName, activityNAme));
        in.setPackage(packageName);

        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(vd.getDisplay().getDisplayId());
        startActivity(in, options.toBundle());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}