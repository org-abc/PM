package com.kondie.pocketmechanic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VerifAct extends AppCompatActivity {

    private EditText code;
    private TextView resendCodeButt;
    private Button sendCodeButt;
    public static Activity activity;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verif_act);
        activity = this;
        prefs = getSharedPreferences("PM", MODE_PRIVATE);

        code = findViewById(R.id.verif_code);
        sendCodeButt = findViewById(R.id.submit_code);
        resendCodeButt = findViewById(R.id.resend_verif_code);

        sendCodeButt.setOnClickListener(sendCode);
        resendCodeButt.setOnClickListener(reSend);
    }

    private View.OnClickListener reSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new SendCode().execute(prefs.getString("email", ""), "verif");
        }
    };

    private View.OnClickListener sendCode = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new VerifyUser().execute(code.getText().toString(), getIntent().getExtras().getString("email"), getIntent().getExtras().getString("pass"));
        }
    };
}
