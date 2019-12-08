package com.kondie.pocketmechanic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    private EditText newPass, confNewPass, code;
    private Button submitButt;
    TextView reSendCode;
    public static Activity activity;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        activity = this;
        prefs = getSharedPreferences("PM", MODE_PRIVATE);

        newPass = findViewById(R.id.new_client_pass);
        confNewPass = findViewById(R.id.client_conf_new_pass);
        code = findViewById(R.id.new_pass_verif_code);
        submitButt = findViewById(R.id.submit_new_pass);
        reSendCode = findViewById(R.id.resend_forgot_code);

        submitButt.setOnClickListener(changePass);
        reSendCode.setOnClickListener(reSend);
    }

    private View.OnClickListener reSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new SendCode().execute(prefs.getString("email", ""), "forgot");
        }
    };

    private View.OnClickListener changePass = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!code.getText().toString().equals("")) {
                if (newPass.getText().toString().length() >= 6) {
                    if (newPass.getText().toString().equals(confNewPass.getText().toString())) {
                        new ChangePassword().execute(code.getText().toString(), newPass.getText().toString());
                    } else {
                        Toast.makeText(activity, "The password doesn't match", Toast.LENGTH_SHORT);
                    }
                } else {
                    Toast.makeText(activity, "The password must be a minimum of 6 character", Toast.LENGTH_SHORT);
                }
            }else{
                Toast.makeText(activity, "You didn't enter the code", Toast.LENGTH_SHORT);
            }
        }
    };
}
