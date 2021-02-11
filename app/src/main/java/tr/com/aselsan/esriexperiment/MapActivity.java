package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import es.dmoral.toasty.Toasty;

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
        setLoadStatus(map);
        mapView.setViewpoint(new Viewpoint(34.056295, -117.195800, 10000));

        mapView.addMapRotationChangedListener(m -> {
            Log.d(TAG, "Map rotation changed! " + m.getSource().getRotation());
        });

        mapView.addMapScaleChangedListener(m -> {
            Log.d(TAG, "Map scale changed! " + m.getSource().getMapScale());
        });

    }
    
    private void setLoadStatus(ArcGISMap map){
        // Listener on change in map load status
        map.addLoadStatusChangedListener(loadStatusChangedEvent -> {
            String mapLoadStatus;
            mapLoadStatus = loadStatusChangedEvent.getNewLoadStatus().name();
            // map load status can be any of LOADING, FAILED_TO_LOAD, NOT_LOADED or LOADED
            // set the status in the TextView accordingly
            switch (mapLoadStatus) {
                case "LOADING":
                    Toasty.info(this, "Map Loading!", Toast.LENGTH_SHORT, true).show();
                    break;

                case "FAILED_TO_LOAD":
                    Toasty.error(this, "Map Load Failed!", Toast.LENGTH_SHORT, true).show();
                    break;

                case "NOT_LOADED":
                    Toasty.warning(this, "Map Not Loaded!", Toast.LENGTH_SHORT, true).show();
                    break;

                case "LOADED":
                    Toasty.success(this, "Map Loaded!", Toast.LENGTH_SHORT, true).show();
                    break;

                default:
                    Toasty.normal(this, "Something strange!").show();
                    break;
            }

            Log.d(TAG, mapLoadStatus);
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
