package my.edu.utar.mypassword;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;

public class MyPassword extends Application {

    Timer timer;
    String MYPREFERENCES = "secret_shared_prefs" ;
    String AUTOLOCK = "AutoLock";
    SharedPreferences sharedpreferences;
    String masterKeyAlias;

    private BaseActivity baseActivity;

    public void startUserSession() {
        cancelTimer();
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
        String autolock = sharedpreferences.getString(AUTOLOCK, "never");
        if (!autolock.equals("never")) {
            long delay = Long.parseLong(autolock)*1000; // Testing
//            long delay = Long.parseLong(autolock)*60*1000;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    baseActivity.autoLock();
                }
            }, delay);
        }
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void registerSessionListener(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }
}
