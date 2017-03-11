package tech.hanafi.weatherapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.hanafi.weatherapp.R;
import tech.hanafi.weatherapp.model.CardWeather;

/**
 * Created by han.afi on 10/3/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<CardWeather> weatherList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.imgWeatherForecastIcon)
        ImageView imgWeatherForecastIcon;
        @BindView(R.id.txtTime)
        TextView txtTime;
        @BindView(R.id.txtTemp)
        TextView txtTemp;
        @BindView(R.id.txtSummary)
        TextView txtSummary;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context mContext, List<CardWeather> weatherList) {
        this.mContext = mContext;
        this.weatherList = weatherList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CardWeather cw = weatherList.get(position);
        holder.imgWeatherForecastIcon.setImageResource(cw.getIconDrawableId());
        holder.txtTime.setText(cw.getTime());
        holder.txtTemp.setText(cw.getTemp() + (char) 0x00B0 + " C");
        holder.txtSummary.setText(cw.getSummary());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}


