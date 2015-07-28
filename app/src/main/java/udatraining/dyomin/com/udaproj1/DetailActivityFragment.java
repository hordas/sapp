package udatraining.dyomin.com.udaproj1;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String forecastString;
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        forecastString = getActivity().getIntent().getStringExtra("forecast");
        ((TextView) v.findViewById(R.id.textview_forecast_detailed_fragment)).setText(forecastString);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider actionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(item);
        if (actionProvider != null) {
            actionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provide is null?");
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
