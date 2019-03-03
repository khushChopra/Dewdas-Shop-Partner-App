package com.dewdastech.dewdasshoppartner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    // CONSTANTS
    protected int RC_SIGN_IN = 894;

    // Views
    protected BottomNavigationView bottom_navigation;
    protected FrameLayout container;
    protected Toolbar main_toolbar;
    protected ActionBar main_actionbar;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views initilisation
        viewsInit();

        // Firebase init
        firebaseInit();

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

    private void viewsInit(){
        bottom_navigation = findViewById(R.id.bottom_navigation);
        container = findViewById(R.id.fragment_container);
        main_toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);
    }

    private void firebaseInit(){
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
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.header_logo)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menu_inflator = getMenuInflater();
        menu_inflator.inflate(R.menu.app_bar_menu,menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout_button){
            myFirebaseAuth.removeAuthStateListener(myListener);
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "You are logged out, you can exit app", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }

}
