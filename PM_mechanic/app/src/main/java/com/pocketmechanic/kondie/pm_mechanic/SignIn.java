package com.pocketmechanic.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kondie on 2019/01/14.
 */

public class SignIn extends AppCompatActivity {

    EditText username, password;
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        activity = SignIn.this;

        username = (EditText) findViewById(R.id.sign_in_username);
        password = (EditText) findViewById(R.id.sign_in_pass);
        TextView signInButton = (TextView) findViewById(R.id.sign_in_button);
        Toolbar tb = (Toolbar) findViewById(R.id.sign_in_toolbar);
        setSupportActionBar(tb);

        signInButton.setOnClickListener(signItIn);
    }

    View.OnClickListener signItIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!username.getText().toString().equalsIgnoreCase("") && !password.getText().toString().equalsIgnoreCase(""))
            {
                new SubmitSignInForm().execute(username.getText().toString(), password.getText().toString(), "login");
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
