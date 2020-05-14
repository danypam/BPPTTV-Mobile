package tv.bppt.app.dev.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.listeners.ListItemClickListener;
import tv.bppt.app.dev.models.post.Post;

import java.util.ArrayList;
import java.util.Random;

public class HomeRecentPostAdapter extends RecyclerView.Adapter<HomeRecentPostAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<Post> mRecentPostList;
    private ListItemClickListener mItemClickListener;

    public HomeRecentPostAdapter(Context mContext, ArrayList<Post> mRecentPostList) {
        this.mContext = mContext;
        this.mRecentPostList = mRecentPostList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recent_post_list, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPost;
        private TextView tvCategoryName, tvPostTitle, tvPostDate;
        private ImageButton btnBook, btnShare;
        private CardView cardView;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            imgPost = (ImageView) itemView.findViewById(R.id.post_img);
            tvCategoryName = (TextView) itemView.findViewById(R.id.category_name);
            tvPostTitle = (TextView) itemView.findViewById(R.id.title_text);
            tvPostDate = (TextView) itemView.findViewById(R.id.date_text);
            btnBook = (ImageButton) itemView.findViewById(R.id.btn_book);
            btnShare = (ImageButton) itemView.findViewById(R.id.btn_share);
            cardView = (CardView) itemView.findViewById(R.id.card_view_top);

            btnBook.setOnClickListener(this);
            btnShare.setOnClickListener(this);
            cardView.setOnClickListener(this);
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
        return (null != mRecentPostList ? mRecentPostList.size() : 0);
    }

    @Override
    public void onBindViewHolder(HomeRecentPostAdapter.ViewHolder mainHolder, int position) {
        final Post model = mRecentPostList.get(position);

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

        mainHolder.tvCategoryName.setText(Html.fromHtml(model.getEmbedded().getWpTerms().get(0).get(0).getName()));
        mainHolder.tvPostTitle.setText(Html.fromHtml(model.getTitle().getRendered()));
        mainHolder.tvPostDate.setText(model.getFormattedDate());


        if (model.isBookmark()) {
            mainHolder.btnBook.setImageResource(R.drawable.ic_book);
        } else {
            mainHolder.btnBook.setImageResource(R.drawable.ic_un_book);
        }

        Random rand = new Random();
        int i = rand.nextInt(5) + 1;


        switch (i) {
            case 1:
                mainHolder.tvCategoryName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_green));
                break;
            case 2:
                mainHolder.tvCategoryName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_orange));
                break;
            case 3:
                mainHolder.tvCategoryName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_pink));
                break;
            case 4:
                mainHolder.tvCategoryName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_purple));
                break;
            case 5:
                mainHolder.tvCategoryName.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_red));
                break;
            default:
                break;
        }

    }
}