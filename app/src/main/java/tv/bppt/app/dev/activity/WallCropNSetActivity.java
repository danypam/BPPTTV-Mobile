package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.utility.AdsUtilities;
import tv.bppt.app.dev.utility.WallSelectDialog;
import com.theartofdev.edmodo.cropper.CropImageView;

public class WallCropNSetActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private CropImageView mCropImageView;
    private FloatingActionButton mFab;
    private String mImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = WallCropNSetActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mImgUrl = intent.getStringExtra(AppConstant.BUNDLE_KEY_URL);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_wall_crop);

        mCropImageView = findViewById(R.id.cropImageView);
        mFab = (FloatingActionButton) findViewById(R.id.fab_set_wall);
    }

    private void initFunctionality() {
        fullScreen();

        getBitmap();

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    private void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap cropped = mCropImageView.getCroppedImage();
                    WallSelectDialog wallSelectDialog = new WallSelectDialog(mActivity, cropped, mActivity);
                    wallSelectDialog.show(getFragmentManager(), "wallselectdialog");

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, getString(R.string.wallpaper_set_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void fullScreen() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        // Immersive mode: Backward compatible to KitKat.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

    public void getBitmap() {
        Glide.with(mContext)
                .asBitmap()
                .load(mImgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mCropImageView.setImageBitmap(resource);
                    }
                });
    }
}