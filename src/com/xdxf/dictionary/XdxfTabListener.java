package com.xdxf.dictionary;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.xdxf.dictionary.fragments.HistoryFragment;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/30/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class XdxfTabListener<T extends Fragment> implements ActionBar.TabListener {
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private Fragment mFragment;
    /**
     * This was the simplest way i cud pass some data from
     * historyfragment to xdxfdictionary fragment
     */
    private Bundle args;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity The host Activity, used to instantiate the fragment
     * @param tag      The identifier tag for the fragment
     * @param clz      The fragment's Class, used to instantiate the fragment
     *                 http://stackoverflow.com/a/10031750/722965
     */

    public XdxfTabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
        if (mFragment != null && !mFragment.isDetached()) {
            FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName(), (Bundle) tab.getTag());
            ft.add(android.R.id.content, mFragment, mTag);
            tab.setTag(null);
        } else {
            if(tab.getTag() != null)
            {
                //we have bundle, gotta use it
                ft.detach(mFragment);
                mFragment = null;
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), (Bundle) tab.getTag());
                ft.add(android.R.id.content, mFragment, mTag);
                tab.setTag(null);
            }
            else
            // If it exists, simply attach it in order to show it
//            ft.setCustomAnimations(android.R.animator.fade_in,
//                    R.animator.animationtest);
            ft.attach(mFragment);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        if (mFragment != null) {

//            ft.setCustomAnimations(android.R.animator.fade_in,
//                    R.animator.test);
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
