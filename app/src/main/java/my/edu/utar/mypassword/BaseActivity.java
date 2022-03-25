package my.edu.utar.mypassword;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity{

    boolean resumeFlag = false;
    boolean pauseFlag = true;
    Intent intentResume;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyPassword) getApplication()).registerSessionListener(this);
        ((MyPassword) getApplication()).startUserSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pauseFlag) {
            resumeFlag = true;
            intentResume = new Intent(this, Login.class);
            ((MyPassword) getApplication()).cancelTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pauseFlag = true;
        onUserInteraction();
        if (resumeFlag) {
            resumeFlag = false;
            startActivity(intentResume);
            finish();
        }
    }

    @Override
    public void onUserInteraction() {
        ((MyPassword) getApplication()).startUserSession();
    }

    public void autoLock() {
        finish();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
