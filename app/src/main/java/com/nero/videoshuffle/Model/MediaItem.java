package com.nero.videoshuffle.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by nlang on 11/12/2015.
 */
public class MediaItem implements Parcelable {
    public String Data;
    public String Title;
    public String Size;
    public long Id;

    public MediaItem(long id, String data, String title, String size) {
        this.Id = id;
        this.Data = data;
        this.Title = title;
        this.Size = size;
    }

    public Uri getUri() {
        return Uri.fromFile(new File(Data));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.Id);
        dest.writeString(this.Data);
        dest.writeString(this.Title);
        dest.writeString(this.Size);
    }

    protected MediaItem(Parcel in) {
        this.Id = in.readLong();
        this.Data = in.readString();
        this.Title = in.readString();
        this.Size = in.readString();
    }

    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
}
