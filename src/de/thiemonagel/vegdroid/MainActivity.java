package de.thiemonagel.vegdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

    private MainMenuFragment menuFrag;
    private AboutFragment aboutFrag;
    private Fragment visible; // used to keep track of what fragment is being
                              // shown.
    private Fragment mVisibleCached; // keep track of the previous fragment that
                                     // was being shown in case user presses the
                                     // back button.
    public static final int MASK_ALL = 0x3ff;
    public static final int MASK_FOOD = 0x00f;
    public static final int MASK_SHOP = 0x050;
    public static final int MASK_LODGE = 0x100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupFragments();
    }

    /*
     * This is where the first fragment is initialized. Beyond that everything
     * is event driven by the on click listener from the different fragments and
     * menus.
     */
    public void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        menuFrag = ((MainMenuFragment) fm
                .findFragmentByTag(MainMenuFragment.TAG));
        if (menuFrag == null) {
            menuFrag = MainMenuFragment.newInstance();
            ft.add(R.id.map_list_menu, menuFrag, MainMenuFragment.TAG);
        }
        visible = menuFrag;
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: // no idea what this is for. This a a link to
            showFragment(0); // your icon in the actionbar. Most people set
                             // it to the home Screen of the app.
            return true;
        case R.id.menu_about:
            showFragment(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFragment(int fragIn) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (visible != null) {
            mVisibleCached = visible; //we need to cache the currently visible fragment in case the user presses the back button.
        }
        // 0 is for menu fragmnet
        if (fragIn == 0) {
            menuFrag = ((MainMenuFragment) fm
                    .findFragmentByTag(MainMenuFragment.TAG));
            if (menuFrag == null)
                menuFrag = MainMenuFragment.newInstance();
            ft.replace(R.id.map_list_menu, menuFrag, MainMenuFragment.TAG);
            ft.addToBackStack(null);
            visible = menuFrag;
            ft.commit();
        }
        // 1 is for about fragment
        else if (fragIn == 1) {
            aboutFrag = ((AboutFragment) fm
                    .findFragmentByTag(AboutFragment.TAG));

            if (aboutFrag == null)
                aboutFrag = AboutFragment.newInstance();
            ft.replace(R.id.map_list_menu, aboutFrag, AboutFragment.TAG);
            ft.addToBackStack(null);
            visible = aboutFrag;
            ft.commit();
        }
        // the rest are map fragments with different data displayed
        else {
            switch (fragIn) {
            case 3:
                Global.getInstance(this).setCatFilterMask(MASK_FOOD);
                break;
            case 4:
                Global.getInstance(this).setCatFilterMask(MASK_SHOP);
                break;
            case 5:
                Global.getInstance(this).setCatFilterMask(MASK_LODGE);
                break;
            case 6:
                Global.getInstance(this).setCatFilterMask(MASK_ALL);
                break;
            }
            Intent in = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(in);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        visible = mVisibleCached;
    }

    public void StartFood(View view) {
        Global.getInstance(this).setCatFilterMask(MASK_FOOD);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void StartShop(View view) {
        Global.getInstance(this).setCatFilterMask(MASK_SHOP);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void StartLodge(View view) {
        Global.getInstance(this).setCatFilterMask(MASK_LODGE);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void StartAll(View view) {
        Global.getInstance(this).setCatFilterMask(MASK_ALL);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void StartCustom(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

}
