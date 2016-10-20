package com.worldpayment.demoapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldpayment.demoapp.activities.ActivitySettlement;
import com.worldpayment.demoapp.activities.RefundVoidViewActivity;
import com.worldpayment.demoapp.fragments.HomeFragment;

import static com.worldpayment.demoapp.BuildConfig.MERCHANT_ID;
import static com.worldpayment.demoapp.BuildConfig.MERCHANT_KEY;


public class Navigation extends AppCompatActivity implements View.OnClickListener {

    TextView email, name;
    ImageView profilePhoto;
    LinearLayout profileLayout;
    DrawerLayout drawer;

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;

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

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        profileLayout.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.frame, homeFragment);
        setTitle("Home");
        homeFragmentTransaction.commit();

        if (getIntent().getExtras() != null) {
            String Activity = getIntent().getExtras().getString("Activity");
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
                {
                    menuItem.setChecked(false);
                }
                else
                {
                    menuItem.setChecked(true);
                }

                //Closing member_menu_items on item click
                drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.home:
                        drawer.closeDrawers();
                        return true;

                    case R.id.debit_credit:
                        Intent intent = new Intent(Navigation.this, DebitCreditActivity.class);
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
        alertDialogBuilder.setMessage("Please add Merchant KEY & ID inside 'app/build.gradle' !");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("CLose",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
