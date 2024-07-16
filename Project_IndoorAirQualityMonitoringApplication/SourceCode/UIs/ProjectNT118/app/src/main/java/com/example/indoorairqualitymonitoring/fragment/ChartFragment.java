package com.example.indoorairqualitymonitoring.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.R;
import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.AssetService;
import com.example.indoorairqualitymonitoring.model.datapoint.AssetDatapointAttribute;
import com.example.indoorairqualitymonitoring.model.datapoint.AssetDatapointAttributeObject;
import com.example.indoorairqualitymonitoring.support.JsonDeserializer;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = ChartFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChartFragment() {
        // Required empty public constructor
    }
    private TextInputEditText edtEnding;
    private TextInputLayout textInputLayoutEnding, textInputLayoutTimeframe;
    private LineChart lineChart;
    private AutoCompleteTextView autoCompleteAttr, autoCompleteTimeframe;
    private Context context;
    private View view;
    private AssetService assetService;
    private String token;

    //List<AssetDatapointAttribute> lst = new ArrayList<>();
    private List<String> xLabels = new ArrayList<>();
    private static final String[] dropListAttr = new String[]{"Humidity (%)", "Rainfall (mm)", "Temperature (°C)", "Wind speed (km/h)"};
    private static final String[] dropListTimeframe = new String[]{"Hour", "Day", "Week", "Month", "Year"};
    private long fromTime, toTime;
    private String timeRange, attributeName;

    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getBaseContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chart, container, false);

        initiate();

        // Set attribute
        autoCompleteAttr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toTime = Calendar.getInstance().getTimeInMillis();

                // Set current date and time by default in time-ending box
                Date date = new Date(toTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.getDefault());
                edtEnding.setText(sdf.format(date));

                if (textInputLayoutEnding.getVisibility() == View.INVISIBLE && textInputLayoutTimeframe.getVisibility() == View.INVISIBLE)
                {
                    // Show time frame box and time-ending box in the first time
                    textInputLayoutEnding.setVisibility(View.VISIBLE);
                    textInputLayoutTimeframe.setVisibility(View.VISIBLE);

                    // By default, time range is DAY
                    timeRange = "day";
                }

                // Choose attribute
                switch (position)
                {
                    case 0:
                        attributeName = "humidity";
                        break;

                    case 1:
                        attributeName = "rainfall";
                        break;

                    case 2:
                        attributeName = "temperature";
                        break;

                    case 3:
                        attributeName = "windSpeed";
                        break;
                }

                // Display line chart
                xLabels = getXLabels(timeRange);
                displayAttribute(fromTime, toTime, attributeName);
            }
        });

        // Set time range
        autoCompleteTimeframe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toTime = Calendar.getInstance().getTimeInMillis();

                // Set current date and time by default in time-ending box
                Date date = new Date(toTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.getDefault());
                edtEnding.setText(sdf.format(date));

                switch (position)
                {
                    case 0:
                        timeRange = "hour";
                        break;

                    case 1:
                        timeRange = "day";
                        break;

                    case 2:
                        timeRange = "week";
                        break;

                    case 3:
                        timeRange = "month";
                        break;

                    case 4:
                        timeRange = "year";
                        break;
                }

                // Display line chart
                xLabels = getXLabels(timeRange);
                displayAttribute(fromTime, toTime, attributeName);
            }
        });

        textInputLayoutEnding.setEndIconOnClickListener(new View.OnClickListener() {
            Calendar now = Calendar.getInstance();
            int day = now.get(Calendar.DAY_OF_MONTH);
            int month = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));

                        toTime = chosen.getTimeInMillis();

                        Date date = new Date(toTime);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                        edtEnding.setText(sdf.format(date));

                        // Display line chart
                        xLabels = getXLabels(timeRange);
                        displayAttribute(fromTime, toTime, attributeName);
                    }
                }, year, month, day);

                dialog.show();
            }
        });

        return view;
    }

    private void displayAttribute(long fromTime, long toTime, String attributeName) {
        AssetDatapointAttributeObject body = new AssetDatapointAttributeObject(getResources().getString(R.string.typeDatapoint), fromTime, toTime,100);

        Call<JsonArray> call = assetService.getAssetDatapointAttribute("Bearer " + token, getResources().getString(R.string.defaultWeatherAssetID), attributeName, body);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    // Deserialize JSON array
                    List<AssetDatapointAttribute> lstObject;
                    String jsonString = response.body().toString();
                    Type lstType = new TypeToken<List<AssetDatapointAttribute>>() {}.getType();
                    lstObject = JsonDeserializer.getListFromJsonArray(jsonString, lstType);

                    ArrayList<Entry> entries = new ArrayList<>();

                    // Collect data
                    if (timeRange.equals("year"))
                    {
                        float x = 0;
                        int y_index = 0;
                        long interval = 0, from = fromTime;

                        while (y_index < lstObject.size())
                        {
                            long measured = lstObject.get(y_index).getTimestamp();
                            float y_val = Float.parseFloat(String.valueOf(lstObject.get(y_index).getAttributeValue()));

                            // Get month at measurement time
                            Date date = new Date(measured);
                            int month = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(date));

                            // Get year at measurement time
                            int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date));

                            switch (month)
                            {
                                case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                                interval = 31L * 86400000; // These months have 31 days
                                break;

                                case 4: case 6: case 9: case 11:
                                interval = 30L * 86400000; // These months have 30 days
                                break;

                                case 2:
                                {
                                    if (IsLeapYear(year))
                                    {
                                        interval = 29L * 86400000; // Feb in leap year has 29 days
                                    }
                                    else
                                    {
                                        interval = 28L * 86400000; // Feb in normal year has 28 days
                                    }

                                    break;
                                }
                            }

                            double step = (double) (measured - from) / interval;
                            entries.add(new Entry(Float.parseFloat(String.valueOf(x + step)), y_val));

                            y_index++;
                        }
                    }
                    else
                    {
                        float x = 0;
                        int y_index = 0;
                        long interval = 0, from = fromTime;

                        if (timeRange.equals("hour")) interval = 300000;
                        if (timeRange.equals("day")) interval = 3600000;
                        if (timeRange.equals("week")) interval = 43200000;
                        if (timeRange.equals("month")) interval = 172800000;

                        while (y_index < lstObject.size())
                        {
                            long measured = lstObject.get(y_index).getTimestamp();
                            float y_val = Float.parseFloat(String.valueOf(lstObject.get(y_index).getAttributeValue()));

                            if (measured < from + interval)
                            {
                                double step = (double) (measured - from) / interval;
                                entries.add(new Entry(Float.parseFloat(String.valueOf(x + step)), y_val));
                            }
                            else
                            {
                                x++;
                                from += interval;
                                double step = (double) (measured - from) / interval;
                                entries.add(new Entry(Float.parseFloat(String.valueOf(x + step)), y_val));
                            }

                            y_index++;
                        }
                    }

                    // Customize line chart
                    lineChart.animateY(1000, Easing.EaseInOutCubic);
                    lineChart.setDescription(null);
                    lineChart.getAxisRight().setEnabled(false);

                    // Customize data set
                    LineDataSet dataSet = new LineDataSet(entries,"");
                    if (attributeName.equals("humidity"))
                    {
                        dataSet.setLabel(getResources().getString(R.string.humidity));
                    }
                    else if (attributeName.equals("rainfall"))
                    {
                        dataSet.setLabel(getResources().getString(R.string.rainfall));
                    }
                    else if (attributeName.equals("temperature"))
                    {
                        dataSet.setLabel(getResources().getString(R.string.temperature));
                    }
                    else
                    {
                        dataSet.setLabel(getResources().getString(R.string.windSpeed));
                    }

                    dataSet.setCircleRadius(4);
                    dataSet.setDrawCircleHole(false);
                    dataSet.setCircleColor(Color.parseColor("#476930"));
                    dataSet.setLineWidth(2);
                    dataSet.setColor(Color.RED);
                    dataSet.setDrawValues(false);
                    dataSet.setDrawFilled(true);
                    dataSet.setFillColor(Color.CYAN);

                    // Customize legend
                    Legend legend = lineChart.getLegend();
                    legend.setTextSize(20);
                    legend.setForm(Legend.LegendForm.LINE);
                    legend.setFormSize(20);
                    legend.setFormToTextSpace(10);

                    // Customize X-axis
                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelRotationAngle(-60);
                    xAxis.setTextSize(10);
                    xAxis.setGridLineWidth(2);
                    xAxis.setAxisMinimum(0);
                    xAxis.setAxisMaximum(xLabels.size() - 1);
                    xAxis.setValueFormatter(new MyAxisValueFormatter(xLabels));

                    // Customize Y-axis
                    YAxis yAxis = lineChart.getAxisLeft();
                    yAxis.setTextSize(12);
                    yAxis.setGridLineWidth(2);
                    yAxis.setAxisMinimum(0);
                    yAxis.setLabelCount(8);

                    // Build a chart
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet);
                    LineData line = new LineData(dataSets);
                    lineChart.setData(line);
                    lineChart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    private void initiate()
    {
        // Reference to ID of the widget
        edtEnding = view.findViewById(R.id.edtEnding);
        textInputLayoutEnding = view.findViewById(R.id.textInputLayoutEnding);
        textInputLayoutTimeframe = view.findViewById(R.id.textInputLayoutTimeframe);
        lineChart = view.findViewById(R.id.lineChart);
        autoCompleteAttr = view.findViewById(R.id.autoCompleteAttr);
        autoCompleteTimeframe = view.findViewById(R.id.autoCompleteTimeframe);

        // Initialize adapter
        ArrayAdapter<String> adapterAttr = new ArrayAdapter<>(context, R.layout.dropbox_item, dropListAttr);
        ArrayAdapter<String> adapterTimeframe = new ArrayAdapter<>(context, R.layout.dropbox_item, dropListTimeframe);
        autoCompleteAttr.setAdapter(adapterAttr);
        autoCompleteTimeframe.setAdapter(adapterTimeframe);

        // Create instance to call API
        assetService = ApiClient.getClient("https://uiot.ixxc.dev/").create(AssetService.class);

        // Setup line chart
        lineChart.setNoDataText("Please choose an attribute!");
        lineChart.setNoDataTextColor(Color.BLACK);

        // Get token
        token = HomeScreen.sessionManager.getToken();
    }

    private List<String> getXLabels(String timeRange)
    {
        List<String> labels = new ArrayList<>();

        if (timeRange.equals("hour"))
        {
            fromTime = toTime - 3600000;
            long from = fromTime;
            long to = toTime;

            while (from <= to)
            {
                Date date = new Date(from);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                labels.add(sdf.format(date));

                // Interval is 5 minutes
                from += 300000;
            }
        }
        else if (timeRange.equals("day"))
        {
            // Round destination time to nearest hour
            toTime = (toTime / 3600000 + 1) * 3600000;
            fromTime = toTime - 86400000;
            long from = fromTime, to = toTime;

            while (from <= to)
            {
                Date date = new Date(from);
                SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
                labels.add(sdf.format(date) + ":00");

                // Interval is an hour
                from += 3600000;
            }
        }
        else if (timeRange.equals("week"))
        {
            // Round destination time to nearest hour
            toTime = (toTime / 3600000 + 1) * 3600000;
            fromTime = toTime - 604800000;
            long from = fromTime, to = toTime;

            while (from <= to)
            {
                Date date = new Date(from);
                SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
                labels.add(sdf.format(date) + ":00");

                // Interval is 12 hours
                from += 43200000;
            }
        }
        else if (timeRange.equals("month"))
        {
            Calendar thisDateAMonthAgo = Calendar.getInstance();
            Calendar currDate = Calendar.getInstance();

            // Destination time is 12:00:00 AM the next day
            currDate.set(Calendar.HOUR_OF_DAY, 0);
            currDate.set(Calendar.MINUTE, 0);
            currDate.set(Calendar.SECOND, 0);
            toTime = currDate.getTimeInMillis() + 86400000;

            thisDateAMonthAgo.set(Calendar.DAY_OF_MONTH, currDate.get(Calendar.DAY_OF_MONTH));
            thisDateAMonthAgo.set(Calendar.MONTH, currDate.get(Calendar.MONTH) - 1);
            thisDateAMonthAgo.set(Calendar.YEAR, currDate.get(Calendar.YEAR));
            thisDateAMonthAgo.set(Calendar.HOUR_OF_DAY, 0);
            thisDateAMonthAgo.set(Calendar.MINUTE, 0);
            thisDateAMonthAgo.set(Calendar.SECOND, 0);
            fromTime = thisDateAMonthAgo.getTimeInMillis() + 86400000;

            long from = fromTime, to = toTime;

            while (from <= to)
            {
                Date date = new Date(from);
                SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
                labels.add(sdf.format(date));

                // Interval is 2 days
                from += 172800000;
            }
        }
        else
        {
            Calendar firstDateOfNewYear = Calendar.getInstance();
            Calendar firstDateOfThisYear = Calendar.getInstance();
            Calendar currDate = Calendar.getInstance();
            int thisYear = currDate.get(Calendar.YEAR);

            // Destination time is the beginning of new year
            firstDateOfNewYear.set(Calendar.DAY_OF_MONTH, 1);
            firstDateOfNewYear.set(Calendar.MONTH, 0);
            firstDateOfNewYear.set(Calendar.YEAR, thisYear + 1);
            firstDateOfNewYear.set(Calendar.HOUR_OF_DAY, 0);
            firstDateOfNewYear.set(Calendar.MINUTE, 0);
            firstDateOfNewYear.set(Calendar.SECOND, 0);

            firstDateOfThisYear.set(Calendar.DAY_OF_MONTH, 1);
            firstDateOfThisYear.set(Calendar.MONTH, 0);
            firstDateOfThisYear.set(Calendar.YEAR, thisYear);
            firstDateOfThisYear.set(Calendar.HOUR_OF_DAY, 0);
            firstDateOfThisYear.set(Calendar.MINUTE, 0);
            firstDateOfThisYear.set(Calendar.SECOND, 0);

            fromTime = firstDateOfThisYear.getTimeInMillis();
            toTime = firstDateOfNewYear.getTimeInMillis();
            long from = fromTime, to = toTime, interval = 0;
            int monthThisYear = 1;

            while (from <= to)
            {
                Date date = new Date(from);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                labels.add(sdf.format(date));

                switch (monthThisYear)
                {
                    case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                        interval = 31L * 86400000; // These months have 31 days
                        break;

                    case 4: case 6: case 9: case 11:
                        interval = 30L * 86400000; // These months have 30 days
                        break;

                    case 2:
                    {
                        if (IsLeapYear(thisYear))
                        {
                            interval = 29L * 86400000; // Feb in leap year has 29 days
                        }
                        else
                        {
                            interval = 28L * 86400000; // Feb in normal year has 28 days
                        }

                        break;
                    }
                }

                from += interval;
                monthThisYear++;
            }
        }

        return labels;
    }

    private boolean IsLeapYear(int year)
    {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    private class MyAxisValueFormatter extends IndexAxisValueFormatter
    {
        private List<String> values;
        public MyAxisValueFormatter(List<String> values) {
            this.values = values;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = Math.round(value);

            if (index >= values.size()) {
                index = values.size() - 1;
            }

            axis.setLabelCount(values.size(),true);
            return values.get(index);
        }
    }
}