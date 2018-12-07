package com.example.dell.chargetimer.cars;

import android.app.Activity;
import android.util.Log;

import com.example.dell.chargetimer.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarsProvider {
    private final String TAG = CarsProvider.class.getSimpleName();
    private final Activity activity;

    public CarsProvider(Activity activity) {
        this.activity = activity;
    }


    public ArrayList<Car> Get(){
        InputStream is = activity.getResources().openRawResource(R.raw.ev_cars);
        String carsString = readToString(is);
        return convertFromJson(carsString);
    }

    private String readToString(InputStream stream){
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception while using CarsProvider", e);
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while using CarsProvider", e);
            }
        }

        return writer.toString();
    }

    private ArrayList<Car> convertFromJson(String carsJson){
        ArrayList<Car> carList = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(carsJson);
            JSONArray cars = jsonObj.getJSONArray("ev_cars");

            for (int i = 0; i < cars.length(); i++) {
                JSONObject c = cars.getJSONObject(i);

                Car car = new Car();
                car.Id = c.getString("id");
                car.Manufacture = c.getString("manufacture");
                car.Model = c.getString("model");
                car.Capacity = c.getDouble("capacity");
                car.UsableCapacity = c.getDouble("usableCapacity");

                carList.add(car);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }

        return carList;
    }
}
