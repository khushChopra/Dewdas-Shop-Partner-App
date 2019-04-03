package com.dewdastech.dewdasshoppartner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class enterNumberOfStock extends AppCompatActivity {

    // region Varaiables
    protected TextView messageTextView;
    protected EditText entryEditText;
    protected Button numberSubmitButton;

    // extra info
    protected String updatePath;
    protected int currentCount;
    protected String displayMessage;

    // Firebase variables
    protected DatabaseReference myMainRef;

    // endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_number_of_stock);

        viewsInit();

        intentInfoImport();

        myMainRef = FirebaseDatabase.getInstance().getReference();
    }

    private void intentInfoImport(){
        updatePath = getIntent().getStringExtra("updatePath");
        displayMessage = getIntent().getStringExtra("displayMessage");
        currentCount = getIntent().getIntExtra("currentCount",0);

    }

    private void viewsInit(){
        messageTextView = findViewById(R.id.messageTextView);
        messageTextView.setText(displayMessage);
        entryEditText = findViewById(R.id.entryEditText);
        numberSubmitButton = findViewById(R.id.numberSubmitButton);
        numberSubmitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myMainRef.child(updatePath).setValue(Integer.parseInt(entryEditText.getText().toString())+currentCount);
                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra("goto","inventory");
                        startActivity(i);
                    }
                }
        );
    }
}
