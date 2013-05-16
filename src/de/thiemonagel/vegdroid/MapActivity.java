package de.thiemonagel.vegdroid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.thiemonagel.vegdroid.DetailsFragment.GetData;
import de.thiemonagel.vegdroid.ListFragment.SetDetails;

/**
 * Activity to display venues as markers on a zoom- and moveable map.
 *
 * To receive tap events a map of (Marker.getId() --> venueId) is required. To
 * filter venues, a mapping of Marker <--> venueId is required.
 *
 */
public class MapActivity extends AbstractMapActivity implements GetData,
        SetDetails {
    private static final String MAP_FRAGMENT_TAG = "map";
    private static final boolean DEBUG = false;
    private static final int SHOW_MAP = 0;
    private static final int SHOW_DETAILS = 1;
    private static final int SHOW_LIST = 2;
    private static final int SHOW_ABOUT = 3;
    public volatile GoogleMap map;
    private String fError = "";
    private int venueId;
    private Location fCurrentLoc = null;
    private DetailsFragment details;
    private AboutFragment aboutFrag;
    private SupportMapFragment fMap;
    private ListFragment listFrag;
    private Fragment visible;
    private Fragment mVisibleCached;
    private Map<Marker, Integer> markers = new HashMap<Marker, Integer>(); // (Marker
                                                                           // -->
                                                                           // venueId)
    private Map<Integer, Marker> venues = new HashMap<Integer, Marker>(); // (venueId
                                                                          // -->
                                                                          // Marker)

    public void setDetails(int vId) {
        venueId = vId;
        showFragment(SHOW_DETAILS);
    }

    // this is used by the details fragment to retrieve the data for the
    // specific venue. It is part of the interface that this class implements.
    public int getData() {
        return venueId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportActionBar().setHomeButtonEnabled(true);

            if (readyToGo()) {
                fMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentByTag(MAP_FRAGMENT_TAG));
                if (fMap == null) {
                    fMap = SupportMapFragment.newInstance();
                }
                FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                ft.replace(R.id.map_list_menu, fMap, MAP_FRAGMENT_TAG);
                ft.commit();
            }


        setupFragments(savedInstanceState);

        // Work around pre-Froyo bugs in HTTP connection reuse.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }

        Global.getInstance(this).mapActivity = this;

        if (this.UpdateLocation())
            new LoadStream(this).execute(this.getLocation());
    }

    private void setupFragments(Bundle savedInstanceState) {
        String v = null;
        int i;
        if (savedInstanceState == null) {
            if (readyToGo())
                showFragment(SHOW_MAP);
        } else {
            v = savedInstanceState.getString("visible");
            i = savedInstanceState.getInt("vId");
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            if (v != null) {
                if (v.equals(ListFragment.TAG)) {
                    listFrag = ListFragment.newInstance();
                    ft.replace(R.id.map_list_menu, listFrag, ListFragment.TAG);
                    visible = listFrag;
                }
                if (v.equals(DetailsFragment.TAG)) {
                    venueId = i;
                    details = DetailsFragment.newInstance();
                    ft.replace(getLayout(), details, DetailsFragment.TAG);
                    visible = details;
                }
                if (v.equals(AboutFragment.TAG)) {
                    aboutFrag = AboutFragment.newInstance();
                    ft.replace(getLayout(), aboutFrag, AboutFragment.TAG);
                    visible = aboutFrag;
                }
                ft.commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (visible != null)
            outState.putString("visible", visible.getTag());
        outState.putInt("vId", venueId);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // In case Google Play services has since become available.

        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        Global.getInstance(this).mapActivity = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: // no idea what this is for
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.menu_about:
            showFragment(SHOW_ABOUT);
            return true;
        case R.id.menu_filter_cat:
            FilterDialog.CreateDialog(this).show();
            return true;
        case R.id.menu_list:
            showFragment(SHOW_LIST);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (map == null && fMap != null) {
            // Try to obtain the map from the SupportMapFragment.
            map = fMap.getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setupMap();
            }
        }

    }

    private void setupMap() {
        map.setMyLocationEnabled(true);
        UiSettings ui = map.getUiSettings();
        ui.setCompassEnabled(true);
        ui.setMyLocationButtonEnabled(true);
        ui.setRotateGesturesEnabled(true);
        ui.setScrollGesturesEnabled(true);
        ui.setTiltGesturesEnabled(true);
        ui.setZoomControlsEnabled(true);
        ui.setZoomGesturesEnabled(true);

        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker m) {
                setDetails(markers.get(m));
                showFragment(SHOW_DETAILS);
            }
        });

        if (this.UpdateLocation()) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    this.getLocation(), 12.f));
        }
        /*
         * Date gstart = new Date(); Geocoder gc = new Geocoder(this); int N =
         * 1; for ( int i=1; i<=N; i++ ) try { String loc = "Leopoldstr. " + i +
         * ", München";
         *
         * Date start = new Date(); List<Address> la =
         * gc.getFromLocationName(loc, 1); Date end = new Date(); float ms =
         * (end.getTime()-start.getTime()); Log.d( LOG_TAG, "geocode " + i +
         * ": " + ms + " ms" );
         *
         * if ( la.size() > 0 ) { Address a = la.get(0); fMap.addMarker( new
         * MarkerOptions() .position( new LatLng(a.getLatitude(),
         * a.getLongitude()) ) .title("Title") .snippet(loc) ); } } catch
         * (IOException e) {};
         *
         * Date end = new Date(); float ms = (end.getTime()-gstart.getTime());
         * Log.d( LOG_TAG, "avg time per geocode: " + ms/N + " ms" );
         */

        /*
         * ArrayList<Float> lat = new ArrayList<Float>(); ArrayList<Float> lon =
         * new ArrayList<Float>(); int N=1000; for ( int i=0; i<N; i++ ) { float
         * la = 48.139126f; float lo = 11.580186f; for ( int j=0; j<20; j++ ) {
         * la += (Math.random()-0.5)/30; lo += (Math.random()-0.5)/30; }
         * lat.add(la); lon.add(lo); }
         *
         * for ( int i=0; i<N; i++ ) { fMap.addMarker( new MarkerOptions()
         * .position( new LatLng(lat.get(i), lon.get(i)) ) .title("Title "+i)
         * .snippet("Text "+i) ); }
         */
    }

    private void showFragment(int fragIn) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        int layout = getLayout(); // test which layout we are in.
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (visible != null) {
            mVisibleCached = visible; // we need to cache the currently visible
            // fragment in case the user presses the
            // back button.
        }
        // 0 is for menu fragmnet
        switch (fragIn) {
        case SHOW_MAP:
            fMap = ((SupportMapFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG));
            // Test if the map fragment is there
            if (fMap == null)
                fMap = SupportMapFragment.newInstance();
            ft.replace(R.id.map_list_menu, fMap, MAP_FRAGMENT_TAG);
            ft.addToBackStack(null);
            visible = fMap;
            setUpMapIfNeeded();
            break;
        case SHOW_DETAILS:
            details = ((DetailsFragment) fm
                    .findFragmentByTag(DetailsFragment.TAG));
            if (details == null)
                details = DetailsFragment.newInstance();
            ft.replace(layout, details, DetailsFragment.TAG);
            ft.addToBackStack(null);
            visible = details;
            break;
        case SHOW_ABOUT:
            aboutFrag = ((AboutFragment) fm
                    .findFragmentByTag(AboutFragment.TAG));

            if (aboutFrag == null)
                aboutFrag = AboutFragment.newInstance();
            ft.replace(layout, aboutFrag, AboutFragment.TAG);
            ft.addToBackStack(null);
            visible = aboutFrag;
            break;
        case SHOW_LIST:
            listFrag = ((ListFragment) fm.findFragmentByTag(ListFragment.TAG));
            if (listFrag == null)
                listFrag = ListFragment.newInstance();
            ft.replace(R.id.map_list_menu, listFrag, ListFragment.TAG);
            visible = listFrag;
            break;

        }
        ft.commit();
    }

    private int getLayout() {

        if (findViewById(R.id.details_about) != null) {
            Toast.makeText(this, "details about not null", Toast.LENGTH_SHORT)
                    .show();
            Log.d(Global.LOG_TAG, " notnulldetails");
            return R.id.details_about;
        }
        return R.id.map_list_menu;
    }

    boolean UpdateLocation() {
        // find location
        LocationManager lMan = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> lproviders = lMan.getProviders(false); // true = enabled
                                                            // only
        Log.d(Global.LOG_TAG, lproviders.size() + " location providers found.");
        for (String prov : lproviders) {
            Location l = lMan.getLastKnownLocation(prov);

            String logstr = prov + ": ";
            if (l != null) {
                logstr += l.getLatitude();
                logstr += ", " + l.getLongitude();
                logstr += ", time: " + l.getTime();
                if (l.hasAccuracy()) {
                    logstr += ", error: " + l.getAccuracy() + " m";
                }
            } else {
                logstr += "[empty]";
            }
            Log.d(Global.LOG_TAG, logstr);

            if (l == null)
                continue;

            if (fCurrentLoc == null) {
                fCurrentLoc = l;
                continue;
            }

            // if one reading doesn't have accuracy, the latest is preferred
            if (!fCurrentLoc.hasAccuracy() || !l.hasAccuracy()) {
                if (l.getTime() > fCurrentLoc.getTime()) {
                    fCurrentLoc = l;
                }
                continue;
            }

            long btime = fCurrentLoc.getTime(); // ms
            long ltime = l.getTime(); // ms
            float bacc = fCurrentLoc.getAccuracy(); // m
            float lacc = l.getAccuracy(); // m

            // both have accuracy, l is more recent and more accurate
            if (ltime > btime && lacc < bacc) {
                fCurrentLoc = l;
                continue;
            }

            long tdist = ltime - btime;
            float dist = l.distanceTo(fCurrentLoc);
            // agreement in sigmas
            float agr = dist / FloatMath.sqrt(bacc * bacc + lacc * lacc);

            // use outdated but more precise measurement only
            // when agreement isn't too bad and time difference isn't
            // too large
            float crit = 1e5f / tdist;
            if (crit < 3f) {
                crit = 3f;
            }
            if (agr < crit) {
                if (lacc < bacc) {
                    fCurrentLoc = l;
                }
            } else {
                if (ltime > btime) {
                    fCurrentLoc = l;
                }
            }
        }

        if (fCurrentLoc == null) {
            if (DEBUG) {
                // set bogus location for debugging
                Log.i(Global.LOG_TAG, "No location found.");
                // url += "0,0";
                fCurrentLoc = new Location("");
                fCurrentLoc.setLatitude(48.139126);
                fCurrentLoc.setLongitude(11.580186);
                fCurrentLoc.setAccuracy(100.f);
            } else {
                // abort with error
                fError = "Location could not be determined!";
                return false;
            }
        }
        return true;
    }

    // access current location
    protected LatLng getLocation() {
        if (fCurrentLoc == null)
            return null;
        else
            return new LatLng(fCurrentLoc.getLatitude(),
                    fCurrentLoc.getLongitude());
    }

    public synchronized void addMarker(Context context, int vId, LatLng ll) {
        if (map == null)
            return;

        Venue v = Global.getInstance(context).venues.get(vId);
        if (v == null || v.filtered(context))
            return;

        String name = Global.getInstance(context).venues.get(vId).name;
        String desc = Global.getInstance(context).venues.get(vId).shortDescription;
        MarkerOptions mo = new MarkerOptions().position(ll).title(name)
                .snippet(desc);
        Marker m = map.addMarker(mo);
        if (m == null)
            return;

        markers.put(m, vId);
        venues.put(vId, m);
    }

    public synchronized void updateFilter(Context context) {
        for (Iterator<Map.Entry<Marker, Integer>> it = markers.entrySet()
                .iterator(); it.hasNext();) {
            Map.Entry<Marker, Integer> entry = it.next();
            Marker m = entry.getKey();
            Integer vId = entry.getValue();
            Venue v = Global.getInstance(context).venues.get(vId);
            if (v == null || v.filtered(context)) {
                venues.remove(vId); // remove from MapActivity.venues map
                m.remove(); // remove from map
                it.remove(); // remove from MapActivity.markes map
            }
        }
        for (Map.Entry<Integer, Venue> entry : Global.getInstance(context).venues
                .entrySet()) {
            if (venues.containsKey(entry.getKey()))
                continue;
            int vId = entry.getKey();
            Venue v = Global.getInstance(context).venues.get(vId);
            if (v == null || v.filtered(context))
                continue;
            Global.getInstance(context).CGC.Resolve(context, v);
        }
    }
}
