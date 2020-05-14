package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.PostAdapter;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.BookmarkDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.bookmark.BookmarkModel;
import tv.bppt.app.dev.models.post.Post;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentListActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private List<Post> mRecentList;
    private RecyclerView mRvRecentPosts;
    private PostAdapter mRecentAdapter = null;

    private RelativeLayout mBottomLayout;
    private StaggeredGridLayoutManager mLayoutManager;
    private boolean mUserScrolled = true;
    private int mPageNo = 1, mPastVisibleItems, mVisibleItemCount, mTotalItemCount;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
        implementScrollListener();
    }

    private void initVar() {
        mActivity = RecentListActivity.this;
        mContext = mActivity.getApplicationContext();

        mRecentList = new ArrayList<>();
        mBookmarkList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_recent_post_list);

        mRvRecentPosts = (RecyclerView) findViewById(R.id.rvPosts);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvRecentPosts.setLayoutManager(mLayoutManager);

        mRecentAdapter = new PostAdapter(mActivity, (ArrayList<Post>) mRecentList);
        mRvRecentPosts.setAdapter(mRecentAdapter);

        mBottomLayout = (RelativeLayout) findViewById(R.id.rv_itemload);

        initToolbar(true);
        setToolbarTitle(getString(R.string.recent));
        enableUpButton();
        initLoader();
    }

    private void initFunctionality() {

        showLoader();

        loadRecentPosts();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void initListener() {

        mRecentAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Post model = mRecentList.get(position);
                switch (view.getId()) {
                    case R.id.btn_book:
                        if (model.isBookmark()) {
                            mBookmarkDbController.deleteEachFav(model.getID().intValue());
                            model.setBookmark(false);
                            mRecentAdapter.notifyDataSetChanged();
                            Toast.makeText(mActivity, getString(R.string.removed_from_book), Toast.LENGTH_SHORT).show();

                        } else {
                            int postId = model.getID().intValue();
                            String imgUrl = model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                            String postTitle = model.getTitle().getRendered();
                            String postUrl = model.getPostUrl();
                            String postCategory = model.getEmbedded().getWpTerms().get(0).get(0).getName();
                            String postDate = model.getFormattedDate();

                            mBookmarkDbController.insertData(postId, imgUrl, postTitle, postUrl, postCategory, postDate);
                            model.setBookmark(true);
                            mRecentAdapter.notifyDataSetChanged();
                            Toast.makeText(mActivity, getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_share:
                        final String appPackageName = getPackageName();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(model.getPostUrl())
                                + AppConstant.EMPTY_STRING
                                + getResources().getString(R.string.share_text)
                                + " https://play.google.com/store/apps/details?id=" + appPackageName);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        break;
                    case R.id.card_view_top:
                        ActivityUtilities.getInstance().invokePostDetailsActivity(mActivity, PostDetailsActivity.class, model.getID().intValue(), false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void loadRecentPosts() {
        ApiUtilities.getApiInterface().getLatestPosts(mPageNo).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    loadPosts(response);
                } else {
                    hideMoreItemLoader();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadPosts(Response<List<Post>> response) {
        mRecentList.addAll(response.body());

        updateUI();

        hideMoreItemLoader();
    }

    private void hideMoreItemLoader() {
        mBottomLayout.setVisibility(View.GONE);
        mUserScrolled = true;
    }

    private void implementScrollListener() {
        mRvRecentPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mUserScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,
                                   int dy) {

                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = mLayoutManager.getChildCount();
                mTotalItemCount = mLayoutManager.getItemCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPositions(null)[0];

                if (mUserScrolled && (mVisibleItemCount + mPastVisibleItems) == mTotalItemCount) {
                    mUserScrolled = false;

                    updateRecyclerView();
                }

            }
        });

    }

    private void updateRecyclerView() {
        mBottomLayout.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mPageNo++;
                loadRecentPosts();

            }
        }, 5000);

    }

    private void updateUI() {

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(mActivity);
        }

        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());

        for (int i = 0; i < mRecentList.size(); i++) {
            boolean isBookmarkSet = false;
            for (int j = 0; j < mBookmarkList.size(); j++) {
                if (mRecentList.get(i).getID() == mBookmarkList.get(j).getPostId()) {
                    mRecentList.get(i).setBookmark(true);
                    isBookmarkSet = true;
                    break;
                }
            }
            if (!isBookmarkSet) {
                mRecentList.get(i).setBookmark(false);
            }
        }

        if (mRecentList.size() == 0) {
            showEmptyView();
        } else {
            mRecentAdapter.notifyDataSetChanged();
            hideLoader();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecentList.size() != 0) {
            updateUI();
        }

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}