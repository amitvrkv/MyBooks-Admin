package com.mybooks.mybooks_admin;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BooksAddActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTitle;
    private EditText mAuthor;
    private EditText mCourse;
    private EditText mSem;
    private EditText mMRP;
    private EditText mNewPrice;
    private EditText mOldPrice;
    private EditText mAvlCopy;
    private TextView mAddBtn;

    private ImageView mBackBtn;

    DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_add);

        mTitle = (EditText) findViewById(R.id.addBookTitle);
        mAuthor = (EditText) findViewById(R.id.addBookAuthor);
        mCourse = (EditText) findViewById(R.id.addBookCourse);
        mSem = (EditText) findViewById(R.id.addBookSem);

        mMRP = (EditText) findViewById(R.id.addBookMrp);

        mNewPrice = (EditText) findViewById(R.id.addBookNewPrice);
        mOldPrice = (EditText) findViewById(R.id.addBookOldPrice);
        mAvlCopy = (EditText) findViewById(R.id.addBookAvlCopy);

        mBackBtn = (ImageView) findViewById(R.id.addBookBackBtn);
        mBackBtn.setOnClickListener(this);

        mAddBtn = (TextView) findViewById(R.id.addBookAddBtn);
        mAddBtn.setOnClickListener(this);

        mMRP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( ! TextUtils.isEmpty(mMRP.getText().toString())) {
                    int oldPrice = 0;
                    int newPrice = 0;

                    int mrp = Integer.parseInt(mMRP.getText().toString());

                    newPrice = mrp - (mrp * 15 / 100);
                    mNewPrice.setText("" + newPrice);

                    oldPrice = mrp - (mrp * 30 / 100);
                    mOldPrice.setText("" + oldPrice);
                }
            }
        });

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookBackBtn:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;

            case R.id.addBookAddBtn:
                if (verifyFields()) {
                    progressDialog.show();
                    addBookDetailsToDatabase(mTitle.getText().toString(), mAuthor.getText().toString(), mCourse.getText().toString(), mSem.getText().toString(), mMRP.getText().toString(), mNewPrice.getText().toString(), mOldPrice.getText().toString(), mAvlCopy.getText().toString());
                }
                break;
        }
    }

    public boolean verifyFields() {
        boolean result = true;

        mTitle.setError(null);
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

    public void addBookDetailsToDatabase(String title, String author, String course, String sem, String mrp, String newPrice, String oldPrice, String avlCopy) {
        course = course.replace(".", "");
        course = course.replace(",", "");


        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("adding book details to database...");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).child("title").setValue(title);
        databaseReference.child(key).child("author").setValue(author);
        databaseReference.child(key).child("course").setValue(course);
        databaseReference.child(key).child("sem ").setValue(sem);
        databaseReference.child(key).child("priceMRP").setValue(mrp);
        databaseReference.child(key).child("priceNew").setValue(newPrice);
        databaseReference.child(key).child("priceOld").setValue(oldPrice);
        databaseReference.child(key).child("avlcopy").setValue(avlCopy);
        databaseReference.child(key).child("key").setValue(key);
        databaseReference.child(key).child("soldcopy").setValue("0");
        databaseReference.child(key).child("src").setValue("na").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Book details successfully added to database", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
