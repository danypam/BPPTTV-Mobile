package tv.bppt.app.dev.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Content implements Parcelable {
    @SerializedName("rendered")
    private String mRendered;

    public Content() {

    }

    public String getRendered() {
        return mRendered;
    }

    public static Creator<Content> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mRendered);
    }

    protected Content(Parcel in) {
        mRendered = in.readString();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel source) {
            return new Content(source);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

}