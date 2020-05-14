package tv.bppt.app.dev.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CommentLink implements Parcelable {
    @SerializedName("href")
    String mHref;

    public String getHref() {
        return mHref;
    }

    public static Creator<CommentLink> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mHref);
    }

    protected CommentLink(Parcel in) {
        mHref = in.readString();
    }

    public static final Creator<CommentLink> CREATOR = new Creator<CommentLink>() {
        @Override
        public CommentLink createFromParcel(Parcel source) {
            return new CommentLink(source);
        }

        @Override
        public CommentLink[] newArray(int size) {
            return new CommentLink[size];
        }
    };

}