package de.thiemonagel.vegdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

    private MainMenuFragment menuFrag;
    private Fragment visible;
    private Fragment mVisibleCached;
    private static final int MASK_ALL = 0x3ff;
    private static final int MASK_FOOD = 0x00f;
    private static final int MASK_SHOP = 0x050;
    private static final int MASK_LODGE = 0x100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

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
            ft.add(R.id.fragment_container, menuFrag, MainMenuFragment.TAG);
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
                                // your icon in the actionbar. Most people set
                                // it to the home Screen of the app.
            return true;
        case R.id.menu_about:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void showFragment(int fragIn) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (visible != null) {
            ft.hide(visible);
            mVisibleCached = visible;
        }

        switch (fragIn) {
        case 0:
            menuFrag = ((MainMenuFragment) fm
                    .findFragmentByTag(MainMenuFragment.TAG));
            if (menuFrag == null)
                menuFrag = MainMenuFragment.newInstance();
            ft.replace(R.id.fragment_container, menuFrag, MainMenuFragment.TAG);
            ft.addToBackStack(null);
            visible = menuFrag;
            break;
        case 1:
            Global.getInstance(this).setCatFilterMask(MASK_FOOD);
            break;
        case 2:
            Global.getInstance(this).setCatFilterMask(MASK_SHOP);

            break;
        case 3:
            Global.getInstance(this).setCatFilterMask(MASK_LODGE);

            break;
        case 4:
            Global.getInstance(this).setCatFilterMask(MASK_ALL);

            break;

        }
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
