package com.mybooks.mybooks_admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Individual_book_details extends AppCompatActivity {

    private ImageView mBook_image, mBackButton;
    private TextView mBookTitle, mBookPublisher, mBookAuthor, mBookCourse, mBookSem, mBookMRP, mBookNewPrice, mBookOldPrice;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_book_details);

        setToolbar();

        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");

        mBookTitle = (TextView) findViewById(R.id.BookTitle);
        mBookPublisher = (TextView) findViewById(R.id.BookPublisher);
        mBookAuthor = (TextView) findViewById(R.id.BookAuthor);
        mBookCourse = (TextView) findViewById(R.id.BookCourse);
        mBookSem = (TextView) findViewById(R.id.BookSem);
        mBookMRP = (TextView) findViewById(R.id.BookMRP);
        mBookNewPrice = (TextView) findViewById(R.id.BookNewPrice);
        mBookOldPrice = (TextView) findViewById(R.id.BookOldPrice);
        mBook_image = (ImageView) findViewById(R.id.book_image);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelProductList modelProductList = dataSnapshot.getValue(ModelProductList.class);
                mBookTitle.setText("Book Title: " + capitalizeEveryWord(modelProductList.getF2()));
                mBookPublisher.setText("Publisher: " + capitalizeEveryWord(modelProductList.getF3()));
                mBookAuthor.setText("Author: " + capitalizeEveryWord(modelProductList.getF4()));
                mBookCourse.setText("Course: " + modelProductList.getF5());
                mBookSem.setText("Semester: " + modelProductList.getF6());
                mBookMRP.setText("Book MRP: \u20B9 " + modelProductList.getF7());
                mBookNewPrice.setText("New Book Price: \u20B9 " + modelProductList.getF8());
                mBookOldPrice.setText("Old Book Price: \u20B9 " + modelProductList.getF9());

                if (modelProductList.getF13().equals("na"))
                    mBook_image.setVisibility(View.GONE);
                else
                    //Picasso.with(getApplicationContext()).load(modelProductList.getF13()).into(mBook_image);
                    Glide.with(getApplicationContext()).load(modelProductList.getF13())
                            .error(R.drawable.no_image_available)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mBook_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String capitalizeEveryWord(String str) {

        if (str == null)
            return "";

        System.out.println(str);
        StringBuffer stringbf = new StringBuffer();
        Matcher m = Pattern.compile(
                "([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);

        while (m.find()) {
            m.appendReplacement(
                    stringbf, m.group(1).toUpperCase() + m.group(2).toLowerCase());
        }
        return m.appendTail(stringbf).toString();
    }
}
