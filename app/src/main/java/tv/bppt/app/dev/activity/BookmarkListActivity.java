package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.BookmarkAdapter;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.BookmarkDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.bookmark.BookmarkModel;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;
import tv.bppt.app.dev.utility.DialogUtilities;

import java.util.ArrayList;


public class BookmarkListActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<BookmarkModel> mBookmarkList;
    private BookmarkAdapter mBookmarkAdapter = null;
    private RecyclerView mRecycler;

    private BookmarkDbController mBookmarkDbController;
    private MenuItem mMenuItemDeleteAll;
    private int mAdapterPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = BookmarkListActivity.this;
        mContext = mActivity.getApplicationContext();

        mBookmarkList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_bookmark);

        mRecycler = (RecyclerView) findViewById(R.id.rvBookmark);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBookmarkAdapter = new BookmarkAdapter(mContext, mBookmarkList);
        mRecycler.setAdapter(mBookmarkAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.site_menu_book));
        enableUpButton();
        initLoader();
    }

    private void initFunctionality() {

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void updateUI() {
        showLoader();

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(mContext);
        }
        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());

        mBookmarkAdapter.notifyDataSetChanged();

        hideLoader();

        if (mBookmarkList.size() == 0) {
            showEmptyView();
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(false);
            }
        } else {
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(true);
            }
        }
    }

    public void initListener() {
        // recycler list item click listener
        mBookmarkAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mAdapterPosition = position;
                BookmarkModel model = mBookmarkList.get(position);
                switch (view.getId()) {
                    case R.id.btn_book:
                        FragmentManager manager = getSupportFragmentManager();
                        DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_book), getString(R.string.delete_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_EACH_BOOKMARK);
                        dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                        break;
                    case R.id.btn_share:
                        final String appPackageName = mActivity.getPackageName();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(model.getPostUrl())
                                + AppConstant.EMPTY_STRING
                                + mActivity.getResources().getString(R.string.share_text)
                                + " https://play.google.com/store/apps/details?id=" + appPackageName);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        break;
                    case R.id.lyt_container:
                        ActivityUtilities.getInstance().invokePostDetailsActivity(mActivity, PostDetailsActivity.class, model.getPostId(), false);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menus_delete_all:
                FragmentManager manager = getSupportFragmentManager();
                DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_book), getString(R.string.delete_all_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_ALL_BOOKMARK);
                dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_all, menu);
        mMenuItemDeleteAll = menu.findItem(R.id.menus_delete_all);

        updateUI();

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBookmarkList.size() > 0) {
            updateUI();
        }
        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_ALL_BOOKMARK)) {
                mBookmarkDbController.deleteAllFav();
                updateUI();
            } else if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_EACH_BOOKMARK)) {
                mBookmarkDbController.deleteEachFav(mBookmarkList.get(mAdapterPosition).getPostId());
                updateUI();
            }
        }
    }
}
