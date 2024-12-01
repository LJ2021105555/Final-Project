package com.scholl.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ImageListResponse {
    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<ImageResponse> images;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<ImageResponse> getImages() {
        return images;
    }
}
