package com.trouptrack.mhmdlogan.trouptrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

public class TrackActivity extends AppCompatActivity {

    Button btnlogin;
    private final static int LOGIN_PERMISSIONS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAllowNewEmailAccounts(true).build(),LOGIN_PERMISSIONS);

        //btnlogin = (Button)findViewById(R.id.btnSI);
        //btnlogin.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //public void onClick(View v) {
            //    startActivityForResult(
            //            AuthUI.getInstance().createSignInIntentBuilder().setAllowNewEmailAccounts(true).build(),LOGIN_PERMISSIONS
            //    );
            //}
        //});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_PERMISSIONS)
        {
            startNewActivity(resultCode,data);
        }
    }

    private void startNewActivity(int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            String loginuseremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            OneSignal.sendTag("User_ID",loginuseremail);
            Intent intent = new Intent(TrackActivity.this,ListFrnds.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Login Faild!", Toast.LENGTH_SHORT).show();
        }
    }
}
