package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;

public class WallPreviewActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private PhotoView mFullImage;
    private ImageButton mBtnExit, mBtnSetWall;
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
        mActivity = WallPreviewActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mImgUrl = intent.getStringExtra(AppConstant.BUNDLE_KEY_URL);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_wall_preview);

        mFullImage = findViewById(R.id.full_image);
        mBtnExit = findViewById(R.id.btn_exit);
        mBtnSetWall = findViewById(R.id.btn_set_image);
    }

    private void initFunctionality() {
        fullScreen();
        if (mImgUrl != null && !mImgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(mImgUrl)
                    .into(mFullImage);
        }

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    private void initListener() {
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();
                finish();
            }
        });

        mBtnSetWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, WallCropNSetActivity.class, mImgUrl, true);
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
}