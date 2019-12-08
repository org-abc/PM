package com.kondie.pocketmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by kondie on 2019/01/14.
 */

public class SignIn extends AppCompatActivity {

    EditText email, password;
    TextView forgotPassword;
    public static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        activity = SignIn.this;

        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_pass);
        TextView signInButton = findViewById(R.id.sign_in_button);
        TextView createAcc = findViewById(R.id.new_account_button);
        forgotPassword = findViewById(R.id.forgot_pass_butt);

        signInButton.setOnClickListener(signItIn);
        createAcc.setOnClickListener(gotoSignUp);
        forgotPassword.setOnClickListener(showDialog);
    }

    private View.OnClickListener showDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog emailDialog = new Dialog(activity);
            emailDialog.setContentView(R.layout.email_dialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(emailDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final EditText dialogEmail = emailDialog.findViewById(R.id.forgot_client_email);
            Button dialogSubmitButt = emailDialog.findViewById(R.id.submit_email);

            dialogSubmitButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!dialogEmail.getText().toString().equals("")){
                        new SendCode().execute(dialogEmail.getText().toString(), "signin");
                    }
                    else{
                        Toast.makeText(activity, "You must enter an email", Toast.LENGTH_SHORT);
                    }
                }
            });

            emailDialog.show();
            emailDialog.getWindow().setAttributes(lp);
        }
    };

    View.OnClickListener gotoSignUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent toSignUpIntent = new Intent(activity, SignUp.class);
            startActivity(toSignUpIntent);
        }
    };

    View.OnClickListener signItIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!email.getText().toString().equalsIgnoreCase("") && !password.getText().toString().equalsIgnoreCase(""))
            {
                new SubmitSignInForm().execute(email.getText().toString(), password.getText().toString(), "login");
            }
            else
            {
                Toast.makeText(activity, "Fill in all the details", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
