package de.thiemonagel.vegdroid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class ListFragment extends SherlockListFragment {
    public static final String TAG = "listfragment";
    private SetDetails sd;
    private ArrayList<Venue> dataList = null;
    MapActivity ma = null;

    static ListFragment newInstance() {
        ListFragment lf = new ListFragment();
        return lf;
    }

    public interface SetDetails {
        public void setDetails(int vId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sd = (SetDetails) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setDetails");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int vId = dataList.get(position).getId();
        sd.setDetails(vId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ma = (MapActivity)getSherlockActivity();
        if (!checkAdapter()) {
            ListViewLoaderTask lvlTask = new ListViewLoaderTask();
            lvlTask.execute();
        } else {
            SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
            adapter.notifyDataSetChanged();
        }


    }

    public boolean checkAdapter() {
        MyListAdapter adapter = (MyListAdapter) getListAdapter();
        if (adapter != null)
            return true;
        else
            return false;
    }


    private class ListViewLoaderTask extends
            AsyncTask<Void, Void, MyListAdapter> {
        MyListAdapter adapter = null;
        ArrayList<Venue> list = new ArrayList<Venue>();

        @Override
        protected MyListAdapter doInBackground(Void...params ) {

            Iterator<Entry<Integer, Venue>> it = Global.getInstance(getSherlockActivity()).venues.entrySet().iterator();

            while (it.hasNext()) {
                Entry<Integer, Venue> e = it.next();
                Venue v = (Venue) e.getValue();
                if (!v.filtered(ma)){
                    list.add(v);
                }
            }
            dataList = list;
            if (dataList != null)
            adapter = new MyListAdapter(ma, R.layout.list_item, dataList);
            return adapter;
        }

        @Override
        protected void onPostExecute(MyListAdapter adapter) {

            setListAdapter(adapter);
            MyListAdapter ad = (MyListAdapter) getListAdapter();
            ad.notifyDataSetChanged();
        }
    }
}
