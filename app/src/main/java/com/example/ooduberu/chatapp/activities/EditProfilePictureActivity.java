package com.example.ooduberu.chatapp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

@Deprecated
public class EditProfilePictureActivity extends BaseActivity {
    Unbinder unbinder;
    File photoFile = null;
    StorageReference profileImagesStorage;
    DatabaseReference userTable;
    Uri image_uri;

    @BindView(R.id.user_profile_image) ImageView user_profile_image;
    @BindView(R.id.fullName) TextView fullName;
    @BindView(R.id.acceptImage) ImageButton acceptImage;

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "CropImage";
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 1234;
    private static final int REQUEST_CAMERA_PERMISSIONS = 1000;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static int gallery_pick = 1;//specify the number of pictures you want to pick

    String profile_image;
    String name;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_picture);

        unbinder = ButterKnife.bind(this);

        uId = AppPreference.getCurrentUserId();

        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        profileImagesStorage = FirebaseStorage.getInstance().getReference();

        profile_image = getIntent().getStringExtra("profile_image");
        name = getIntent().getStringExtra("full_name");


        if(profile_image.equals("default")){
            user_profile_image.setImageResource(R.drawable.person_placeholder);
        }
        else{
            Glide.with(getBaseContext()).load(profile_image)
                    .apply(new RequestOptions().error(R.drawable.person_placeholder).placeholder(R.drawable.person_placeholder).fitCenter())
                    .into(user_profile_image);
        }

        fullName.setText(name);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //getWindow().setLayout((int)(width*.6),(int)(height*.6));

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;

        getWindow().setAttributes(layoutParams);



    }

    //to activate mobile camera intent
    private void photoCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAMERA_REQUEST);
    }

    // to check if camera hardware exists
    public boolean checkCameraExists() {
        return getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    @OnClick(R.id.pickCameraImage)
    public void useCamera(){
        if (checkCameraExists()){
            boolean result = checkStoragePermission(EditProfilePictureActivity.this);
            if(result){
                photoCameraIntent();
            }

        }
        else{
            Toasty.error(getBaseContext(),"you don't have camera on this phone to use this feature").show();
        }
    }

    @OnClick(R.id.pickGalleryImage)
    public void pickGalleyPicture(){
        boolean result = checkStoragePermission(EditProfilePictureActivity.this);
        if(result){
            Intent gallery_intent = new Intent();
            gallery_intent.setType("image/*");//sets the type as an image
            gallery_intent.setAction(Intent.ACTION_GET_CONTENT);//gets the content
            //Intent.createChooser() is a function that allows you open the external gallery from your phone
            startActivityForResult(Intent.createChooser(gallery_intent,"select profile image"),gallery_pick);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkStoragePermission(final AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission Required");
            alertBuilder.setMessage("Permision to Read/Write to External storage is required");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(!NetworkUtils.isNetworkAvailable(EditProfilePictureActivity.this)) {
//            Toast.makeText(SettingsActivity.this, "No network connection!",Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (requestCode == gallery_pick && resultCode == RESULT_OK){
            //if there is no error
            image_uri = data.getData();//gets the data uri of the image from gallery
            startCrop(image_uri);
            //Toast.makeText(getBaseContext(),image_uri.toString(),Toast.LENGTH_LONG).show();

        }
        if(requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK){

            if(data != null){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                photoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    photoFile.createNewFile();
                    fo = new FileOutputStream(photoFile);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            image_uri = Uri.fromFile(photoFile);
            startCrop(image_uri);

        }else if (requestCode == UCrop.REQUEST_CROP) {
            handleCropResult(data);
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(this.getCacheDir(), destinationFileName)));

        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);

        uCrop.start(this, UCrop.REQUEST_CROP);
    }

    private void handleCropResult(@NonNull Intent result) {
        if(result == null){
            return;
        }
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            try {

                Bitmap thumbnail = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(resultUri));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                File photoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                photoFile.createNewFile();
                fo = new FileOutputStream(photoFile);
                fo.write(byteArrayOutputStream.toByteArray());
                fo.close();

                image_uri = Uri.fromFile(photoFile);

                Glide.with(this).load(photoFile).into(user_profile_image);
                acceptImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                //Log.e(TAG, "setImageUri", e);
                //ToastUtils.showErrorMessageToast(getContext(), e.getMessage());
            }
        } else {
            //ToastUtils.showErrorMessageToast(getContext(), "Failed to crop image");
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            //Log.e(TAG, "handleCropError: ", cropError);
            //ToastUtils.showErrorMessageToast(getContext(), cropError.getMessage());
        } else {
            //ToastUtils.showErrorMessageToast(getContext(), "Unexpected error occurred");
        }
    }
    private UCrop basisConfig(@NonNull UCrop uCrop) {
        //uCrop = uCrop.useSourceImageAspectRatio();
        uCrop = uCrop.withAspectRatio(167, 100);
        // uCrop = uCrop.withMaxResultSize(300, 180);
        return uCrop;
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG); //PNG
        //options.setCompressionQuality(80);

        options.setHideBottomControls(true);
        //options.setFreeStyleCropEnabled(false);

        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(2);

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);

        /*
        If you want to configure how gestures work for all UCropActivity tabs
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.
        options.setMaxBitmapSize(640);
        * */

        /*
        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);
        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));
        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));
       */

        return uCrop.withOptions(options);
    }

    @OnClick(R.id.acceptImage)
    public void saveProfileImage(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }
        showProgressLoader();
        //upload image to the firebase storage then download the image from the storage inother to save it in the database
        if(image_uri != null){
            final StorageReference filePath = profileImagesStorage.child("profile_images").child(uId+".jpg");

            UploadTask uploadTask = filePath.putFile(image_uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                            throw  task.getException();
                        }
                        return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                      if(task.isSuccessful()){
                          final Uri download_uri = task.getResult();

                          userTable.child(uId).child("image").setValue(download_uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  hideProgressLoader();
                                  if(task.isSuccessful()){
                                      Toasty.success(getBaseContext(),"profile picture updated").show();
                                  }else{
                                      Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                  }
                              }
                          });

                      }
                      else{
                          hideProgressLoader();
                          Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                      }
                }
            });



        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
