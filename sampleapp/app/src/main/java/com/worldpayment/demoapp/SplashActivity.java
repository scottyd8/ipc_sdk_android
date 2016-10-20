package com.worldpayment.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView splshIV;
    TextView movingTV;
    private static AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splshIV = (ImageView) findViewById(R.id.splshIV);
        movingTV = (TextView) findViewById(R.id.movingTV);

        Animation blinking = AnimationUtils.loadAnimation(this, R.anim.blink);
        movingTV.startAnimation(blinking);

        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotation);
        splshIV.startAnimation(rotate);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Navigation.class);
                startActivity(intent);
                finish();
            }
        }, 5 * 1000);

    }

    public static void startProgressBar(ProgressDialog masterProgress, String message) {
        masterProgress.setMessage(message);
        masterProgress.setIndeterminate(true);
        masterProgress.setCancelable(false);
        masterProgress.show();
    }

    public static void dismissProgressBar(ProgressDialog masterProgress) {
        masterProgress.dismiss();
    }

    private void hideAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.hide();
        }
    }

    public static void showSuccessDialog(String message, Context context) {
        //  hideAlertDialog();
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, null)
                .create();
        alertDialog.show();
    }

    public static void showErrorDialog(String message, Context context) {
        //  hideAlertDialog();
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("ERROR!")
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, null)
                .create();
        alertDialog.show();
    }
}
