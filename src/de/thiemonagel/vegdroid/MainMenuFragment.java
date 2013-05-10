package de.thiemonagel.vegdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainMenuFragment extends SherlockFragment implements
        OnClickListener {
    public static final String TAG = "mainmenufrag";
    MainActivity main = null;
    private Button bt;

    static MainMenuFragment newInstance() {
        MainMenuFragment menu = new MainMenuFragment();
        return (menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup container,
            Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View result = inflate.inflate(R.layout.activity_main, container, false);
        Button food = (Button) result.findViewById(R.id.food);
        food.setOnClickListener(this);
        Button shops = (Button) result.findViewById(R.id.shops);
        shops.setOnClickListener(this);
        Button accommodation = (Button) result.findViewById(R.id.accommodation);
        accommodation.setOnClickListener(this);
        Button all = (Button) result.findViewById(R.id.all);
        all.setOnClickListener(this);
        bt = (Button) result.findViewById(R.id.btCustom);
        bt.setText(Html.fromHtml(getString(R.string.button_previous)));
        main = ((MainActivity) getSherlockActivity());

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        final int MASK_ALL = 0x3ff;
        final int MASK_FOOD = 0x00f;
        final int MASK_SHOP = 0x050;
        final int MASK_LODGE = 0x100;

        // hide "custom" button when category filter has not been customized

        int filter = Global.getInstance(main).getCatFilterMask()
                & MASK_ALL;
        Log.d(Global.LOG_TAG, "onResume() filter: " + filter);
        if (filter == MASK_FOOD || filter == MASK_SHOP
                || filter == MASK_LODGE || filter == MASK_ALL)
            bt.setVisibility(View.GONE);
        else
            bt.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(View arg0) {

        int id = arg0.getId();
        switch (id) {

        case R.id.food:
            main.showFragment(3); //the numbers 3-7 relate to the switch statement for the show fragments function.
            break;
        case R.id.shops:
            main.showFragment(4);
            break;
        case R.id.accommodation:
            main.showFragment(5);
            break;
        case R.id.all:
            main.showFragment(6);
            break;
        case R.id.btCustom:
            main.showFragment(7);
            break;

        }
    }

}
