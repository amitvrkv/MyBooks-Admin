package com.mybooks.mybooks_admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Database extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> keyList;
    List<String> valueList;
    List<String> childList;

    TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        path = (TextView) findViewById(R.id.path);

        recyclerView = (RecyclerView) findViewById(R.id.databaseRecyclerView);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        childList.add(keyList.get(position));
                        setDatabase();
                    }
                })
        );

        keyList = new ArrayList<>();
        valueList = new ArrayList<>();
        childList = new ArrayList<>();

        setDatabase();
    }

    public void setDatabase() {

        String databasePath = "ROOT";

        keyList.clear();
        valueList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        for (String child : childList) {
            databaseReference = databaseReference.child(child);
            databasePath = databasePath + ">" + child;
        }
        path.setText(databasePath);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    String value = String.valueOf(dataSnapshot1.getValue());
                    if (value.startsWith("{"))
                        value = "";

                    keyList.add(key);
                    valueList.add(value);
                    //Toast.makeText(getApplicationContext(), ">> " + key + "\n>> " + value + "\n" + keyList.size() , Toast.LENGTH_SHORT).show();
                }

                DatabaseCustomAdapter databaseCustomAdapter = new DatabaseCustomAdapter(keyList, valueList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Database.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(databaseCustomAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (childList.size() <= 0)
            super.onBackPressed();
        else {
            childList.remove(childList.size() - 1);
            setDatabase();
        }
    }
}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}

