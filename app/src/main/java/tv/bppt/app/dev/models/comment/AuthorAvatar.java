package tv.bppt.app.dev.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AuthorAvatar implements Parcelable {
    @SerializedName("96")
    private String mUrlLink;

    public AuthorAvatar() {
    }

    public String getUrlLink() {
        return mUrlLink;
    }

    public static Creator<AuthorAvatar> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrlLink);
    }

    protected AuthorAvatar(Parcel in) {
        mUrlLink = in.readString();
    }

    public static final Creator<AuthorAvatar> CREATOR = new Creator<AuthorAvatar>() {
        @Override
        public AuthorAvatar createFromParcel(Parcel source) {
            return new AuthorAvatar(source);
        }

        @Override
        public AuthorAvatar[] newArray(int size) {
            return new AuthorAvatar[size];
        }
    };

}