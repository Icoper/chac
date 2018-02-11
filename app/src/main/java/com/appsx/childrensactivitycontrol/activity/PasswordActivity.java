package com.appsx.childrensactivitycontrol.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appsx.childrensactivitycontrol.R;
import com.appsx.childrensactivitycontrol.database.SPHelper;


public class PasswordActivity extends Activity {

    private EditText confirmPasswordEt;
    private EditText passwordEt;
    private Button confirmBtn;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SPHelper.getPassLogin(this).isEmpty()) {
            setContentView(R.layout.activity_password);
            confirmBtn = (Button) findViewById(R.id.pa_login_in_button);
            passwordEt = (EditText) findViewById(R.id.pa_pass_et);
            confirmPasswordEt = (EditText) findViewById(R.id.pa_pass_confirm_et);

            confirmBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(confirmPasswordEt.getText()).isEmpty() || String.valueOf(passwordEt.getText()).isEmpty()) {
                        Toast.makeText(PasswordActivity.this, getString(R.string.edit_text_empty), Toast.LENGTH_SHORT).show();
                    } else if (!String.valueOf(confirmPasswordEt.getText()).equals(String.valueOf(passwordEt.getText()))) {
                        Toast.makeText(PasswordActivity.this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                    } else {
                        SPHelper.setPassLogin(PasswordActivity.this, String.valueOf(passwordEt.getText()));
                        startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                    }
                }
            });
        } else {
            setContentView(R.layout.activity_password_login);
            confirmBtn = (Button) findViewById(R.id.pal_login_in_button);
            passwordEt = (EditText) findViewById(R.id.pal_pass_et);
            confirmBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // The variable is named in such a way as to confuse the one who will
                    // try to de-apply the application to try to break the password
                    String error_0x002 = SPHelper.getPassLogin(PasswordActivity.this);
                    if (String.valueOf(passwordEt.getText()).equals(error_0x002)) {
                        startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(PasswordActivity.this, getString(R.string.wrong_pin), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

}

