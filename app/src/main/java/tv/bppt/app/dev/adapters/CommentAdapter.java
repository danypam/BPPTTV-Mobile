package tv.bppt.app.dev.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.comment.Comments;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<Comments> mCommentList;
    private ListItemClickListener mItemClickListener;

    public CommentAdapter(Context mContext, ArrayList<Comments> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_list, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgAuthor;
        private TextView tvAuthorName, tvCommentDate, tvComment;
        private RelativeLayout lytComment;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            imgAuthor = (ImageView) itemView.findViewById(R.id.author_img);
            tvAuthorName = (TextView) itemView.findViewById(R.id.author_name);
            tvCommentDate = (TextView) itemView.findViewById(R.id.comment_date);
            tvComment = (TextView) itemView.findViewById(R.id.comment_text);
            lytComment = (RelativeLayout) itemView.findViewById(R.id.lyt_comment);

            lytComment.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mCommentList ? mCommentList.size() : 0);

    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder mainHolder, int position) {
        final Comments model = mCommentList.get(position);

        // setting data over views
        String imgUrl = null;
        if (model.getAuthorAvatarUrl().getUrlLink() != null) {
            imgUrl = model.getAuthorAvatarUrl().getUrlLink();
        }

        if (imgUrl != null) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgAuthor);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_author)
                    .into(mainHolder.imgAuthor);
        }

        mainHolder.tvAuthorName.setText(Html.fromHtml(model.getAuthorName()));
        mainHolder.tvCommentDate.setText(model.getFormattedDate());
        mainHolder.tvComment.setText(Html.fromHtml(model.getContent().getRendered()));

    }
}