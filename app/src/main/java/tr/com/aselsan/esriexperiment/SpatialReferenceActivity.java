package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by ilkayaktas on 2/12/21 at 2:22 PM.
 */

public class SpatialReferenceActivity extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // get a reference to the map view
        mMapView = findViewById(R.id.mapView);

        // create a map with World_Bonne projection
        ArcGISMap map = new ArcGISMap(SpatialReference.create(54024));

        //Adding a map image layer which can re-project itself to the map's spatial reference
        ArcGISMapImageLayer mapImageLayer = new ArcGISMapImageLayer("https://sampleserver6.arcgisonline.com/arcgis/rest/services/SampleWorldCities/MapServer");

        // set the map image layer as basemap
        Basemap basemap = new Basemap(mapImageLayer);

        // add the basemap to the map
        map.setBasemap(basemap);

        // set the map to be displayed in this view
        mMapView.setMap(map);
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }
}