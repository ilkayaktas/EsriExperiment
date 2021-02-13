package tr.com.aselsan.esriexperiment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import es.dmoral.toasty.Toasty;

/**
 * Created by ilkayaktas on 2/13/21 at 6:04 PM.
 */

public class OfflineMapActivity  extends AppCompatActivity {

    public static final String API_KEY = "AAPK316414d3c0bb45ac8d219331ff89819a_O4tHEfo2H3ytTW2BT8iIyrH5hY-8K2FLOAl2JwfuobtWyTk_2tuA00E6gnrUv9x";

    private static final String TAG = MainActivity.class.getSimpleName();

    private MapView mapView;
    // objects that implement Loadable must be class fields to prevent being garbage collected before loading
    private MobileMapPackage mMapPackage;
    private GraphicsOverlay graphicsOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinemap);

        // get a reference to the map view
        mapView = findViewById(R.id.mapView);

        // create the mobile map package
        mMapPackage = new MobileMapPackage(getExternalFilesDir(null) +  "/Yellowstone.mmpk");
        // load the mobile map package asynchronously
        mMapPackage.loadAsync();

        // add done listener which will invoke when mobile map package has loaded
        mMapPackage.addDoneLoadingListener(() -> {
            // check load status and that the mobile map package has maps
            if (mMapPackage.getLoadStatus() == LoadStatus.LOADED && !mMapPackage.getMaps().isEmpty()) {
                // add the map from the mobile map package to the MapView
                mapView.setMap(mMapPackage.getMaps().get(0));
            } else {
                String error = "Error loading mobile map package: " + mMapPackage.getLoadError().toString();
                Log.e(TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        addMapClicked();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addMapClicked() {
        // add onTouchListener to get the location of the user tap
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mapView) {
            @Override
            public void onLongPress(MotionEvent motionEvent) {
                super.onLongPress(motionEvent);

                SimpleMarkerSymbol locationMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.GREEN, 30);

                android.graphics.Point clickLocation = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));
                Point mapPoint = mMapView.screenToLocation(clickLocation);
                // WGS84 displays lotitude longitude
                Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                Graphic po = new Graphic(mapPoint, locationMarker);
                graphicsOverlay.getGraphics().add(po);

                Toasty.info(OfflineMapActivity.this, wgs84Point.getX()+" "+wgs84Point.getY(), Toasty.LENGTH_SHORT).show();
            }


        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }

}
