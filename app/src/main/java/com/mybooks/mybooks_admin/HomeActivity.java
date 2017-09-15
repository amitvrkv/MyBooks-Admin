package com.mybooks.mybooks_admin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        hideAllNavMenu();
        setPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.manageBooksMenu:
                if (getPermission(getString(R.string.per_addBook)).equals("1"))
                    startActivity(new Intent(this, BooksAddActivity.class));
                else
                    Toast.makeText(getApplicationContext(), "Unauthorized access.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.manageOrdersMenu:
                if (getPermission(getString(R.string.per_manageOrder)).equals("1"))
                    startActivity(new Intent(this, OrderActivity.class));
                else
                    Toast.makeText(getApplicationContext(), "Unauthorized access.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.addExeMenu:
                if (getPermission(getString(R.string.per_addExe)).equals("1"))
                    startActivity(new Intent(this, AddExecutive.class));
                else
                    Toast.makeText(getApplicationContext(), "Unauthorized access.", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setPermission() {
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS PERMISSION");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS PERMISSION(addExe VARCHAR, addBook VARCHAR,updateBook VARCHAR, manageOrder VARCHAR, orderInProcess VARCHAR,outForDelivery VARCHAR,delivered VARCHAR,deleteOrder VARCHAR,orderRollBack VARCHAR);");
        sqLiteDatabase.execSQL("INSERT INTO PERMISSION VALUES('0', '0','0','0','0','0','0', '0', '0');");

        final TextView permission_alert = (TextView) findViewById(R.id.permission_alert);
        permission_alert.setText("Please wait.  Loading permission...");
        permission_alert.setVisibility(View.VISIBLE);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Admin").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "*")).child("permission").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ModelClassPermission modelClassPermission = dataSnapshot.getValue(ModelClassPermission.class);

                /*Menu 1*/
                updateDatabase("addBook", modelClassPermission.getAddBook());
                if (modelClassPermission.getAddBook().equals("1")) {
                    showNavMenu(R.id.manageBooksMenu);
                } else {
                    hideNavMenu(R.id.manageBooksMenu);
                }
                /*Menu 2*/
                updateDatabase("manageOrder", modelClassPermission.getManageOrder());
                if (modelClassPermission.manageOrder.equals("1")) {
                    showNavMenu(R.id.manageOrdersMenu);
                } else {
                    hideNavMenu(R.id.manageOrdersMenu);
                }
                /*Menu 3*/
                updateDatabase("addExe", modelClassPermission.getAddExe());
                if (modelClassPermission.addExe.equals("1")) {
                    showNavMenu(R.id.addExeMenu);
                } else {
                    hideNavMenu(R.id.addExeMenu);
                }

                updateDatabase("updateBook", modelClassPermission.getUpdateBook());
                updateDatabase("orderInProcess", modelClassPermission.getOrderInProcess());
                updateDatabase("outForDelivery", modelClassPermission.getOutForDelivery());
                updateDatabase("delivered", modelClassPermission.getDelivered());
                updateDatabase("deleteOrder", modelClassPermission.getDeleteOrder());
                updateDatabase("orderRollBack", modelClassPermission.getOrderRollBack());

                permission_alert.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateDatabase(String column, String value) {
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        sqLiteDatabase.execSQL("UPDATE PERMISSION SET " + column + "='" + value + "'");
    }

    public String getPermission(String column) {
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getString(R.string.database_path), null);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from PERMISSION", null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public void hideAllNavMenu() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.manageBooksMenu).setVisible(false);
        nav_menu.findItem(R.id.manageOrdersMenu).setVisible(false);
        nav_menu.findItem(R.id.addExeMenu).setVisible(false);
    }

    public void showNavMenu(int menuID){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(menuID).setVisible(true);
    }

    public void hideNavMenu(int menuID){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(menuID).setVisible(false);
    }
}
