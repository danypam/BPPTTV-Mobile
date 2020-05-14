package tv.bppt.app.dev.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.RelatedAdapter;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.api.HttpParams;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.BookmarkDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.listeners.WebListener;
import tv.bppt.app.dev.models.bookmark.BookmarkModel;
import tv.bppt.app.dev.models.comment.Comments;
import tv.bppt.app.dev.models.post.Post;
import tv.bppt.app.dev.models.post.PostDetails;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;
import tv.bppt.app.dev.utility.AppUtilities;
import tv.bppt.app.dev.utility.TtsEngine;
import tv.bppt.app.dev.webengine.PostWebEngine;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailsActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;

    private ImageView mPostImage;
    private FloatingActionButton mFab;
    private TextView mTvTitle, mTvDate, mTvComment, mTvRelated;
    private int mPostId;
    private PostDetails mModel = null;
    private RelativeLayout mLytContainer;
    private ArrayList<Comments> mCommentList;
    private String mCommentsLink;
    private int mItemCount = 5;
    private LinearLayout mLytSecondary, mLytThird;

    // Bookmarks view
    private List<BookmarkModel> mBookmarkList;
    private BookmarkDbController mBookmarkDbController;
    private boolean mIsBookmark;

    private TtsEngine mTtsEngine;
    private boolean mIsTtsPlaying = false;
    private String mTtsText;
    private MenuItem menuItemTTS;

    private WebView mWebView;
    private PostWebEngine mPostWebEngine;

    private List<Post> mRelatedList;
    private RecyclerView mRvRelated;
    private RelatedAdapter mRelatedAdapter = null;
    private int mPageNo = 1;
    private Bitmap bitmap;
    private String imgUrl = null;

    private Button mBtnViewALlComments, mBtnWriteAComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = PostDetailsActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mPostId = intent.getIntExtra(AppConstant.BUNDLE_KEY_POST_ID, 0);
        }
        mBookmarkList = new ArrayList<>();
        mCommentList = new ArrayList<>();
        mRelatedList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_post_details);

        mPostImage = (ImageView) findViewById(R.id.post_img);
        mFab = (FloatingActionButton) findViewById(R.id.share_post);
        mTvTitle = (TextView) findViewById(R.id.title_text);
        mTvDate = (TextView) findViewById(R.id.date_text);
        mTvComment = (TextView) findViewById(R.id.comment_text);
        mLytContainer = (RelativeLayout) findViewById(R.id.lyt_container);
        mLytSecondary = (LinearLayout) findViewById(R.id.lyt_secondary);
        mTvRelated = (TextView) findViewById(R.id.tv_related);
        mLytThird = (LinearLayout) findViewById(R.id.lyt_third);

        mRvRelated = (RecyclerView) findViewById(R.id.rvRelated);
        mRvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRelatedAdapter = new RelatedAdapter(mContext, (ArrayList<Post>) mRelatedList);
        mRvRelated.setAdapter(mRelatedAdapter);

        mBtnViewALlComments = (Button) findViewById(R.id.btn_view_comment);
        mBtnWriteAComment = (Button) findViewById(R.id.btn_write_comment);

        initWebEngine();

        initLoader();
        initToolbar(false);
        enableUpButton();
    }

    public void initWebEngine() {

        mWebView = (WebView) findViewById(R.id.web_view);

        mPostWebEngine = new PostWebEngine(mWebView, mActivity);
        mPostWebEngine.initWebView();


        mPostWebEngine.initListeners(new WebListener() {
            @Override
            public void onStart() {
                showLoader();
            }

            @Override
            public void onLoaded() {
                hideLoader();
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onNetworkError() {
                showEmptyView();
            }

            @Override
            public void onPageTitle(String title) {
            }
        });
    }

    private void initFunctionality() {

        showLoader();

        mTtsEngine = new TtsEngine(mActivity);

        loadPostDetails();
        updateUI();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    if (mIsBookmark) {
                        mBookmarkDbController.deleteEachFav(mModel.getID().intValue());
                        Toast.makeText(mActivity, getString(R.string.removed_from_book), Toast.LENGTH_SHORT).show();
                    } else {
                        int postId = mModel.getID().intValue();
                        String imgUrl = mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                        String postTitle = mModel.getTitle().getRendered();
                        String postUrl = mModel.getPostUrl();
                        String postCategory = mModel.getEmbedded().getWpTerms().get(0).get(0).getName();
                        String postDate = mModel.getFormattedDate();

                        mBookmarkDbController.insertData(postId, imgUrl, postTitle, postUrl, postCategory, postDate);
                        Toast.makeText(mActivity, getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show();
                    }
                    mIsBookmark = !mIsBookmark;
                    setFabImage();
                }
            }
        });

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, WallPreviewActivity.class, imgUrl, false);
                }
            }
        });

        mLytContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeCommentListActivity(mActivity, CommentListActivity.class, mPostId, mCommentsLink, false, false);
                }
            }
        });

        mRelatedAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Post model = mRelatedList.get(position);
                switch (view.getId()) {
                    case R.id.lyt_container:
                        ActivityUtilities.getInstance().invokePostDetailsActivity(mActivity, PostDetailsActivity.class, model.getID().intValue(), true);
                        break;
                    default:
                        break;
                }
            }
        });

        mBtnViewALlComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeCommentListActivity(mActivity, CommentListActivity.class, mPostId, mCommentsLink, false, false);
                }
            }
        });

        mBtnWriteAComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeCommentListActivity(mActivity, CommentListActivity.class, mPostId, mCommentsLink, true, false);
                }
            }
        });
    }

    private void loadPostDetails() {
        ApiUtilities.getApiInterface().getPostDetails(mPostId).enqueue(new Callback<PostDetails>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<PostDetails> call, Response<PostDetails> response) {
                if (response.isSuccessful()) {

                    mLytSecondary.setVisibility(View.VISIBLE);
                    mFab.setVisibility(View.VISIBLE);

                    mModel = response.body();

                    mCommentsLink = mModel.getLinks().getComments().get(0).getHref();
                    loadComments();
                    loadRelatedPosts();

                    mTvTitle.setText(Html.fromHtml(mModel.getTitle().getRendered()));

                    if (mModel.getEmbedded().getWpFeaturedMedias().size() > 0) {
                        if (mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails() != null) {
                            if (mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl() != null) {
                                imgUrl = mModel.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                            }
                        }
                    }
                    if (imgUrl != null) {
                        Glide.with(getApplicationContext())
                                .load(imgUrl)
                                .into(mPostImage);
                        getBitmap();
                    }

                    mTvDate.setText(mModel.getFormattedDate());

                    String postContent = mModel.getContent().getRendered();
                    mTtsText = new StringBuilder(Html.fromHtml(mModel.getTitle().getRendered())).append(AppConstant.DOT).append(Html.fromHtml(mModel.getContent().getRendered())).toString();
                    postContent = new StringBuilder().append(AppConstant.CSS_PROPERTIES).append(postContent).toString();
                    mPostWebEngine.loadHtml(postContent);

                }
            }

            @Override
            public void onFailure(Call<PostDetails> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    private void loadComments() {

        ApiUtilities.getApiInterface().getComments(mCommentsLink, mItemCount).enqueue(new Callback<List<Comments>>() {
            @Override
            public void onResponse(Call<List<Comments>> call, Response<List<Comments>> response) {
                if (response.isSuccessful()) {

                    int totalPages = Integer.parseInt(response.headers().get(HttpParams.TOTAL_PAGE));

                    if (totalPages > 1) {
                        mItemCount = mItemCount * totalPages;
                        loadComments();
                    } else {
                        mCommentList.clear();
                        mCommentList.addAll(response.body());

                        int commentCount = 0;
                        for (int i = 0; i < mCommentList.size(); i++) {
                            if (mCommentList.get(i).getParent() == 0) {
                                commentCount++;
                            }
                        }

                        mTvComment.setText(String.valueOf(commentCount));
                        mBtnViewALlComments.setText(String.format(getString(R.string.view_comments), commentCount));
                        hideLoader();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Comments>> call, Throwable t) {
                showEmptyView();
                t.printStackTrace();
            }
        });
    }

    public void loadRelatedPosts() {
        ApiUtilities.getApiInterface().getPostsByCategory(mPageNo, mModel.getCategories().get(AppConstant.ZERO_INDEX)).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {

                    List<Post> relatedList = new ArrayList<>();
                    relatedList.addAll(response.body());
                    for (int i = 0; i < relatedList.size(); i++) {
                        if (mRelatedList.size() == AppConstant.THIRD_INDEX) {
                            break;
                        }
                        if (relatedList.get(i).getID().intValue() != mPostId) {
                            mRelatedList.add(relatedList.get(i));
                        }
                    }
                    if (mRelatedList.size() > AppConstant.ZERO_INDEX) {
                        mTvRelated.setVisibility(View.VISIBLE);
                    }
                    mLytThird.setVisibility(View.VISIBLE);

                    mRelatedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
                showEmptyView();
            }
        });
    }

    public void updateUI() {

        if (mBookmarkDbController == null) {
            mBookmarkDbController = new BookmarkDbController(mContext);
        }
        mBookmarkList.clear();
        mBookmarkList.addAll(mBookmarkDbController.getAllData());


        for (int i = 0; i < mBookmarkList.size(); i++) {
            if (mPostId == mBookmarkList.get(i).getPostId()) {
                mIsBookmark = true;
                break;
            }
        }
        setFabImage();
    }

    private void setFabImage() {
        if (mIsBookmark) {
            mFab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_book));
        } else {
            mFab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_un_book));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menus_read_article:
                if (mModel != null) {
                    toggleTtsPlay();
                }
                return true;
            case R.id.menus_share_post:
                if (mModel != null) {
                    final String appPackageName = mActivity.getPackageName();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mModel.getPostUrl())
                            + AppConstant.EMPTY_STRING
                            + mActivity.getResources().getString(R.string.share_text)
                            + " https://play.google.com/store/apps/details?id=" + appPackageName);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                }
                break;
            case R.id.menus_copy_text:
                if (mModel != null) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", Html.fromHtml(mModel.getTitle().getRendered() + mModel.getContent().getRendered()));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menus_download_image:
                if (mModel != null) {
                    AppUtilities.downloadFile(mContext, mActivity, bitmap);
                }
                break;
            case R.id.menus_set_image:
                if (mModel != null) {
                    ActivityUtilities.getInstance().invokeWallPreviewNCropSetActiviy(mActivity, WallCropNSetActivity.class, imgUrl, false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTtsPlay() {
        if (mIsTtsPlaying) {
            mTtsEngine.releaseEngine();
            mIsTtsPlaying = false;
        } else {
            mTtsEngine.startEngine(mTtsText);
            mIsTtsPlaying = true;
        }
        toggleTtsView();
    }

    private void toggleTtsView() {
        if (mIsTtsPlaying) {
            menuItemTTS.setTitle(R.string.site_menu_stop_reading);
        } else {
            menuItemTTS.setTitle(R.string.read_post);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTtsEngine.releaseEngine();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTtsEngine.releaseEngine();
        mModel = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsTtsPlaying) {
            mIsTtsPlaying = false;
            menuItemTTS.setTitle(R.string.read_post);
        }

        if (mCommentsLink != null) {
            loadComments();
        }
        // load full screen ad
        AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);

        menuItemTTS = menu.findItem(R.id.menus_read_article);

        return true;
    }

    public void getBitmap() {
        Glide.with(mContext)
                .asBitmap()
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = resource;
                    }
                });
    }

}
