package tr.com.aselsan.esriexperiment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import es.dmoral.toasty.Toasty;

import java.util.Arrays;

/**
 * Created by ilkayaktas on 2/11/21 at 1:41 PM.
 */

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private MapView mapView;
    double timeRef;
    private Graphic startLocation;
    private Graphic endLocation;
    private Point startPoint;
    private final LinearUnit unitOfMeasurement = new LinearUnit(LinearUnitId.METERS);
    private Graphic path;
    private GraphicsOverlay graphicsOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ArcGISRuntimeEnvironment.setApiKey(MainActivity.API_KEY);
        mapView = findViewById(R.id.mapView);

        ArcGISMap map = new ArcGISMap(BasemapStyle.OSM_STANDARD_RELIEF_BASE);
        map.setMinScale(50000);
        map.setMaxScale(500);

        mapView.setMap(map);
        setLoadStatus(map);

        timeRef = System.currentTimeMillis();
        // Initial map are
        // Setting the initial viewpoint is useful when a user wishes to first load the map at a particular area of interest.
        /*Envelope initialExtent = new Envelope(-12211308.778729, 4645116.003309, -12208257.879667, 4650542.535773,
                SpatialReference.create(102100));
        Viewpoint viewpoint = new Viewpoint(initialExtent);
        mapView.setViewpoint(viewpoint);*/

        // Initial map location
        mapView.setViewpoint(new Viewpoint(39.9969, 32.7517, 4000));

        mapView.addMapRotationChangedListener(m -> {
            Log.d(TAG, "Map rotation changed! " + m.getSource().getRotation());
        });

        mapView.addMapScaleChangedListener(m -> {
            Log.d(TAG, "Map scale changed! " + m.getSource().getMapScale());
        });

        // Add calculation points
        // create a graphic overlay
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        // Start point
        startPoint = new Point(32.7517, 39.9969, SpatialReferences.getWgs84());
        SimpleMarkerSymbol locationMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.CYAN, 30);
        startLocation = new Graphic(startPoint, locationMarker);
        graphicsOverlay.getGraphics().add(startLocation);

        // create graphic for the destination. currently not visualized
        endLocation = new Graphic();
        endLocation.setSymbol(locationMarker);
        graphicsOverlay.getGraphics().add(endLocation);

        // create graphic representing the geodesic path between the two locations
        path = new Graphic();
        path.setSymbol(new SimpleLineSymbol(SimpleLineSymbol.Style.SHORT_DASH, Color.RED, 5));
        graphicsOverlay.getGraphics().add(path);

        addMapClicked();

        showGrid();
    }

    private void showGrid() {
        LatitudeLongitudeGrid grid = new LatitudeLongitudeGrid();
        mapView.setGrid(grid);
    }

    private void centerPointAndRorate(Point mapPoint){
        Viewpoint center = new Viewpoint(mapPoint, 2000);
        mapView.setViewpointAsync(center, 0.5f, AnimationCurve.EASE_IN_OUT_SINE);
        //mapView.setViewpointCenterAsync(mapPoint, 5000);
        mapView.setViewpointRotationAsync(Math.random()*100);
    }

    private void showCallout(Point wgs84Point){
        TextView calloutContent = new TextView(getApplicationContext());
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setSingleLine();
        // format coordinates to 4 decimal places
        calloutContent.setText("Lat: " + String.format("%.4f", wgs84Point.getY()) + ", Lon: " + String.format("%.4f", wgs84Point.getX()));

        // get callout, set content and show
        Callout mCallout = mapView.getCallout();
        mCallout.setLocation(wgs84Point);
        mCallout.setContent(calloutContent);
        mCallout.show();

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

                centerPointAndRorate(mapPoint);
                showCallout(wgs84Point);
                Toasty.info(MapActivity.this, wgs84Point.getX()+" "+wgs84Point.getY(), Toasty.LENGTH_SHORT).show();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                // get the point that was clicked and convert it to a point in the map
                android.graphics.Point clickLocation = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));
                Point mapPoint = mMapView.screenToLocation(clickLocation);

                final Point destinationPoint = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                endLocation.setGeometry(destinationPoint);

                // create a straight line path between the start and end locations
                PointCollection points = new PointCollection(Arrays.asList(startPoint, destinationPoint), SpatialReferences.getWgs84());
                Polyline polyline = new Polyline(points);
                // densify the path as a geodesic curve and show it with the path graphic
                Geometry pathGeometry = GeometryEngine.densifyGeodetic(polyline, 1, unitOfMeasurement, GeodeticCurveType.GEODESIC);
                path.setGeometry(pathGeometry);


                // calculate the path distance
                double distance = GeometryEngine.lengthGeodetic(pathGeometry, unitOfMeasurement, GeodeticCurveType.GEODESIC);

                // create a textview for the callout
                TextView calloutContent = new TextView(getApplicationContext());
                calloutContent.setTextColor(Color.BLACK);
                calloutContent.setSingleLine();
                // format coordinates to 2 decimal places
                calloutContent.setText("Distance: " + String.format("%.2f", distance) + " meters");
                final Callout callout = mMapView.getCallout();
                callout.setLocation(mapPoint);
                callout.setContent(calloutContent);
                callout.show();
                return true;

            }
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
                    double loadTime = System.currentTimeMillis();
                    Toasty.success(this, "Map Loaded in " + (loadTime - timeRef)/1000 + " ms!", Toast.LENGTH_SHORT, true).show();
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
