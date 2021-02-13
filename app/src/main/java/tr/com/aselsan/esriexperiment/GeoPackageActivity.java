package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.GeoPackageFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.raster.GeoPackageRaster;

/**
 * Created by ilkayaktas on 2/14/21 at 1:02 AM.
 */

public class GeoPackageActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private MapView mapView;
    // objects that implement Loadable must be class fields to prevent being garbage collected before loading
    private GeoPackage mGeoPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geopackage);

        // authentication with an API key or named user is required to access basemaps and other
        // location services
        ArcGISRuntimeEnvironment.setApiKey(MainActivity.API_KEY);

        // inflate MapView from layout
        mapView = findViewById(R.id.mapView);

        // create a new map centered on Aurora Colorado and add it to the map view
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint( 39.7294, -104.8319, 1000000));

        // open and load the GeoPackage
        mGeoPackage = new GeoPackage(getExternalFilesDir(null) + "/AuroraCO.gpkg");
        mGeoPackage.loadAsync();
        mGeoPackage.addDoneLoadingListener(() -> {
            if (mGeoPackage.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                String error = "Geopackage failed to load: " + mGeoPackage.getLoadError();
                Log.e(TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                return;
            }

            // loop through each GeoPackageRaster
            for (GeoPackageRaster geoPackageRaster : mGeoPackage.getGeoPackageRasters()) {
                // create a RasterLayer from the GeoPackageRaster
                RasterLayer rasterLayer = new RasterLayer(geoPackageRaster);

                // set the opacity on the RasterLayer to partially visible
                rasterLayer.setOpacity(0.55f);

                // add the layer to the map
                mapView.getMap().getOperationalLayers().add(rasterLayer);
            }

            // loop through each GeoPackageFeatureTable
            for (GeoPackageFeatureTable geoPackageFeatureTable : mGeoPackage.getGeoPackageFeatureTables()) {
                // create a FeatureLayer from the GeoPackageFeatureLayer
                FeatureLayer featureLayer = new FeatureLayer(geoPackageFeatureTable);

                // add the layer to the map
                mapView.getMap().getOperationalLayers().add(featureLayer);
            }
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