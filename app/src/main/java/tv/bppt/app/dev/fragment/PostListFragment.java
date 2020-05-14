package tv.bppt.app.dev.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import tv.bppt.app.dev.R;
import tv.bppt.app.dev.activity.PostDetailsActivity;
import tv.bppt.app.dev.adapters.PostAdapter;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.BookmarkDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.bookmark.BookmarkModel;
import tv.bppt.app.dev.models.post.Post;
import tv.bppt.app.dev.utility.ActivityUtilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListFragment extends Fragment {

    private LinearLayout mLoadingView, mNoDataView;

    private ArrayList<Post> postList;
    private PostAdapter mAdapter = null;

    private RelativeLayout mBottomLayout;
    private LinearLayoutManager mLayoutManager;
    private boolean mUserScrolled = true;
    private RecyclerView mRvPosts;
    private int mCategoryId, mPageNo = 1, mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private String mSearchedText;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);


        initVar();
        initView(rootView);
        initFunctionality(rootView);
        initListener();

        implementScrollListener();

        return rootView;
    }


    public void initVar() {

        postList = new ArrayList<>();
        mBookmarkList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategoryId = getArguments().getInt(AppConstant.BUNDLE_KEY_CATEGORY_ID);
            mSearchedText = getArguments().getString(AppConstant.BUNDLE_KEY_SEARCH_TEXT);
        }
    }

    public void initView(View rootView) {

        mBottomLayout = (RelativeLayout) rootView.findViewById(R.id.rv_itemload);
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);

        initLoader(rootView);

        mRvPosts = (RecyclerView) rootView.findViewById(R.id.rvPosts);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvPosts.setLayoutManager(mLayoutManager);

        mAdapter = new PostAdapter(getActivity(), postList);
        mRvPosts.setAdapter(mAdapter);


    }

    public void initLoader(View rootView) {
        mLoadingView = (LinearLayout) rootView.findViewById(R.id.loadingView);
        mNoDataView = (LinearLayout) rootView.findViewById(R.id.noDataView);
    }

    public void showLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void hideLoader() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mNoDataView != null) {
            mNoDataView.setVisibility(View.VISIBLE);
        }
    }


    public void initFunctionality(View rootView) {

        showLoader();

        loadPosts();
    }

    public void initListener() {

        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Post model = postList.get(position);
                switch (view.getId()) {
                    case R.id.btn_book:
                        if (model.isBookmark()) {
                            mBookmarkDbController.deleteEachFav(model.getID().intValue());
                            model.setBookmark(false);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.removed_from_book), Toast.LENGTH_SHORT).show();

                        } else {
                            int postId = model.getID().intValue();
                            String imgUrl = model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                            String postTitle = model.getTitle().getRendered();
                            String postUrl = model.getPostUrl();
                            String postCategory = model.getEmbedded().getWpTerms().get(0).get(0).getName();
                            String postDate = model.getFormattedDate();

                            mBookmarkDbController.insertData(postId, imgUrl, postTitle, postUrl, postCategory, postDate);
                            model.setBookmark(true);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_share:
                        final String appPackageName = getActivity().getPackageName();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(model.getPostUrl())
                                + AppConstant.EMPTY_STRING
                                + getActivity().getResources().getString(R.string.share_text)
                                + " https://play.google.com/store/apps/details?id=" + appPackageName);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        break;
                    case R.id.card_view_top:
                        ActivityUtilities.getInstance().invokePostDetailsActivity(getActivity(), PostDetailsActivity.class, model.getID().intValue(), false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void loadPosts() {
        switch (mCategoryId) {
            case AppConstant.BUNDLE_KEY_FEATURED_POST_ID:
                loadFeaturedPosts();
                break;
            case AppConstant.BUNDLE_KEY_SEARCH_POST_ID:
                loadSearchedPosts();
                break;
            default:
                break;
        }
    }

    public void loadFeaturedPosts() {
        ApiUtilities.getApiInterface().getFeaturedPosts(mPageNo).enqueue(new Callback<List<Post>>() {
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

    public void loadSearchedPosts() {
        ApiUtilities.getApiInterface().getSearchedPosts(mPageNo, mSearchedText).enqueue(new Callback<List<Post>>() {
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
        postList.addAll(response.body());

        updateUI();

        hideMoreItemLoader();
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

                mPageNo++;
                loadPosts();

            }
        }, 5000);

    }

    private void updateUI() {

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(getActivity());
        }

        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());

        for (int i = 0; i < postList.size(); i++) {
            boolean isBookmarkSet = false;
            for (int j = 0; j < mBookmarkList.size(); j++) {
                if (postList.get(i).getID() == mBookmarkList.get(j).getPostId()) {
                    postList.get(i).setBookmark(true);
                    isBookmarkSet = true;
                    break;
                }
            }
            if (!isBookmarkSet) {
                postList.get(i).setBookmark(false);
            }
        }

        if (postList.size() == 0) {
            showEmptyView();
        } else {
            mAdapter.notifyDataSetChanged();
            hideLoader();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (postList.size() != 0) {
            updateUI();
        }
    }
}
