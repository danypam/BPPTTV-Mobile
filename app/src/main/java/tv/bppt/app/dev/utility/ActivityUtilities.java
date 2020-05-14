package tv.bppt.app.dev.utility;

import android.app.Activity;
import android.content.Intent;

import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.models.category.Category;

import java.util.ArrayList;

public class ActivityUtilities {

    private static ActivityUtilities sActivityUtilities = null;

    public static ActivityUtilities getInstance() {
        if (sActivityUtilities == null) {
            sActivityUtilities = new ActivityUtilities();
        }
        return sActivityUtilities;
    }

    public void invokeNewActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void subCategoryListActivity(Activity activity, Class<?> tClass, int clickedCategoryId, String categoryName, ArrayList<Category> categoryList, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_CATEGORY_ID, clickedCategoryId);
        intent.putExtra(AppConstant.BUNDLE_KEY_CATEGORY_NAME, categoryName);
        intent.putExtra(AppConstant.BUNDLE_KEY_CATEGORY_LIST, categoryList);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeCustomUrlActivity(Activity activity, Class<?> tClass, String pageTitle, String pageUrl, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_TITLE, pageTitle);
        intent.putExtra(AppConstant.BUNDLE_KEY_URL, pageUrl);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokePostDetailsActivity(Activity activity, Class<?> tClass, int clickedPostId, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_POST_ID, clickedPostId);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeCommentListActivity(Activity activity, Class<?> tClass, int clickedPostId, String commentsLink, boolean shouldDialogOpen, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_POST_ID, clickedPostId);
        intent.putExtra(AppConstant.BUNDLE_KEY_COMMENTS_LINK, commentsLink);
        intent.putExtra(AppConstant.BUNDLE_KEY_DIALOG_OPTION, shouldDialogOpen);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeWallPreviewNCropSetActiviy(Activity activity, Class<?> tClass, String imgUrl, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(AppConstant.BUNDLE_KEY_URL, imgUrl);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

}
