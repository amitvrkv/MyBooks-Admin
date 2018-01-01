package com.mybooks.mybooks_admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddExecutive extends AppCompatActivity implements View.OnClickListener {

    private EditText mexe_username, mexe_device_id;
    private TextView mexe_create_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_executive);

        mexe_username = (EditText) findViewById(R.id.exe_username);
        mexe_device_id = (EditText) findViewById(R.id.exe_device_id);
        mexe_create_acc = (TextView) findViewById(R.id.exe_create_acc);
        mexe_create_acc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mexe_create_acc.getId() == v.getId()) {
            if (verifyFields()) {
                addExeDeviceDetails();
            }
        }
    }

    public boolean verifyFields() {
        mexe_username.setError(null);
        mexe_device_id.setError(null);

        boolean result = true;

        if (TextUtils.isEmpty(mexe_username.getText())) {
            mexe_username.setError("This field is required");
            result = false;
        } else if (!(mexe_username.getText().toString().contains("@") || mexe_username.getText().toString().contains("@"))) {
            mexe_username.setError("Invalid email id");
            result = false;
        }

        if (TextUtils.isEmpty(mexe_device_id.getText())) {
            mexe_device_id.setError("This field is required");
            result = false;
        }

        return result;
    }

    public void addExeDeviceDetails() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Admin").child(mexe_username.getText().toString().replace(".", "*")).child("key").setValue(mexe_device_id.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    addPermission();
                else
                    Toast.makeText(getApplicationContext(), "Failed to created account", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addPermission() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child(mexe_username.getText().toString().replace(".", "*")).child("permission");
        databaseReference.child("addExe").setValue("0");
        databaseReference.child("addBook").setValue("0");
        databaseReference.child("updateBook").setValue("0");
        databaseReference.child("manageOrder").setValue("0");
        databaseReference.child("orderInProcess").setValue("0");
        databaseReference.child("outForDelivery").setValue("0");
        databaseReference.child("delivered").setValue("0");
        databaseReference.child("deleteOrder").setValue("0");
        databaseReference.child("orderRollBack").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_LONG).show();
                    //FirebaseAuth.getInstance().signOut();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to created account", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
