package com.snape.shivichu.simpleretrofitupload.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.snape.shivichu.simpleretrofitupload.R;
import com.snape.shivichu.simpleretrofitupload.util.NetworkTask;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button mPickUpload;
    private final int GALLERY_REQUEST_CODE = 1001;
    private NetworkTask networkTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkTask = new NetworkTask(this);
        progressDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.image);
        mPickUpload = findViewById(R.id.btnPickUpload);

        mPickUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = null;
                    if (data != null) {
                        selectedImage = data.getData();

                        String filePath = getRealPathFromURI(getApplicationContext(), selectedImage);
                        File file = new File(filePath);
                        Log.e("FilePath -->", filePath);
                        Log.e("FilePath -->", file.getAbsolutePath());


                        /*Getting file name from file path*/
                        Log.e("File Name ->",file.getName());



                        imageView.setImageURI(selectedImage);


                        //upload Image
                        uploadImageUsingRetrofit(file,file.getName());

                        //uplaod video
                        //  uploadVideoUsingRetrofit(file);
                    }
                    break;
            }
        }

    }

    private void uploadVideoUsingRetrofit(File videoFile) {

        progressDialog.setMessage("Uplaoding Video.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        retrofit2.Call<String> response;
        MultipartBody.Part vFile;
        RequestBody fbody;
        String fileExt;

        fbody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        fileExt = FilenameUtils.getExtension(videoFile.toString());
        vFile = MultipartBody.Part.createFormData("video", "video_file." + fileExt, fbody);

        response = networkTask.uploadVideo("fileUpload", vFile);

        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Log.e("OnResponse-->", "Success" + response);

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Log.e("OnResponse-->", "Failed:" + t.getMessage());

            }
        });

    }

    private void uploadImageUsingRetrofit(File imageFile, String filename) {

        progressDialog.setMessage("Uploading Image.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        retrofit2.Call<String> response;
        RequestBody reqFile = null;

        /*create image request here*/
        reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", filename, reqFile);

        response = networkTask.uploadImageToServer("fileUpload", part);

        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Log.e("OnResponse:", "Success" + response);

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                Log.e("OnResponse:", "Failed" + t.getMessage());
            }
        });

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
            }
            return Objects.requireNonNull(cursor).getString(column_index);
        } catch (Exception e) {
            Log.e("TAG", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
