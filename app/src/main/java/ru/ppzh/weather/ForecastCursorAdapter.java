package ru.ppzh.weather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ForecastCursorAdapter extends CursorAdapter {
    public static final String TAG = "ForecastCursorAdapter";

    private Context context;
    private ForecastCursor forecastCursor;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public ForecastCursorAdapter(Context context, ForecastCursor fc) {
        super(context, fc, 0);
        this.context = context;
        this.forecastCursor = fc;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();
        Forecast f = forecastCursor.getForecast();

        holder.city.setText(f.getCity() + ", " + f.getCountry());
        holder.description.setText(f.getDescription());
        holder.update.setText(f.getUpdated());
        double t = f.getTemperature();
        holder.temperature.setText((t >= 0 ? "+" : "") + String.format("%.2f", t)
                + " °C");
    }

    class ViewHolder {
        TextView city;
        TextView update;
        TextView temperature;
        TextView description;

        public ViewHolder(View view) {
            city = (TextView) view.findViewById(R.id.city_list_item);
            update = (TextView) view.findViewById(R.id.updated_list_item);
            temperature = (TextView) view.findViewById(R.id.temperature_list_item);
            description = (TextView) view.findViewById(R.id.description_list_item);
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        forecastCursor = (ForecastCursor) newCursor;
        return super.swapCursor(newCursor);
    }
}
