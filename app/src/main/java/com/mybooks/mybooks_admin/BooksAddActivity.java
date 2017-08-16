package com.mybooks.mybooks_admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BooksAddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 99;
    private EditText mTitle;
    private EditText mPublisher;
    private EditText mAuthor;
    private EditText mCourse;
    private EditText mSem;
    private EditText mMRP;
    private EditText mNewPrice;
    private EditText mOldPrice;
    private EditText mAvlCopy;
    private TextView mAddBtn;

    private ImageView upload_image;

    DatabaseReference databaseReference;
    StorageReference mStorageRef;

    private ProgressDialog progressDialog;

    Bitmap updatedBitmap;

    String image_source = "na";
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_add);

        mTitle = (EditText) findViewById(R.id.addBookTitle);
        mPublisher = (EditText) findViewById(R.id.addBookPublisher);
        mAuthor = (EditText) findViewById(R.id.addBookAuthor);
        mCourse = (EditText) findViewById(R.id.addBookCourse);
        mSem = (EditText) findViewById(R.id.addBookSem);

        mMRP = (EditText) findViewById(R.id.addBookMrp);

        mNewPrice = (EditText) findViewById(R.id.addBookNewPrice);
        mOldPrice = (EditText) findViewById(R.id.addBookOldPrice);
        mAvlCopy = (EditText) findViewById(R.id.addBookAvlCopy);

        mAddBtn = (TextView) findViewById(R.id.addBookAddBtn);
        mAddBtn.setOnClickListener(this);

        upload_image = (ImageView) findViewById(R.id.upload_photo);
        upload_image.setOnClickListener(this);

        mMRP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(mMRP.getText().toString())) {
                    int oldPrice = 0;
                    int newPrice = 0;

                    int mrp = Integer.parseInt(mMRP.getText().toString());

                    newPrice = mrp - (mrp * 10 / 100);
                    mNewPrice.setText("" + newPrice);

                    oldPrice = mrp - (mrp * 25 / 100);
                    mOldPrice.setText("" + oldPrice);
                }
            }
        });

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addBookAddBtn:
                if (verifyFields()) {
                    progressDialog.show();
                    upload_image_to_firebase();
                }
                break;

            case R.id.upload_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            image_uri = data.getData();
            upload_image.setImageURI(image_uri);
        }
    }

    public boolean verifyFields() {
        boolean result = true;

        mTitle.setError(null);
        mPublisher.setError(null);
        mAuthor.setError(null);
        mCourse.setError(null);
        mSem.setError(null);
        mMRP.setError(null);
        mNewPrice.setError(null);
        mOldPrice.setError(null);
        mAvlCopy.setError(null);

        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mPublisher.getText().toString())) {
            mPublisher.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mAuthor.getText().toString())) {
            mAuthor.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mCourse.getText().toString())) {
            mCourse.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mSem.getText().toString())) {
            mSem.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mMRP.getText().toString())) {
            mMRP.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mNewPrice.getText().toString())) {
            mNewPrice.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mOldPrice.getText().toString())) {
            mOldPrice.setError("This is required field.");
            result = false;
        }

        if (TextUtils.isEmpty(mAvlCopy.getText().toString())) {
            mAvlCopy.setError("This is required field.");
            result = false;
        }
        return result;
    }

    public void upload_image_to_firebase() {
        if (upload_image.getDrawable() == null) {
            image_source = "na";
            return;
        }

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("uploading image...");

        Bitmap bitmap = compressImage(image_uri);
        upload_image.setImageBitmap(bitmap);

        mStorageRef = FirebaseStorage.getInstance().getReference()
                .child("Books")
                .child(mCourse.getText().toString().toUpperCase())
                .child("sem_" + mSem.getText().toString()).child(mTitle.getText().toString().toUpperCase() + "_" + mAuthor.getText().toString().toUpperCase());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mStorageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                image_source = String.valueOf(downloadUrl);

                addBookDetailsToDatabase(mTitle.getText().toString(), mPublisher.getText().toString(), mAuthor.getText().toString(), mCourse.getText().toString(), mSem.getText().toString(), mMRP.getText().toString(), mNewPrice.getText().toString(), mOldPrice.getText().toString(), mAvlCopy.getText().toString());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    }
                });

    }

    public void addBookDetailsToDatabase(String title, String publisher, String author, String course, String sem, String mrp, String newPrice, String oldPrice, String avlCopy) {
        course = course.replace(".", "");
        course = course.replace(",", "");
        course = course.toUpperCase();


        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("adding book details to database...");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).child("title").setValue(title.toUpperCase());
        databaseReference.child(key).child("publisher").setValue(publisher.toUpperCase());
        databaseReference.child(key).child("author").setValue(author.toUpperCase());
        databaseReference.child(key).child("course").setValue(course.toUpperCase());
        databaseReference.child(key).child("sem").setValue(sem);
        databaseReference.child(key).child("priceMRP").setValue(mrp);
        databaseReference.child(key).child("priceNew").setValue(newPrice);
        databaseReference.child(key).child("priceOld").setValue(oldPrice);
        databaseReference.child(key).child("avlcopy").setValue(Integer.parseInt(avlCopy));
        databaseReference.child(key).child("key").setValue(key);
        databaseReference.child(key).child("soldcopy").setValue("0");
        databaseReference.child(key).child("src").setValue(image_source).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Book details successfully added to database", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    public Bitmap compressImage(Uri imageUri) {

        //String filePath = getRealPathFromURI(imageUri);
        String filePath = getPath(imageUri);

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        //Bitmap bmp = ((BitmapDrawable) upload_image.getDrawable()).getBitmap();

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FileOutputStream out = null;
        //String filename = getFilename();
        try {
            //out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            updatedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            upload_image.setImageBitmap(updatedBitmap);


        } finally {

        }

        return updatedBitmap;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public void setDiscount(String newBookDiscount, String oldBookDiscount) {
        SharedPreferences sharedPreferences = null;
        sharedPreferences = getSharedPreferences("discount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("new", newBookDiscount);
        editor.putString("old", oldBookDiscount);
        editor.commit();

        /*if ( sharedPreferences.getString("Name", null) == null) {
            mDelName.setText("");
        } else {
            mDelName.setText(sharedPreferences.getString("Name", null));
        }*/
    }

}
