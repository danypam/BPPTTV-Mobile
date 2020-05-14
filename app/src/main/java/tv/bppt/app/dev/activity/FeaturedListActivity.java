package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.fragment.PostListFragment;
import tv.bppt.app.dev.utility.AdsUtilities;

public class FeaturedListActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private Fragment mPostListFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
    }

    private void initVar() {
        mActivity = FeaturedListActivity.this;
        mContext = mActivity.getApplicationContext();

    }

    private void initView() {
        setContentView(R.layout.activity_search_or_featured_list);
        mFragmentManager = getSupportFragmentManager();
        mPostListFragment = mFragmentManager.findFragmentById(R.id.fragment_container);


        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.featured));
    }

    private void initFunctionality() {
        Bundle args = new Bundle();
        mPostListFragment = new PostListFragment();
        args.putInt(AppConstant.BUNDLE_KEY_CATEGORY_ID, AppConstant.BUNDLE_KEY_FEATURED_POST_ID);
        mPostListFragment.setArguments(args);
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mPostListFragment)
                .commit();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }
}