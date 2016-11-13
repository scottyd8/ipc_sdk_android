package com.worldpayment.demoapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldpay.library.enums.Swiper;
import com.worldpay.library.webservices.services.authtokens.AuthTokenCreateRequest;
import com.worldpay.library.webservices.services.authtokens.AuthTokenCreateResponse;
import com.worldpay.library.webservices.tasks.CreateAuthTokenTask;
import com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity;
import com.worldpayment.demoapp.activities.refundvoid.RefundVoidViewActivity;
import com.worldpayment.demoapp.activities.settlement.ActivitySettlement;
import com.worldpayment.demoapp.activities.vaultcustomers.VaultOperations;
import com.worldpayment.demoapp.fragments.HomeFragment;
import com.worldpayment.demoapp.utility.KeyboardUtility;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;
import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class Navigation extends AppCompatActivity implements View.OnClickListener {

    TextView email, name;
    ImageView profilePhoto;
    LinearLayout profileLayout;
    DrawerLayout drawer;

    private ProgressDialog progressDialog;

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    public static Swiper swiper;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);

        email = (TextView) header.findViewById(R.id.headeremail);
        name = (TextView) header.findViewById(R.id.headername);
        profilePhoto = (ImageView) header.findViewById(R.id.profile_image);
        profileLayout = (LinearLayout) header.findViewById(R.id.profileLayout);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);

        TextView toolbar_title = (TextView) appBarLayout.findViewById(R.id.toolbar_title);
        //Swiper SHuttle
        swiper = Swiper.fromDescription("Shuttle");

        if (!PreferenceManager.getDefaultSharedPreferences(Navigation.this).contains(PREF_AUTH_TOKEN)) {
            authenticate();
        }

        profileLayout.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.frame, homeFragment);
        toolbar_title.setText("Home");
        getSupportActionBar().setTitle("");
        homeFragmentTransaction.commit();

        if (getIntent().getExtras() != null) {
            String Activity = getIntent().getExtras().getString("Activity");


        } else {

        }

        if (MERCHANT_ID.equals("") && MERCHANT_KEY.equals("")) {
            validateMerchantInfo();
        }
        navigationView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                KeyboardUtility.closeKeyboard(getApplicationContext(), view);
                return false;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing member_menu_items on item click
                drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.home:
                        drawer.closeDrawers();
                        return true;

                    case R.id.debit_credit:
                        Intent intent = new Intent(Navigation.this, CreditDebitActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.refund_void:
                        Intent refund_void = new Intent(Navigation.this, RefundVoidViewActivity.class);
                        startActivity(refund_void);
                        return true;

                    case R.id.settlement:
                        Intent settlement = new Intent(Navigation.this, ActivitySettlement.class);
                        startActivity(settlement);
                        return true;

                    case R.id.vault:
                        Intent vault = new Intent(Navigation.this, VaultOperations.class);
                        startActivity(vault);
                        return true;


                    default:
                        return true;


                }
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to member_menu_items layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.profileLayout:
                break;

            default:
                break;
        }
    }

    public void validateMerchantInfo() {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                Navigation.this);

        alertDialogBuilder.setTitle("Alert Info!");
        alertDialogBuilder.setMessage(getResources().getString(R.string.requiredKeyID));
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void authenticate() {

        String authToken = PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_AUTH_TOKEN, null);

        if (!TextUtils.isEmpty(authToken)) {
            return;
        }

        AuthTokenCreateRequest authTokenRequestDto = new AuthTokenCreateRequest();
        authTokenRequestDto.setApplicationId(getApplication().getApplicationInfo().packageName);
        authTokenRequestDto.setApplicationOs(Build.VERSION.CODENAME);
        authTokenRequestDto.setApplicationVersion(BuildConfig.VERSION_NAME);
        authTokenRequestDto.setDeveloperId(BuildConfig.DEVELOPER_ID);
        authTokenRequestDto.setTerminalId(swiper.getTerminalId());
        authTokenRequestDto.setTerminalVendor(swiper.getVendorId());
        authTokenRequestDto.setApplicationModel(Build.MODEL);

        switch (BuildConfig.MERCHANT_GATEWAY) {

            case NONE:
                break;

            case SECURENET:
                authTokenRequestDto.setSecureNetId(BuildConfig.MERCHANT_ID);
                authTokenRequestDto.setSecureNetKey(BuildConfig.MERCHANT_KEY);
                break;

            case MERCHANT_PARTNERS:
                authTokenRequestDto.setMerchantPartnersId(BuildConfig.MERCHANT_ID);
                authTokenRequestDto.setMerchantPartnersPin(BuildConfig.MERCHANT_KEY);
                break;
        }

        new CreateAuthTokenTask(authTokenRequestDto) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(Navigation.this,
                        getResources().getString(R.string.loading_title),
                        getResources().getString(R.string.loading_data), true);
            }

            @Override
            protected void onPostExecute(AuthTokenCreateResponse authTokenCreateResponse) {
                if (authTokenCreateResponse.hasError()) {
                    closeProgressDialog();
                    authenticatePopup("ERROR!", "" + R.string.error_no_token, Navigation.this);
                    return;
                }
                String authToken = authTokenCreateResponse.getAuthToken();
                PreferenceManager.getDefaultSharedPreferences(Navigation.this).edit()
                        .putString(PREF_AUTH_TOKEN, authToken).apply();

                closeProgressDialog();
            }
        }.execute();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void authenticatePopup(final String titleStr, final String messageStr, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.master_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);


        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        TextView message = (TextView) dialogSignature.findViewById(R.id.message);

        if (titleStr.toString().trim().equals("ERROR!")) {
            title.setTextColor(Color.parseColor("#f11e15"));
        } else if (titleStr.toString().trim().equals("SUCCESS")) {
            title.setTextColor(Color.parseColor("#007867"));
        }
        title.setText(titleStr);
        message.setText(messageStr);

        Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        dialog_btn_negative.setText("" + getResources().getString(R.string.close));

        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);
        dialog_btn_positive.setText("" + getResources().getString(R.string.retry));
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                authenticate();
            }
        });

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
