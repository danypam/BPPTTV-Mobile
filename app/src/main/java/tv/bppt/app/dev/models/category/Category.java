package tv.bppt.app.dev.models.category;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Category implements Parcelable {
    @SerializedName("id")
    private Double mID;
    @SerializedName("name")
    private String mName;
    @SerializedName("parent")
    private Double mParent;
    @SerializedName("count")
    private Double mCount;

    public Double getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getParent() {
        return mParent;
    }

    public void setParent(Double mParent) {
        this.mParent = mParent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mID);
        dest.writeString(mName);
        dest.writeDouble(mParent);
        dest.writeDouble(mCount);
    }

    protected Category(Parcel in) {
        mID = in.readDouble();
        mName = in.readString();
        mParent = in.readDouble();
        mCount = in.readDouble();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

}
