package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.UnitSystem;
import com.esri.arcgisruntime.geoanalysis.LocationDistanceMeasurement;
import com.esri.arcgisruntime.geometry.Distance;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.*;

/**
 * Created by ilkayaktas on 2/11/21 at 2:00 PM.
 */

public class SceneActivity extends AppCompatActivity {

    private static final String TAG = "SceneActivity";
    private SceneView mSceneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        ArcGISScene scene = new ArcGISScene();
        scene.setBasemap(Basemap.createImageryWithLabels());

        mSceneView = findViewById(R.id.sceneView);
        mSceneView.setScene(scene);
        //[DocRef: END]

//        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
        ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/TopoBathy3D/ImageServer");
        scene.getBaseSurface().getElevationSources().add(elevationSource);

        Camera camera = new Camera(40.4, 32.9, 10010.0, 10.0, 80.0, 0.0);
        mSceneView.setViewpointCamera(camera);

        mSceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);

        mSceneView.setSunLighting(LightingMode.LIGHT_AND_SHADOWS);

        // create analysis overlay and add it to scene
        AnalysisOverlay analysisOverlay = new AnalysisOverlay();
        mSceneView.getAnalysisOverlays().add(analysisOverlay);

        // Calculate distance
        Point start = new Point(-4.494677, 48.384472, 24.772694, SpatialReferences.getWgs84());
        Point end = new Point(-4.495646, 48.384377, 58.501115, SpatialReferences.getWgs84());
        LocationDistanceMeasurement distanceMeasurement = new LocationDistanceMeasurement(start, end);
        distanceMeasurement.setUnitSystem(UnitSystem.METRIC);
        analysisOverlay.getAnalyses().add(distanceMeasurement);

        distanceMeasurement.addMeasurementChangedListener(measurementChangedEvent -> {
            Distance directDistance = distanceMeasurement.getDirectDistance();
            Distance verticalDistance = distanceMeasurement.getVerticalDistance();
            Distance horizontalDistance = distanceMeasurement.getHorizontalDistance();

            Log.d(TAG, "Distance " + directDistance.getUnit().getDisplayName() + " " + directDistance.getValue() + " " + verticalDistance.getValue() + " " + horizontalDistance.getValue());
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        // pause SceneView
        mSceneView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // resume SceneView
        mSceneView.resume();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        // dispose SceneView
        mSceneView.dispose();
    }
}
