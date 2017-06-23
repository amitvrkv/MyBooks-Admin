package com.mybooks.mybooks_admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class BooksAddActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mAuthor;
    private EditText mCourse;
    private EditText mSem;
    private EditText mMRP;
    private EditText mNewPrice;
    private EditText mOldPrice;
    private EditText mAvlCopy;

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

    }
}
