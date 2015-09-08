package udatraining.dyomin.com.udaproj1;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.Test;

import udatraining.dyomin.com.udaproj1.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private final int VIEW_TYPE_COUNT = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forecast;
        }

        View v = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(holder);
        return v;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.iconView.setImageResource(R.mipmap.ic_launcher);

        String date = Utility.getFriendlyDayString(context, cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        holder.dateView.setText(date);

        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        holder.descriptionView.setText(forecast);

        boolean isMetric = Utility.isMetric(context);
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        holder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}