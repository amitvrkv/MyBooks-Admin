package com.mybooks.mybooks_admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        recyclerView = (RecyclerView) findViewById(R.id.databaseRecyclerView);
        keyList = new ArrayList<>();
        valueList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("admin@mybooks*com");
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


                    Toast.makeText(getApplicationContext(), ">> " + key + "\n>> " + value + "\n" + keyList.size() , Toast.LENGTH_SHORT).show();
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

}

