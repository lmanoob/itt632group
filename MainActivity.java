package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter adapter;
    private ArrayList<DataItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        data = new ArrayList<>();
        adapter = new CustomAdapter(data);
        listView.setAdapter(adapter);

        // Make an HTTP request to fetch data from PHP server
        new GetDataTask().execute();
    }

    private void sendDataToPhp(DataItem item) {
        // Make an HTTP request to send data back to PHP server
        new SendDataTask().execute(item);
    }

    private class CustomAdapter extends ArrayAdapter<DataItem> {

        public CustomAdapter(ArrayList<DataItem> data) {
            super(MainActivity.this, 0, data);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            final DataItem item = getItem(position);

            TextView carPlateTextView = convertView.findViewById(R.id.carPlateTextView);
            carPlateTextView.setText(item.getCarPlate());

            Button sendButton = convertView.findViewById(R.id.sendButton);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataToPhp(item);
                }
            });

            return convertView;
        }

    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://206.189.151.246:80/read.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Process the received data here
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject itemObject = jsonArray.getJSONObject(i);
                        String carPlate = itemObject.getString("car_plate");

                        DataItem item = new DataItem(carPlate);
                        data.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class SendDataTask extends AsyncTask<DataItem, Void, String> {

        @Override
        protected String doInBackground(DataItem... items) {
            try {
                DataItem item = items[0];
                URL url = new URL("http://206.189.151.246:80/delete.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                // Construct the data to send
                String dataToSend = "carPlate=" + item.getCarPlate();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(dataToSend.getBytes());
                outputStream.flush();
                outputStream.close();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                inputStream.close();
                connection.disconnect();

                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Process the response from the PHP page if needed
                Toast.makeText(MainActivity.this, "Data sent successfully", Toast.LENGTH_SHORT).show();

                data.clear();
                new GetDataTask().execute();
            } else {
                Toast.makeText(MainActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class DataItem {
        private String carPlate;
        public DataItem(String carPlate) {
            this.carPlate = carPlate;
        }

        public String getCarPlate() {
            return carPlate;
        }

        public void setCarPlate(String carPlate) {
            this.carPlate = carPlate;
        }
    }
}
