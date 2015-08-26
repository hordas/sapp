package udatraining.dyomin.com.udaproj1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String forecastString;
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private final int DETAILS_LOADER = 1;
    private TextView forecastTextView;
    private ShareActionProvider actionProvider;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        forecastTextView = ((TextView) v.findViewById(R.id.textview_forecast_detailed_fragment));
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        actionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(item);
        if (actionProvider != null) {
            actionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        Uri uri = null;
        if (intent != null) {
            uri = Uri.parse(intent.getDataString());
        }
        return new CursorLoader(getActivity(), uri, ForecastFragment.FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            forecastString = Utility.formatDate(data.getLong(ForecastFragment.COL_WEATHER_DATE))
                    + " - "
                    + data.getString(ForecastFragment.COL_WEATHER_MAX_TEMP) + " / "
                    + data.getString(ForecastFragment.COL_WEATHER_MIN_TEMP) + " - "
                    + data.getString(ForecastFragment.COL_WEATHER_DESC);
            forecastTextView.setText(forecastString);
        } else {
            forecastTextView.setText("Unable to download results.");
        }
        if (actionProvider != null) {
            actionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastTextView.setText("");
        forecastString = "";
    }
}
