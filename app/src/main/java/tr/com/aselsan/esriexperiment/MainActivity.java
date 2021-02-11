package tr.com.aselsan.esriexperiment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "AAPK316414d3c0bb45ac8d219331ff89819a_O4tHEfo2H3ytTW2BT8iIyrH5hY-8K2FLOAl2JwfuobtWyTk_2tuA00E6gnrUv9x";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openMapView(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }
}