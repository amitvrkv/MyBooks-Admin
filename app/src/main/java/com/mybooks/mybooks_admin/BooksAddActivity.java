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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BooksAddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 99;
    DatabaseReference databaseReference;
    StorageReference mStorageRef;
    Bitmap updatedBitmap;
    Spinner stage;
    String image_source = "na";
    Uri image_uri;
    /*fetch_price*/
    EditText fetch_price_mrp;
    EditText fetch_price_new_price;
    EditText fetch_price_old_price;
    TextView fetch_price_new_price_per;
    TextView fetch_price_old_price_per;
    SeekBar fetch_price_new_price_seekbar;
    SeekBar fetch_price_old_price_seekbar;
    Button fetch_price;
    RelativeLayout fetch_price_layout;
    private RelativeLayout book_details_form;
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
    private ProgressDialog progressDialog;

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_add);

        book_details_form = (RelativeLayout) findViewById(R.id.book_details_form);
        mTitle = (EditText) findViewById(R.id.addBookTitle);
        mPublisher = (EditText) findViewById(R.id.addBookPublisher);
        mAuthor = (EditText) findViewById(R.id.addBookAuthor);
        mCourse = (EditText) findViewById(R.id.addBookCourse);
        mCourse.setHint("Course");
        mSem = (EditText) findViewById(R.id.addBookSem);
        mSem.setHint("Semester");

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
                fetch_price_layout.setVisibility(View.VISIBLE);
                book_details_form.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*
                if (!TextUtils.isEmpty(mMRP.getText().toString())) {
                    int oldPrice = 0;
                    int newPrice = 0;

                    int mrp = Integer.parseInt(mMRP.getText().toString());

                    newPrice = mrp - (mrp * 10 / 100);
                    mNewPrice.setText("" + newPrice);

                    oldPrice = mrp - (mrp * 25 / 100);
                    mOldPrice.setText("" + oldPrice);
                }*/
            }
        });
        mMRP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fetch_price_layout.setVisibility(View.VISIBLE);
                book_details_form.setVisibility(View.GONE);
                return true;
            }
        });

        stage = (Spinner) findViewById(R.id.stage);
        final List<String> stageList = new ArrayList<String>();
        stageList.add("UG");
        stageList.add("PG");
        stageList.add("12th");
        stageList.add("School");
        ArrayAdapter<String> stageDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stageList);
        stageDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stage.setAdapter(stageDataAdapter);
        stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCourse.setEnabled(true);
                if (stageList.get(position).equals("UG") || stageList.get(position).equals("PG")) {
                    mCourse.setHint("Course");
                    mSem.setHint("Semester or Book Edition");
                } else if (stageList.get(position).equals("12th")) {
                    mCourse.setHint("Stream");
                    mSem.setHint("Year");
                } else if (stageList.get(position).equals("School")) {
                    mCourse.setText("School");
                    mCourse.setEnabled(false);
                    mSem.setHint("Class");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressDialog = new ProgressDialog(this);

        fetchPrice();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addBookAddBtn:
                if (verifyFields()) {
                    progressDialog.show();
                    try {
                        updatedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    upload_image_to_firebase();
                }
                break;

            case R.id.upload_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select product image"), PICK_IMAGE);
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

    public void fetchPrice() {
        fetch_price_layout = (RelativeLayout) findViewById(R.id.fetch_price_layout);
        fetch_price_mrp = (EditText) findViewById(R.id.fetch_price_mrp);
        fetch_price_new_price = (EditText) findViewById(R.id.fetch_price_new_price);
        fetch_price_old_price = (EditText) findViewById(R.id.fetch_price_old_price);
        fetch_price_new_price_per = (TextView) findViewById(R.id.fetch_price_new_price_per);
        fetch_price_old_price_per = (TextView) findViewById(R.id.fetch_price_old_price_per);
        fetch_price_new_price_seekbar = (SeekBar) findViewById(R.id.fetch_price_new_price_seekbar);
        fetch_price_old_price_seekbar = (SeekBar) findViewById(R.id.fetch_price_old_price_seekbar);
        fetch_price = (Button) findViewById(R.id.fetch_price);

        fetch_price_mrp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fetch_price_new_price.setText(fetch_price_mrp.getText().toString());
                fetch_price_old_price.setText(fetch_price_mrp.getText().toString());
                fetch_price_new_price_seekbar.setProgress(100);
                fetch_price_old_price_seekbar.setProgress(100);
            }
        });

        fetch_price_new_price_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (TextUtils.isEmpty(fetch_price_mrp.getText())) {
                    return;
                }

                int price = Integer.parseInt(fetch_price_mrp.getText().toString()) * progress / 100;
                fetch_price_new_price_per.setText("" + ( 100 - progress ) + "% off on MRP");
                fetch_price_new_price.setText(" " + price);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (TextUtils.isEmpty(fetch_price_mrp.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter MRP", Toast.LENGTH_SHORT).show();
                    fetch_price_new_price_seekbar.setProgress(100);
                }
            }
        });

        fetch_price_old_price_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (TextUtils.isEmpty(fetch_price_mrp.getText())) {
                    return;
                }

                int price = Integer.parseInt(fetch_price_mrp.getText().toString()) * progress / 100;
                fetch_price_old_price_per.setText("" + ( 100 - progress ) + "% off on MRP");
                fetch_price_old_price.setText(" " + price);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (TextUtils.isEmpty(fetch_price_mrp.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter MRP", Toast.LENGTH_SHORT).show();
                    fetch_price_old_price_seekbar.setProgress(100);
                }
            }
        });

        fetch_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fetch_price_mrp.getText()) || TextUtils.isEmpty(fetch_price_new_price.getText()) || TextUtils.isEmpty(fetch_price_old_price.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter MRP / New price or Old price", Toast.LENGTH_SHORT).show();
                    return;
                }
                mMRP.setText(fetch_price_mrp.getText().toString());
                mNewPrice.setText(fetch_price_new_price.getText().toString());
                mOldPrice.setText(fetch_price_old_price.getText().toString());

                fetch_price_layout.setVisibility(View.GONE);
                book_details_form.setVisibility(View.VISIBLE);
            }
        });
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

        String key = ( mTitle.getText().toString().trim() + "_" + mPublisher.getText().toString().trim() + "_" + mMRP.getText().toString().trim() ).replace(".", "").toUpperCase();
        mStorageRef = FirebaseStorage.getInstance().getReference()
                .child("Books")
                .child(key);

        //Bitmap bitmap = compressImage(String.valueOf(image_uri));

        Bitmap bitmap = updatedBitmap;

        upload_image.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();


        mStorageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                image_source = String.valueOf(downloadUrl);

                progressDialog.dismiss();
                addBookDetailsToDatabase(mTitle.getText().toString(), mPublisher.getText().toString(), mAuthor.getText().toString(), mCourse.getText().toString(), mSem.getText().toString(), mMRP.getText().toString(), mNewPrice.getText().toString(), mOldPrice.getText().toString(), mAvlCopy.getText().toString());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //progressDialog.setMessage("uploading image...\n" + progress + " completed");
                    }
                });

    }

    public void addBookDetailsToDatabase(final String title, String publisher, String author, String course, String sem, String mrp, String newPrice, String oldPrice, String avlCopy) {
        course = course.replace(".", "");
        course = course.replace(",", "");
        course = course.toUpperCase();

        final String key = ( title + "_" + publisher + "_" + mrp ).replace(".", "").toUpperCase() ;

        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("adding book details to database...");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("PRODUCT").child("PRODUCTS");
        databaseReference.child(key).child("f1").setValue("BOOK");
        databaseReference.child(key).child("f2").setValue(title.toUpperCase().trim());
        databaseReference.child(key).child("f3").setValue(publisher.toUpperCase().trim());
        databaseReference.child(key).child("f4").setValue(author.toUpperCase().trim());
        databaseReference.child(key).child("f5").setValue(course.toUpperCase().trim());
        databaseReference.child(key).child("f6").setValue(sem.trim());
        databaseReference.child(key).child("f7").setValue(mrp.trim());
        databaseReference.child(key).child("f8").setValue(newPrice.trim());
        databaseReference.child(key).child("f9").setValue(oldPrice.trim());
        databaseReference.child(key).child("f10").setValue(avlCopy.toString().trim());
        databaseReference.child(key).child("f11").setValue(key);
        databaseReference.child(key).child("f12").setValue("0");    //Sold copy
        databaseReference.child(key).child("f13").setValue(image_source).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Book details successfully added to database", Toast.LENGTH_SHORT).show();
                    finish();
                    //addProduactUnderParent(key, title);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }


/*
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
*/

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

    /////////////////////////////

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

    public Bitmap compressImage(String imagePath) {
        final float maxHeight = 1280.0f;
        final float maxWidth = 1280.0f;

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

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
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
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
        if (bmp != null) {
            bmp.recycle();
        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FileOutputStream out = null;

        //String filepath = MainActivity.imageFilePath;//getFilename();

        //try {
        //new File(imageFilePath).delete();
        //out = new FileOutputStream(filepath);

        //write the compressed bitmap at the destination specified by filename.
        //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        //} catch (FileNotFoundException e) {
        //   e.printStackTrace();
        //}
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        updatedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        upload_image.setImageBitmap(updatedBitmap);

        return updatedBitmap;
    }

}