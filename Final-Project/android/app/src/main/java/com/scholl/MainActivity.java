package com.scholl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.scholl.entity.ImageListResponse;
import com.scholl.entity.ImageResponse;
import com.scholl.service.ApiService;
import com.scholl.util.ApiClient;
import com.scholl.util.FileUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private LinearLayout imageContainer;
    private Button buttonUploadImage;
    private ApiService apiService;

    private SwipeRefreshLayout swipeRefreshLayout;


    private final String url = "http://192.168.1.123:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // 获取 SwipeRefreshLayout
        imageContainer = findViewById(R.id.imageContainer);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> fetchImageList());

        // 获取图像列表
        fetchImageList();

        // 设置上传按钮点击事件
        buttonUploadImage.setOnClickListener(v -> selectImageFromGallery());
    }

    private void fetchImageList() {
        swipeRefreshLayout.setRefreshing(true);

        apiService.getImageList().enqueue(new Callback<ImageListResponse>() {
            @Override
            public void onResponse(Call<ImageListResponse> call, Response<ImageListResponse> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<ImageResponse> images = response.body().getImages();
                    if (images != null && !images.isEmpty()) {
                        displayImages(images);
                    } else {
                        Toast.makeText(MainActivity.this, "Images list is empty!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Get image list failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageListResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(MainActivity.this, "API error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayImages(List<ImageResponse> images) {
        if (images == null || images.isEmpty()) {
            Toast.makeText(this, "没有图像数据", Toast.LENGTH_SHORT).show();
            return;
        }

        imageContainer.removeAllViews(); // 清空之前的内容

        for (ImageResponse image : images) {
            LinearLayout imageLayout = new LinearLayout(this);
            imageLayout.setOrientation(LinearLayout.VERTICAL);
            imageLayout.setPadding(8, 8, 8, 16);

            // 创建图片视图
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400
            );
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                    .load(url + image.getImage())
                    .into(imageView);

            // 创建文字视图
            TextView textView = new TextView(this);
            textView.setTextSize(14);
            textView.setPadding(0, 8, 0, 8);

            // 显示 check_json 信息
            if (image.getCheckJson() != null) {
                StringBuilder checkJsonInfo = new StringBuilder();
                checkJsonInfo.append("Result: \n");
                for (ImageResponse.CheckJson checkJson : image.getCheckJson()) {
                    checkJsonInfo.append(
                                    "Class: ").append(checkJson.getClassNumber()).append("\n")
                            .append("Confidence: ").append(checkJson.getConfidence()).append("\n")
                            .append("Height: ").append(checkJson.getHeight()).append("\n")
                            .append("Name: ").append(checkJson.getName()).append("\n")
                            .append("Width: ").append(checkJson.getWidth()).append("\n")
                            .append("X Center: ").append(checkJson.getXcenter()).append("\n")
                            .append("Y Center: ").append(checkJson.getYcenter()).append("\n\n");
                }
                textView.setText(checkJsonInfo.toString().trim());
            } else {
                textView.setText("No check_json data available");
            }

            // 添加图片和文字到布局
            imageLayout.addView(imageView); // 图片先添加
            imageLayout.addView(textView); // 然后添加文字

            // 将布局添加到容器
            imageContainer.addView(imageLayout);
        }
    }


    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] fileBytes = FileUtils.readBytes(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

            apiService.uploadImage(body).enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MainActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                        fetchImageList();
                    } else {
                        Toast.makeText(MainActivity.this, "upload fail", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "api error"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "file no found", Toast.LENGTH_SHORT).show();
        }
    }
}
