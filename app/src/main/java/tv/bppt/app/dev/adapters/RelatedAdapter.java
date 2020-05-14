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
import tv.bppt.app.dev.models.post.Post;

import java.util.ArrayList;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<Post> mRelatedList;
    private ListItemClickListener mItemClickListener;

    public RelatedAdapter(Context mContext, ArrayList<Post> mRelatedList) {
        this.mContext = mContext;
        this.mRelatedList = mRelatedList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_list, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPost;
        private TextView tvPostTitle;
        private RelativeLayout lytContainer;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            tvPostTitle = (TextView) itemView.findViewById(R.id.title_text);
            lytContainer = (RelativeLayout) itemView.findViewById(R.id.lyt_container);

            lytContainer.setOnClickListener(this);
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
        return (null != mRelatedList ? mRelatedList.size() : 0);

    }

    @Override
    public void onBindViewHolder(RelatedAdapter.ViewHolder mainHolder, int position) {
        final Post model = mRelatedList.get(position);

        // setting data over views
        String imgUrl = null;
        if (model.getEmbedded().getWpFeaturedMedias().size() > 0) {
            if (model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails() != null) {
                if (model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl() != null) {
                    imgUrl = model.getEmbedded().getWpFeaturedMedias().get(0).getMediaDetails().getSizes().getFullSize().getSourceUrl();
                }
            }
        }

        if (imgUrl != null) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgPost);
        } else {
            Glide.with(mContext)
                    .load(R.color.imgPlaceholder)
                    .into(mainHolder.imgPost);
        }
        mainHolder.tvPostTitle.setText(Html.fromHtml(model.getTitle().getRendered()));

    }
}