package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.HomeCategoryAdapter;
import tv.bppt.app.dev.adapters.FeaturedPagerAdapter;
import tv.bppt.app.dev.adapters.HomeRecentPostAdapter;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.api.HttpParams;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.BookmarkDbController;
import tv.bppt.app.dev.data.sqlite.NotificationDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.bookmark.BookmarkModel;
import tv.bppt.app.dev.models.category.Category;
import tv.bppt.app.dev.models.notification.NotificationModel;
import tv.bppt.app.dev.models.post.Post;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;
import tv.bppt.app.dev.utility.AppUtilities;
import tv.bppt.app.dev.utility.RateItDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private RelativeLayout mNotificationView;
    private ImageButton mImgBtnSearch;

    private ArrayList<Category> mCategoryList;
    private ArrayList<Category> mChildCategoryList;
    private int mItemCount = 5, mPageNo = 1;

    private HomeCategoryAdapter mCategoryAdapter = null;
    private RecyclerView mCategoryRecycler;

    private List<Post> mFeaturedList;
    private ViewPager mFeaturedPager;
    private FeaturedPagerAdapter mFeaturedPagerAdapter = null;

    private List<Post> mRecentPostList;
    private RecyclerView mRvPosts;
    private HomeRecentPostAdapter mRecentAdapter = null;

    private TextView mViewAllFeatured, mViewAllRecent;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mLytFeatured, mLytCategories, mLytRecent, mBottomLayout;

    private boolean mUserScrolled = true;
    private int mRecentPageNo = 1, mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RateItDialogFragment.show(this, getSupportFragmentManager());

        initVar();
        initView();
        loadData();
        initListener();
        implementScrollListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();

        if (mRecentPostList.size() != 0) {
            updateUI();
        }

        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    // received new broadcast
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };


    @Override
    public void onBackPressed() {
        AppUtilities.tapPromptToExit(mActivity);
    }

    private void initVar() {
        mActivity = MainActivity.this;
        mContext = getApplicationContext();

        mCategoryList = new ArrayList<>();
        mChildCategoryList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();
        mRecentPostList = new ArrayList<>();
        mBookmarkList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mImgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);

        mCategoryRecycler = (RecyclerView) findViewById(R.id.rvCategories);
        mCategoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCategoryAdapter = new HomeCategoryAdapter(mContext, mChildCategoryList);
        mCategoryRecycler.setAdapter(mCategoryAdapter);

        mFeaturedPager = (ViewPager) findViewById(R.id.pager_featured_posts);
        mViewAllFeatured = (TextView) findViewById(R.id.view_all_featured);

        mRvPosts = (RecyclerView) findViewById(R.id.rvRecent);
        mLayoutManager = new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false);
        mRvPosts.setLayoutManager(mLayoutManager);
        mRecentAdapter = new HomeRecentPostAdapter(mActivity, (ArrayList<Post>) mRecentPostList);
        mRvPosts.setAdapter(mRecentAdapter);

        mViewAllRecent = (TextView) findViewById(R.id.view_all_recent);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        mLytFeatured = (RelativeLayout) findViewById(R.id.lyt_featured);
        mLytCategories = (RelativeLayout) findViewById(R.id.lyt_categories);
        mLytRecent = (RelativeLayout) findViewById(R.id.lyt_recent);

        mBottomLayout = (RelativeLayout) findViewById(R.id.rv_itemload);

        initToolbar(false);
        initDrawer();
        initLoader();
    }

    private void initListener() {
        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, NotificationListActivity.class, false);
            }
        });

        // Search button click listener
        mImgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLytFeatured.setVisibility(View.GONE);
                mLytCategories.setVisibility(View.GONE);
                mLytRecent.setVisibility(View.GONE);

                mRecentPageNo = 1;

                mFeaturedList.clear();
                mCategoryList.clear();
                mChildCategoryList.clear();
                mRecentPostList.clear();

                loadData();
            }
        });

        mFeaturedPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                swipeRefreshController(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        mViewAllFeatured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, FeaturedListActivity.class, false);
            }
        });

        mCategoryAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Category model = mChildCategoryList.get(position);
                switch (view.getId()) {
                    case R.id.lyt_container:
                        ActivityUtilities.getInstance().subCategoryListActivity(mActivity, SubCategoryListActivity.class, model.getID().intValue(), model.getName(), mCategoryList, false);
                        break;
                    default:
                        break;
                }
            }
        });

        mRecentAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Post model = mRecentPostList.get(position);
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

        mViewAllRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, RecentListActivity.class, false);
            }
        });
    }

    private void swipeRefreshController(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    private void loadData() {
        showLoader();

        loadFeaturedPosts();
        loadCategories();
        loadRecentPosts();

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void loadFeaturedPosts() {
        ApiUtilities.getApiInterface().getFeaturedPosts(mPageNo).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    mFeaturedList.addAll(response.body());
                    mFeaturedPagerAdapter = new FeaturedPagerAdapter(mActivity, (ArrayList<Post>) mFeaturedList);
                    mFeaturedPager.setAdapter(mFeaturedPagerAdapter);
                    mFeaturedPagerAdapter.setItemClickListener(new ListItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view) {
                            int clickedPostId = mFeaturedList.get(position).getID().intValue();
                            ActivityUtilities.getInstance().invokePostDetailsActivity(mActivity, PostDetailsActivity.class, clickedPostId, false);
                        }
                    });
                    if (mFeaturedList.size() > 0) {
                        mLytFeatured.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    private void loadCategories() {
        ApiUtilities.getApiInterface().getCategories(mItemCount).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {

                    int totalPages = Integer.parseInt(response.headers().get(HttpParams.TOTAL_PAGE));

                    if (totalPages > 1) {
                        mItemCount = mItemCount * totalPages;
                        loadCategories();

                    } else {
                        mCategoryList.addAll(response.body());
                        for (int i = 0; i < mCategoryList.size(); i++) {
                            if (mCategoryList.get(i).getParent().intValue() == AppConstant.ZERO_INDEX) {
                                mChildCategoryList.add(mCategoryList.get(i));
                            }
                        }
                        mCategoryAdapter.notifyDataSetChanged();
                        mLytCategories.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void loadRecentPosts() {
        ApiUtilities.getApiInterface().getLatestPosts(mRecentPageNo).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    mRecentPostList.addAll(response.body());
                    updateUI();
                    mLytRecent.setVisibility(View.VISIBLE);
                    hideLoader();
                    hideMoreItemLoader();
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

    private void hideMoreItemLoader() {
        mBottomLayout.setVisibility(View.GONE);
        mUserScrolled = true;
    }

    private void implementScrollListener() {
        mRvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

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
                mRecentPageNo++;
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

        for (int i = 0; i < mRecentPostList.size(); i++) {
            boolean isBookmarkSet = false;
            for (int j = 0; j < mBookmarkList.size(); j++) {
                if (mRecentPostList.get(i).getID() == mBookmarkList.get(j).getPostId()) {
                    mRecentPostList.get(i).setBookmark(true);
                    isBookmarkSet = true;
                    break;
                }
            }
            if (!isBookmarkSet) {
                mRecentPostList.get(i).setBookmark(false);
            }
        }

        if (mRecentPostList.size() == 0) {
            showEmptyView();
        } else {
            mRecentAdapter.notifyDataSetChanged();
            hideLoader();
        }

    }

    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(mContext);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }
    }
}
