package tr.com.aselsan.esriexperiment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.LightingMode;
import com.esri.arcgisruntime.mapping.view.SceneView;

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
