package com.neart.tup.neart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class Tutee_MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutee_activity_main);
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.tutee_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_tutee_search);




        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {

                            case R.id.navigation_tutee_profile:
                                selectedFragment = Tutee_FragmentProfile.newInstance();
                                break;
                            case R.id.navigation_tutee_message:
                                selectedFragment = Tutee_FragmentMessage.newInstance();
                                break;
                            case R.id.navigation_tutee_search:
                                selectedFragment = Tutee_FragmentSearch.newInstance();
                                break;
                            case R.id.navigation_tutee_notification:
                                selectedFragment = Tutee_FragmentNotification.newInstance();
                                break;
                            case R.id.navigation_tutee_more:
                                selectedFragment = Tutee_FragmentMore.newInstance();
                                break;

                            


                        }

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;


                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, Tutee_FragmentSearch.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }


}