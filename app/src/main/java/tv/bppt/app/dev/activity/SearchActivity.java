package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.fragment.PostListFragment;
import tv.bppt.app.dev.utility.AdsUtilities;

public class SearchActivity extends BaseActivity {

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
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

    }

    private void initView() {
        setContentView(R.layout.activity_search_or_featured_list);
        mFragmentManager = getSupportFragmentManager();
        mPostListFragment = mFragmentManager.findFragmentById(R.id.fragment_container);


        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
    }

    private void initFunctionality() {
        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Bundle args = new Bundle();
                mPostListFragment = new PostListFragment();
                args.putInt(AppConstant.BUNDLE_KEY_CATEGORY_ID, AppConstant.BUNDLE_KEY_SEARCH_POST_ID);
                args.putString(AppConstant.BUNDLE_KEY_SEARCH_TEXT, newText);
                mPostListFragment.setArguments(args);
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mPostListFragment)
                        .commit();

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}