package tv.bppt.app.dev.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import tv.bppt.app.dev.models.comment.CommentLink;

import java.util.ArrayList;

public class Links implements Parcelable {

    @SerializedName("replies")
    private ArrayList<CommentLink> mComments = new ArrayList<>();

    public Links() {
    }

    public ArrayList<CommentLink> getComments() {
        return mComments;
    }

    public static Creator<Links> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mComments);
    }

    protected Links(Parcel in) {
        in.readList(mComments, CommentLink.class.getClassLoader());
    }

    public static final Creator<Links> CREATOR = new Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel source) {
            return new Links(source);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };

}
