package de.thiemonagel.vegdroid;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class AboutFragment extends SherlockFragment {
    public static final String TAG = "aboutfrag";
    MainActivity main = null;

    static AboutFragment newInstance() {
        AboutFragment aboutFrag = new AboutFragment();
        return (aboutFrag);
    }

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View result = inflate
                .inflate(R.layout.activity_about, container, false);

        // display Google Maps API licensing info, as required in
        // https://developers.google.com/maps/documentation/android/intro
        {
            TextView tv = (TextView) result.findViewById(R.id.aboutText);
            tv.setText(Html.fromHtml(getString(R.string.text_about)));
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
        {
            TextView tv = (TextView) result.findViewById(R.id.GMapLegalese);
            tv.setText(GooglePlayServicesUtil
                    .getOpenSourceSoftwareLicenseInfo(getSherlockActivity()));
        }
        return result;
    }

    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
     * menu; this adds items to the action bar if it is present.
     * getSupportMenuInflater().inflate(R.menu.activity_about, menu); return
     * true; }
     */

}
