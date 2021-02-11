package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by ilkayaktas on 2/11/21 at 1:41 PM.
 */

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ArcGISRuntimeEnvironment.setApiKey(MainActivity.API_KEY);
        mapView = findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(34.056295, -117.195800, 10000));

        mapView.addMapRotationChangedListener(m -> {
            Log.d(TAG, "Map rotation changed! " + m.getSource().getRotation());
        });

        mapView.addMapScaleChangedListener(m -> {
            Log.d(TAG, "Map scale changed! " + m.getSource().getMapScale());
        });

    }

    @Override
    protected void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        mapView.dispose();
        super.onDestroy();
    }

}
