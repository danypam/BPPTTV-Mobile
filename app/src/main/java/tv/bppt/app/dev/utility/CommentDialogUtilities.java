package tv.bppt.app.dev.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tv.bppt.app.dev.R;
import tv.bppt.app.dev.api.ApiUtilities;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.preference.AppPreference;
import tv.bppt.app.dev.data.preference.PrefKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDialogUtilities extends DialogFragment {
    private Activity mActivity;

    private String dialogTitle, positiveText, negativeText;
    private int postId;
    private TextView txtDialogTitle;
    private EditText edtName, edtEmail, edtComment;

    public static interface OnCommentCompleteListener {
        public abstract void onCommentComplete(Boolean isOkPressed);
    }

    private OnCommentCompleteListener mListener;

    public static CommentDialogUtilities newInstance(String dialogTitle, String yes, String no, int postId) {
        Bundle args = new Bundle();
        args.putString(AppConstant.BUNDLE_KEY_TITLE, dialogTitle);
        args.putString(AppConstant.BUNDLE_KEY_YES, yes);
        args.putString(AppConstant.BUNDLE_KEY_NO, no);
        args.putInt(AppConstant.BUNDLE_KEY_POST_ID, postId);
        CommentDialogUtilities fragment = new CommentDialogUtilities();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;

        try {
            this.mListener = (OnCommentCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_comment_dialog, null);

        initVar();
        initView(rootView);
        initFunctionality();

        return new androidx.appcompat.app.AlertDialog.Builder(mActivity)
                .setView(rootView)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = edtName.getText().toString();
                        String email = edtEmail.getText().toString();
                        String comment = edtComment.getText().toString();

                        AppPreference.getInstance(mActivity).setString(PrefKey.PREF_NAME, name);
                        AppPreference.getInstance(mActivity).setString(PrefKey.PREF_EMAIL, email);

                        ApiUtilities.getApiInterface().postComment(name, email, comment, postId).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    mListener.onCommentComplete(true);
                                    Toast.makeText(mActivity, mActivity.getString(R.string.successful_message), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(mActivity, jObjError.getString(AppConstant.BUNDLE_KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(mActivity, mActivity.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {

                        }

                    }
                })
                .create();
    }

    public void initVar() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            dialogTitle = getArguments().getString(AppConstant.BUNDLE_KEY_TITLE);
            positiveText = getArguments().getString(AppConstant.BUNDLE_KEY_YES);
            negativeText = getArguments().getString(AppConstant.BUNDLE_KEY_NO);
            postId = getArguments().getInt(AppConstant.BUNDLE_KEY_POST_ID);
        }
    }

    public void initView(View rootView) {
        txtDialogTitle = (TextView) rootView.findViewById(R.id.dialog_title);
        edtName = (EditText) rootView.findViewById(R.id.edt_name);
        edtEmail = (EditText) rootView.findViewById(R.id.edt_email);
        edtComment = (EditText) rootView.findViewById(R.id.edt_comment);
    }

    public void initFunctionality() {
        txtDialogTitle.setText(dialogTitle);
        edtName.setText(AppPreference.getInstance(mActivity).getString(PrefKey.PREF_NAME));
        edtEmail.setText(AppPreference.getInstance(mActivity).getString(PrefKey.PREF_EMAIL));
    }

}