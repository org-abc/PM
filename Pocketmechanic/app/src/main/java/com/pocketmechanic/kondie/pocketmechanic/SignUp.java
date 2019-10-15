package com.pocketmechanic.kondie.pocketmechanic;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kondie on 2019/01/14.
 */

public class SignUp extends AppCompatActivity {

    EditText username, fname, lname, email, phone, password, conf_pass, car;
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        activity = SignUp.this;

        username = (EditText) findViewById(R.id.client_username);
        fname = (EditText) findViewById(R.id.client_fname);
        lname = (EditText) findViewById(R.id.client_lname);
        email = (EditText) findViewById(R.id.client_email);
        phone = (EditText) findViewById(R.id.client_phone);
        car = (EditText) findViewById(R.id.client_car);
        password = (EditText) findViewById(R.id.client_pass);
        conf_pass = (EditText) findViewById(R.id.client_conf_pass);
        TextView signUpButton = (TextView) findViewById(R.id.sign_up_button);
        Toolbar tb = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(tb);

        signUpButton.setOnClickListener(signItUp);
    }

    View.OnClickListener signItUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!username.getText().toString().equalsIgnoreCase("") &&
                    !fname.getText().toString().equalsIgnoreCase("") &&
                    !lname.getText().toString().equalsIgnoreCase("") &&
                    !phone.getText().toString().equalsIgnoreCase("") &&
                    !password.getText().toString().equalsIgnoreCase(""))
            {
                if (password.getText().toString().equalsIgnoreCase(conf_pass.getText().toString()))
                {
                    new SubmitSignUpForm().execute(username.getText().toString(),
                            fname.getText().toString(),
                            lname.getText().toString(),
                            email.getText().toString(),
                            phone.getText().toString(),
                            car.getText().toString(),
                            password.getText().toString());
                }
                else {
                    Toast.makeText(activity, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
