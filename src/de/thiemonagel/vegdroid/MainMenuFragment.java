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

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        // hide "custom" button when category filter has not been customized

        int filter = Global.getInstance(this).getCatFilterMask() & MASK_ALL;
        Log.d(Global.LOG_TAG, "onResume() filter: " + filter);
        if (filter == MASK_FOOD || filter == MASK_SHOP || filter == MASK_LODGE
                || filter == MASK_ALL)
            bt.setVisibility(View.GONE);
        else
            bt.setVisibility(View.VISIBLE);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

        case R.id.food:

            break;
        case R.id.shops:

            break;
        case R.id.accommodation:

            break;
        case R.id.all:

            break;
        case R.id.btCustom:
            break;

        }
    }

}
