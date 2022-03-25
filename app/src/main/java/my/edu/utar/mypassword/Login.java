package my.edu.utar.mypassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Login extends AppCompatActivity {

    String MYPREFERENCES = "secret_shared_prefs" ;
    String MASTER = "Master";
    String HINT = "Hint";
    SharedPreferences sharedpreferences;
    String masterKeyAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedpreferences = EncryptedSharedPreferences.create(
                    MYPREFERENCES,
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String master = sharedpreferences.getString(MASTER, null);
        boolean setMaster = master == null;

        if (setMaster) {
            setContentView(R.layout.activity_first_login);
            TextInputLayout enterPasswordLayout = findViewById(R.id.enter_password_layout);
            TextInputLayout confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
            TextInputEditText enterPassword = findViewById(R.id.enter_password);
            TextInputEditText confirmPassword = findViewById(R.id.confirm_password);
            EditText hintText = findViewById(R.id.hint);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String p1 = enterPassword.getText().toString();
                    String p2 = confirmPassword.getText().toString();

                    if (!p1.isEmpty()) {
                        enterPasswordLayout.setErrorEnabled(false);
                    }

                    if (!p2.isEmpty()) {
                        confirmPasswordLayout.setErrorEnabled(false);
                    }

                    if (!p1.equals(p2)) {
                        confirmPasswordLayout.setErrorEnabled(true);
                        confirmPasswordLayout.setError("Password not match");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };
            enterPassword.addTextChangedListener(textWatcher);
            confirmPassword.addTextChangedListener(textWatcher);

            findViewById(R.id.first_login_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String p1 = enterPassword.getText().toString();
                    String p2 = confirmPassword.getText().toString();
                    if (!p1.isEmpty() && !p1.trim().isEmpty() && !p2.isEmpty() && !p2.trim().isEmpty()) {
                        if (p1.equals(p2)) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(MASTER, String.valueOf(confirmPassword.getText()));
                            editor.putString(HINT, hintText.getText().toString());
                            editor.commit();
                            Intent intent = new Intent(Login.this, Main.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Password not match.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (p1.isEmpty() && p1.trim().isEmpty()) {
                            enterPasswordLayout.setErrorEnabled(true);
                            enterPasswordLayout.setError("Required");
                        }

                        if (p2.isEmpty() && p2.trim().isEmpty()) {
                            confirmPasswordLayout.setErrorEnabled(true);
                            confirmPasswordLayout.setError("Required");
                        }
                    }
                }
            });
        } else {
            setContentView(R.layout.activity_login);
            TextView loginHint = findViewById(R.id.login_hint);
            TextInputLayout loginPasswordLayout = findViewById(R.id.login_password_layout);
            TextInputEditText loginPassword = findViewById(R.id.login_password);

            String hint = sharedpreferences.getString(HINT, "");
            if (!hint.isEmpty() && !hint.trim().isEmpty()) {
                loginHint.setText( "Hint: " + hint);
                loginHint.setVisibility(View.VISIBLE);
            }

            loginPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    loginPasswordLayout.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String password = loginPassword.getText().toString();
                    if (password.isEmpty() && password.trim().isEmpty()) {
                        loginPasswordLayout.setErrorEnabled(true);
                        loginPasswordLayout.setError("Required");
                    } else if (password.equals(master)) {
                        Intent intent = new Intent(Login.this, Main.class);
                        startActivity(intent);
                        finish();
                    } else {
                        loginPasswordLayout.setErrorEnabled(true);
                        loginPasswordLayout.setError("Wrong password");
                    }
                }
            });
        }
    }
}