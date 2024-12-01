package com.scholl.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResponse {
    private int id;
    private String name;
    private String image;
    private String uploadedAt;
    private List<CheckJson> check_json;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public List<CheckJson> getCheckJson() {
        return check_json;
    }

    public void setCheckJson(List<CheckJson> checkJson) {
        this.check_json = checkJson;
    }

    public static class CheckJson {
        @SerializedName("class")
        private int classNumber;
        private double confidence;
        private double height;
        private String name;
        private double width;
        private double xcenter;
        private double ycenter;

        public int getClassNumber() {
            return classNumber;
        }

        public void setClassNumber(int classNumber) {
            this.classNumber = classNumber;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getXcenter() {
            return xcenter;
        }

        public void setXcenter(double xcenter) {
            this.xcenter = xcenter;
        }

        public double getYcenter() {
            return ycenter;
        }

        public void setYcenter(double ycenter) {
            this.ycenter = ycenter;
        }
    }
}
