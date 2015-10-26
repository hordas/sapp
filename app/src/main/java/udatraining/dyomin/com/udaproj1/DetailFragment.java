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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.ImageView;
import android.widget.TextView;

import udatraining.dyomin.com.udaproj1.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String forecastString;
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final int DETAILS_LOADER = 1;
    public static final String DETAIL_URI = "detail_uri";
    private ShareActionProvider actionProvider;
    Uri mUri;

    private TextView dayTextview;
    private TextView dateTextview;
    private TextView maxTempTextview;
    private TextView minTempTextview;
    private TextView descriptionTextview;
    private TextView humidityTextview;
    private TextView windTextview;
    private TextView pressureTextview;
    private ImageView image;
    private CompassView compassView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        dayTextview = (TextView) v.findViewById(R.id.textview_day_of_week_fragment_detail);
        dateTextview = (TextView) v.findViewById(R.id.textview_date_fragment_detail);
        maxTempTextview = (TextView) v.findViewById(R.id.textview_max_temp_fragment_detail);
        minTempTextview = (TextView) v.findViewById(R.id.textview_min_temp_fragment_detail);
        descriptionTextview = (TextView) v.findViewById(R.id.textview_forecast_description_detail_fragment);
        humidityTextview = (TextView) v.findViewById(R.id.textview_humidity_detail_fragment);
        windTextview = (TextView) v.findViewById(R.id.textview_wind_detail_fragment);
        pressureTextview = (TextView) v.findViewById(R.id.textview_pressure_detail_fragment);
        image = (ImageView) v.findViewById(R.id.imageview_icon_detail_fragment);
        compassView = (CompassView) v.findViewById(R.id.compassview);

        //getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        actionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(item);
        if (forecastString != null) {
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

    void onLocationChanged(String newLocation) {
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updateUri;
            getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ForecastFragment.FORECAST_COLUMNS,
                    null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            boolean isMetric = Utility.isMetric(getActivity());
            long date = data.getLong(ForecastFragment.COL_WEATHER_DATE);
            String dayString = Utility.getDayName(getActivity(), date);
            String dateString = Utility.getFormattedMonthDay(getActivity(), date);
            dayTextview.setText(dayString);
            dateTextview.setText(dateString);

            double maxTemp = data.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
            maxTempTextview.setText(Utility.formatTemperature(getActivity(), maxTemp, isMetric));

            double minTemp = data.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
            minTempTextview.setText(Utility.formatTemperature(getActivity(), minTemp, isMetric));

            String description = data.getString(ForecastFragment.COL_WEATHER_DESC);
            descriptionTextview.setText(description);

            int humidityFormat = R.string.format_humidity;
            double humidity = data.getDouble(ForecastFragment.COL_WEATHER_HUMIDITY);
            humidityTextview.setText(String.format(getActivity().getString(humidityFormat), humidity));

            float windSpeed = data.getFloat(ForecastFragment.COL_WEATHER_WIND_SPEED);
            float degrees = data.getFloat(ForecastFragment.COL_WEATHER_DEGREES);
            String windString = Utility.getFormattedWind(getActivity(), windSpeed, degrees);
            windTextview.setText(windString);
            compassView.setAngle(degrees);

            float pressure = data.getFloat(ForecastFragment.COL_WEATHER_PRESSURE);
            int pressureFormatId = R.string.pressure_format;
            pressureTextview.setText(String.format(getActivity().getString(pressureFormatId), pressure));

            int weatherConditionId = data.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
            int resourceId = Utility.getArtResourceForWeatherCondition(weatherConditionId);
            image.setImageResource(resourceId);
        } else {
            dateTextview.setText("Unable to download results.");
        }
        if (actionProvider != null) {
            actionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dateTextview.setText("");
        forecastString = "";
    }
}
