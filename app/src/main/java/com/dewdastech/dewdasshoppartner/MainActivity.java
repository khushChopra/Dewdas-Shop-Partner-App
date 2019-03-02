package com.dewdastech.dewdasshoppartner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // CONSTANTS
    protected int RC_SIGN_IN = 894;

    @Override
    protected void onPause() {
        super.onPause();
        myFirebaseAuth.removeAuthStateListener(myListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myFirebaseAuth.addAuthStateListener(myListener);
    }

    // Views
    protected BottomNavigationView bottom_navigation;
    protected FrameLayout container;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Views initilisation
        bottom_navigation = findViewById(R.id.bottom_navigation);
        container = findViewById(R.id.fragment_container);

        // Firebase init
        myFirebaseAuth = FirebaseAuth.getInstance();
        myListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    createSignInIntent();
                }
                else{
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
            }
        };




        // initial fragment option
        getSupportFragmentManager().beginTransaction().replace(container.getId(),new Details()).commit();

        // Bottom switcher
        bottom_navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        String title = menuItem.getTitle().toString().toLowerCase();

                        if(title.equals("details")){
                            getSupportFragmentManager().beginTransaction().replace(container.getId(),new Details()).commit();
                        }
                        else if(title.equals("inventory")){
                            getSupportFragmentManager().beginTransaction().replace(container.getId(),new Inventory()).commit();
                        }
                        return true;
                    }
                }
        );




    }


    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                myUser = user;
            }
            else {
                Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
                createSignInIntent();
            }
        }
    }

}
