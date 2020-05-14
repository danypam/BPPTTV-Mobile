package tv.bppt.app.dev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.adapters.NotificationAdapter;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.sqlite.NotificationDbController;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.notification.NotificationModel;
import tv.bppt.app.dev.utility.AdsUtilities;
import tv.bppt.app.dev.utility.DialogUtilities;

import java.util.ArrayList;

public class NotificationListActivity extends BaseActivity {

    private Context mContext;
    private Activity mActivity;

    private RecyclerView mRecycler;
    private NotificationAdapter mNotificationAdapter;
    private ArrayList<NotificationModel> mNotificationList;
    private MenuItem mMenuItemDeleteAll;
    private NotificationDbController mNotificationDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = NotificationListActivity.this;
        mContext = mActivity.getApplicationContext();

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mNotificationList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_notification);

        mRecycler = (RecyclerView) findViewById(R.id.rv_recycler);
        mNotificationAdapter = new NotificationAdapter(mActivity, mNotificationList);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setAdapter(mNotificationAdapter);

        initLoader();
        initToolbar(true);
        setToolbarTitle(getString(R.string.notifications));
        enableUpButton();
    }

    private void initFunctionality() {

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void updateUI() {
        showLoader();

        if (mNotificationDbController == null) {
            mNotificationDbController = new NotificationDbController(mContext);
        }
        mNotificationList.clear();
        mNotificationList.addAll(mNotificationDbController.getAllData());
        mNotificationAdapter.notifyDataSetChanged();

        hideLoader();

        if (mNotificationList.size() == 0) {
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

    private void initListener() {
        // recycler list item click listener
        mNotificationAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mNotificationDbController.updateStatus(mNotificationList.get(position).getId(), true);

                Intent intent = new Intent(mContext, NotificationDetailsActivity.class);
                intent.putExtra(AppConstant.BUNDLE_KEY_TITLE, mNotificationList.get(position).getTitle());
                intent.putExtra(AppConstant.BUNDLE_KEY_MESSAGE, mNotificationList.get(position).getMessage());
                intent.putExtra(AppConstant.BUNDLE_KEY_URL, mNotificationList.get(position).getUrl());
                startActivity(intent);
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
                DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.notifications), getString(R.string.delete_all_notification), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_ALL_NOT);
                dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
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
        if (mNotificationAdapter != null) {
            updateUI();
        }
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_ALL_NOT)) {
                mNotificationDbController.deleteAllNot();
                updateUI();
            }
        }
    }
}
