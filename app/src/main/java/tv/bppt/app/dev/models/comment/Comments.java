package tv.bppt.app.dev.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import tv.bppt.app.dev.models.post.Content;
import tv.bppt.app.dev.utility.DateUtilities;

public class Comments implements Parcelable {
    @SerializedName("id")
    private Double mID;
    @SerializedName("parent")
    private Double mParent;
    @SerializedName("author_name")
    private String mAuthorName;
    @SerializedName("content")
    private Content mContent = new Content();
    @SerializedName("author_avatar_urls")
    private AuthorAvatar mAuthorAvatarUrl = new AuthorAvatar();
    @SerializedName("date")
    private String mOldDate;

    public Double getParent() {
        return mParent;
    }

    public void setParent(Double mParent) {
        this.mParent = mParent;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public Content getContent() {
        return mContent;
    }

    public void setContent(Content mContent) {
        this.mContent = mContent;
    }

    public AuthorAvatar getAuthorAvatarUrl() {
        return mAuthorAvatarUrl;
    }

    public static Creator<Comments> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mID);
        dest.writeDouble(mParent);
        dest.writeString(mAuthorName);
        dest.writeParcelable(mContent, flags);
        dest.writeParcelable(mAuthorAvatarUrl, flags);
        dest.writeString(mOldDate);
    }

    protected Comments(Parcel in) {
        mID = in.readDouble();
        mParent = in.readDouble();
        mAuthorName = in.readString();
        mContent = in.readParcelable(Content.class.getClassLoader());
        mAuthorAvatarUrl = in.readParcelable(AuthorAvatar.class.getClassLoader());
        mOldDate = in.readString();
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel source) {
            return new Comments(source);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

    public String getFormattedDate() {
        return DateUtilities.getFormattedDate(mOldDate);
    }

}
