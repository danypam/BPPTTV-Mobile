package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.CategoryAdapter;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.api.HttpParams;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.category.Category;
import tv.bppt.app.dev.utility.ActivityUtilities;
import tv.bppt.app.dev.utility.AdsUtilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Category> mCategoryList;
    private ArrayList<Category> mChildCategoryList;
    private CategoryAdapter mCategoryAdapter = null;
    private RecyclerView mCategoryRecycler;
    private int mItemCount = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = CategoryListActivity.this;
        mContext = mActivity.getApplicationContext();

        mCategoryList = new ArrayList<>();
        mChildCategoryList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_category_list);

        mCategoryRecycler = (RecyclerView) findViewById(R.id.rvCategories);
        mCategoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mCategoryAdapter = new CategoryAdapter(mContext, mChildCategoryList);
        mCategoryRecycler.setAdapter(mCategoryAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.site_menu_category_list));
        enableUpButton();
        initLoader();
    }

    private void initFunctionality() {

        showLoader();

        loadCategories();

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void initListener() {
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
                        mCategoryList.clear();
                        mChildCategoryList.clear();
                        mCategoryList.addAll(response.body());
                        for (int i = 0; i < mCategoryList.size(); i++) {
                            if (mCategoryList.get(i).getParent().intValue() == AppConstant.ZERO_INDEX) {
                                mChildCategoryList.add(mCategoryList.get(i));
                            }
                        }
                        mCategoryAdapter.notifyDataSetChanged();
                        hideLoader();
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