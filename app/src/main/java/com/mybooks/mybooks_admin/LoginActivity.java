package com.mybooks.mybooks_admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView mUsername;
    private TextView mPassword;
    private TextView mKey;
    private TextView mSign_inBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (TextView) findViewById(R.id.username_sign_in);
        mPassword = (TextView) findViewById(R.id.password_sign_in);
        mKey = (TextView) findViewById(R.id.key_sign_in);
        mSign_inBtn = (TextView) findViewById(R.id.signBtn);


        mSign_inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setError(null);
                mPassword.setError(null);
                mKey.setError(null);

                if (TextUtils.isEmpty(mUsername.getText().toString())) {
                    mUsername.setError("This field is required");
                    return;
                } else if (TextUtils.isEmpty(mPassword.getText().toString())) {
                    mPassword.setError("This field is required");
                    return;
                } else if (TextUtils.isEmpty(mKey.getText().toString())) {
                    mKey.setError("This field is required");
                    return;
                }
                signIn(mUsername.getText().toString(), mPassword.getText().toString(), mKey.getText().toString());
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {

                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signIn(String username, String password, final String key) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(getApplicationContext(), "logged", Toast.LENGTH_SHORT).show();
                    verifyKey(key);
                }
            }
        });
    }

    public void verifyKey(final String key) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Admin")
                .child(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replace(".", "*"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dataBasekey = String.valueOf(dataSnapshot.child("key").getValue());
                if (key.equals(dataBasekey)) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
