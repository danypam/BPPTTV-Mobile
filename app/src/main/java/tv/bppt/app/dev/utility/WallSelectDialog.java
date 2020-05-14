package tv.bppt.app.dev.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import tv.bppt.app.dev.R;

import java.io.IOException;

@SuppressLint("ValidFragment")
public class WallSelectDialog extends DialogFragment {
    Context mContext;
    Activity mActivity;
    Bitmap mResource;

    public WallSelectDialog(Context mContext, Bitmap mResource, Activity mActivity) {
        this.mContext = mContext;
        this.mResource = mResource;
        this.mActivity = mActivity;
    }


    private void setOurWall(int which, int sbMessage) {

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                WallpaperManager.getInstance(mContext)
                        .setBitmap(mResource, null, true, which);
                Toast.makeText(mActivity, sbMessage, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setOurWall(int sbMessage) {

        try {
            WallpaperManager.getInstance(mContext)
                    .setBitmap(mResource);
            Toast.makeText(mActivity, sbMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final AlertDialog.Builder setWall = new AlertDialog.Builder(mContext);
        setWall.setTitle(R.string.set_wallpaper)
                .setItems(R.array.set_wallpaper_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0: {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            setOurWall(WallpaperManager.FLAG_SYSTEM, R.string.home_set);
                                        } else {
                                            setOurWall(R.string.wallpaper_set);
                                        }
                                    }
                                });
                                break;
                            }
                            case 1: {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            setOurWall(WallpaperManager.FLAG_LOCK, R.string.lock_screen_set);
                                        } else {
                                            setOurWall(R.string.wallpaper_set);
                                        }
                                    }
                                });
                                break;
                            }
                            case 2: {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            setOurWall(R.string.both_set);
                                        } else {
                                            setOurWall(R.string.wallpaper_set);
                                        }
                                    }
                                });
                                break;
                            }
                        }

                    }
                });

        return setWall.create();
    }
}
