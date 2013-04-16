package com.xdxf.dictionary.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xdxf.dictionary.HistoryListAdapter;
import com.xdxf.dictionary.R;
import com.xdxf.dictionary.SwipeDetector;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.sqlite.HistoryObject;
import com.xdxf.dictionary.utils.DialogFactory;

public class HistoryFragment extends Fragment {


    private ListView listView;
    private DBHelper dbHelper;
    private SwipeDetector swipeDetector = new SwipeDetector();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview,
                container, false);
        listView = (ListView) view.findViewById(R.id.listView1);
        setHasOptionsMenu(true);
        dbHelper = DBHelper.getInstance(getActivity());
        listView.setOnTouchListener(swipeDetector);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Refresh();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryListAdapter historyListAdapter = (HistoryListAdapter) adapterView.getAdapter();
                final HistoryObject historyObject = historyListAdapter.getItem(i);
                //that was a swipe
                if (swipeDetector.swipeDetected()) {
                    //if swipe was left to right  delete history object
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {

                        if (historyObject.isFavourite()) {
                            DialogFactory.showDialog(getActivity(), "Warning!", "Delete favourite word?", "Yeah! No longer my favourite", "Ooops! Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dbHelper.removeHistoryObject(historyObject);
                                    Refresh();
                                }
                            }, null);
                        } else {
                            dbHelper.removeHistoryObject(historyObject);
                            Refresh();
                        }

                    }
                } else {
                    //ok history item was clicked...
                    Bundle args = new Bundle();
                    args.putString("Word",historyObject.getWord());
                    ActionBar.Tab t = getActivity().getActionBar().getTabAt(0);
                    t.setTag(args);
                    getActivity().getActionBar().selectTab(t);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);    //To change body of overridden methods use File | Settings | File Templates.
        inflater.inflate(R.menu.menu_history, menu);
    }

    private void Refresh() {
        HistoryListAdapter adapter = new HistoryListAdapter(getActivity(), dbHelper.getAllHistoryObjects());
        listView.setAdapter(adapter);
    }


}
