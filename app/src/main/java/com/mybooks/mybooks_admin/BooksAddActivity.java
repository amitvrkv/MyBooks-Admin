package com.mybooks.mybooks_admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    DatabaseReference databaseReference;

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

        mAddBtn = (TextView) findViewById(R.id.addBookAddBtn);
        mAddBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookAddBtn:

                //if( verifyFields())
                    addBookDetailsToDatabase(mTitle.getText().toString(), mAuthor.getText().toString(), mCourse.getText().toString(), mSem.getText().toString(), mMRP.getText().toString(), mNewPrice.getText().toString(), mOldPrice.getText().toString(), mAvlCopy.getText().toString());

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

        if(TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mAuthor.getText().toString())) {
            mAuthor.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mCourse.getText().toString())) {
            mCourse.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mSem.getText().toString())) {
            mSem.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mMRP.getText().toString())) {
            mMRP.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mNewPrice.getText().toString())) {
            mNewPrice.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mOldPrice.getText().toString())) {
            mOldPrice.setError("This is required field.");
            result = false;
        }

        if(TextUtils.isEmpty(mAvlCopy.getText().toString())) {
            mAvlCopy.setError("This is required field.");
            result = false;
        }
        return result;
    }

    public void addBookDetailsToDatabase(String title, String author, String course, String sem, String mrp, String newPrice, String oldPrice, String avlCopy) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books").child(course);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).child("title").setValue(title);
        databaseReference.child("author").setValue(author);
        databaseReference.child("course").setValue(course);
        databaseReference.child("sem").setValue(sem);
        databaseReference.child("priceMRP").setValue(mrp);
        databaseReference.child("priceNew").setValue(newPrice);
        databaseReference.child("priceOld").setValue(oldPrice);
        databaseReference.child("avlcopy").setValue(avlCopy);
        databaseReference.child("key").setValue(key);
        databaseReference.child("soldcopy").setValue("0");
        databaseReference.child("src").setValue("na").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Book successfully added to database", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
