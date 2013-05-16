package de.thiemonagel.vegdroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailsFragment extends SherlockFragment implements
        OnClickListener {
    public static final String TAG = "detailsfrag";
    private Venue mVenue;
    private GetData gd;

    static DetailsFragment newInstance() {
        DetailsFragment details = new DetailsFragment();
        return (details);
    }

    public interface GetData {
        public int getData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            gd = (GetData) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GetData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mVenue = Global.getInstance(getSherlockActivity()).venues.get(gd
                .getData());

        View result = inflate
                .inflate(R.layout.activity_entry, container, false);
        {
            TextView tv = (TextView) result.findViewById(R.id.name);
            tv.setText(mVenue.name);
        }
        {
            RatingBar rb = (RatingBar) result.findViewById(R.id.ratingBar2);
            rb.setRating(mVenue.rating);
        }
        {
            // TextView tv = (TextView)
            // findViewById(R.id.veg_level_description);
            // tv.setText( mVenue.get("veg_level_description") );
        }
        {
            Button but = (Button) result.findViewById(R.id.phone);
            if (mVenue.phone.equals(""))
                but.setVisibility(View.GONE);
            else
                but.setText("Dial " + mVenue.phone);
        }
        {
            Button but = (Button) result.findViewById(R.id.website);
            if (mVenue.website.equals(""))
                but.setVisibility(View.GONE);
            else
                // but.setText( "Visit " + fData.get("website") );
                but.setText("Visit web site");
        }
        {
            TextView tv = (TextView) result.findViewById(R.id.address);
            String addressBlock = mVenue.locHtml();
            if (addressBlock.equals(""))
                tv.setVisibility(View.GONE);
            else
                tv.setText(Html.fromHtml(addressBlock));
        }
        {
            TextView tv = (TextView) result.findViewById(R.id.long_description);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            if (mVenue.longDescription.equals(""))
                tv.setVisibility(View.GONE);
            else
                tv.setText(Html.fromHtml(mVenue.longDescription));
        }
        return result;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        String uri = null;
        switch (id) {
        case R.id.map:
            // the name of the venue is not included in the query string because
            // it seems to cause problems when Google isn't aware of the
            // specific venue
            uri = "geo:0,0?q=" + mVenue.locString();
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            break;
        case R.id.website:
            uri = "tel:" + mVenue.phone;
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            break;
        case R.id.phone:
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mVenue.website));
            startActivity(intent);
            break;
        }

    }

}
