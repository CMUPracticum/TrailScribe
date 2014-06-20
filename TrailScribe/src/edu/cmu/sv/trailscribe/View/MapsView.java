package edu.cmu.sv.trailscribe.View;

import java.io.IOException;
import java.util.Map;

import com.nutiteq.MapView;
//import com.nutiteq.advancedmap.R;
import com.nutiteq.components.Components;
import com.nutiteq.components.Envelope;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Options;
import com.nutiteq.layers.raster.GdalDatasetInfo;
import com.nutiteq.layers.raster.GdalMapLayer;
import com.nutiteq.log.Log;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.utils.UnscaledBitmapLoader;

import edu.cmu.sv.trailscribe.R;
import edu.cmu.sv.trailscribe.Controller.MapsController;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import android.widget.ZoomControls;

// Inspired by nutiteq demo: https://github.com/nutiteq/hellomap3d/blob/master/AdvancedMap3D/src/main/java/com/nutiteq/advancedmap/activity/RasterFileMapActivity.java

public class MapsView extends Activity {
	 private MapView mapView;
	 private MapsController mController;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // spinner in status bar, for progress indication
	        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

	        setContentView(R.layout.maps_view);
	        mapView = (MapView) findViewById(R.id.mapView);
	        
	        // enable logging for troubleshooting - optional
	        // TODO: Set it at application level
	        Log.enableAll();
	        Log.setTag("gdal");

	        loadMap();
	    }

		private void loadMap() {
			restoreMapState();
			
			String mapFilePath = Environment.getExternalStorageDirectory().getPath() + "/trailscribe/samplemap1_wgs84_compressed.tif";

	        try {
	            GdalMapLayer gdalLayer = new GdalMapLayer(new EPSG3857(), 0, 18, 9, mapFilePath, mapView, true);
	            gdalLayer.setShowAlways(true);
	            mapView.getLayers().setBaseLayer(gdalLayer);
	            Map<Envelope, GdalDatasetInfo> dataSets = gdalLayer.getDatasets();
	            if(!dataSets.isEmpty()){
	                GdalDatasetInfo firstDataSet = (GdalDatasetInfo) dataSets.values().toArray()[0];

	                MapPos centerPoint = new MapPos((firstDataSet.envelope.maxX+firstDataSet.envelope.minX)/2,
	                        (firstDataSet.envelope.maxY+firstDataSet.envelope.minY)/2);


	                Log.debug("found extent "+firstDataSet.envelope+", zoom "+firstDataSet.bestZoom+", centerPoint "+centerPoint);

	                mapView.setFocusPoint(centerPoint);
	                mapView.setZoom((float) firstDataSet.bestZoom);
	            }else{
	                Log.debug("no dataset info");
	                Toast.makeText(this, "No dataset info", Toast.LENGTH_LONG).show();

	                mapView.setFocusPoint(new MapPos(0,0));
	                mapView.setZoom(1.0f);

	            }

	            // Activate some mapview options to make it smoother - optional
	            configureMapView();

	            // set sky bitmap - optional, default - white
	            setSkyBitMap();
	            
	            // Map background, visible if no map tiles loaded - optional, default - white
	            setMapBackground();

	            // configure texture caching - optional, suggested
	            configureTextureCaching();

	            // set persistent raster cache limit to 100MB
	            mapView.getOptions().setPersistentCacheSize(100 * 1024 * 1024);

	            // 4. zoom buttons using Android widgets - optional
	            setZoomControls();
	            

	        } catch (IOException e) {
	            Toast.makeText(this, "ERROR "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
	        }
		}
		
		private void restoreMapState() {
			 // Restore map state during device rotation,
	        // it is saved in onRetainNonConfigurationInstance() below
	        Components retainObject = (Components) getLastNonConfigurationInstance();
	        if (retainObject != null) {
	            // just restore configuration, skip other initializations
	            mapView.setComponents(retainObject);
	            return;
	        } else {
	            // 2. create and set MapView components - mandatory
	            Components components = new Components();
	            
	            // set stereo view: works if you rotate to landscape and device has HTC 3D or LG Real3D
	            mapView.setComponents(components);
	        }			
		}

		private void configureMapView() {
			 // rotation - 0 = north-up
            mapView.setMapRotation(0f);
            // tilt means perspective view. Default is 90 degrees for "normal" 2D map view, minimum allowed is 30 degrees.
            mapView.setTilt(90.0f);

			mapView.getOptions().setPreloading(false);
			mapView.getOptions().setSeamlessHorizontalPan(true);
			mapView.getOptions().setTileFading(false);
			mapView.getOptions().setKineticPanning(true);
			mapView.getOptions().setDoubleClickZoomIn(true);
			mapView.getOptions().setDualClickZoomOut(true);
		}

		private void setZoomControls() {
			// get the zoomcontrols that was defined in main.xml
            ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
            // set zoomcontrols listeners to enable zooming
            zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    mapView.zoomIn();
                }
            });
            zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    mapView.zoomOut();
                }
            });
			
		}

		private void configureTextureCaching() {
			mapView.getOptions().setTextureMemoryCacheSize(20 * 1024 * 1024);
			mapView.getOptions().setCompressedMemoryCacheSize(8 * 1024 * 1024);
		}

		private void setMapBackground() {
			mapView.getOptions().setBackgroundPlaneDrawMode(Options.DRAW_BITMAP);
			mapView.getOptions().setBackgroundPlaneBitmap(
			        UnscaledBitmapLoader.decodeResource(getResources(),
			                R.drawable.background_plane));
			mapView.getOptions().setClearColor(Color.WHITE);
		}

		private void setSkyBitMap() {
			mapView.getOptions().setSkyDrawMode(Options.DRAW_BITMAP);
			mapView.getOptions().setSkyOffset(4.86f);
			mapView.getOptions().setSkyBitmap(
			        UnscaledBitmapLoader.decodeResource(getResources(),
			                R.drawable.sky_small));
		}
	    
	    @Override
	    protected void onStart() {
	        mapView.startMapping();
	        super.onStart();
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        mapView.stopMapping();
	    }


}
