/**
 * Android OpenLayers interface for project TrailScribe.
 * All offline mapping functionality on the Android device is realized through this JavaScript.
 * It communicates to native Java via a simple interface. 
 */

/**
 * @requires TrailScribe/assets/lib/openlayers/OpenLayers.mobile.js
 * @requires TrailScribe/assets/styles.js
 */

/**
 * Map and OpenLayers Properties
 */
var map;
var mapBounds;
var extent;
var mapMinZoom;
var mapMaxZoom;
var mapProjection;
var displayProjection;
var emptyTileURL = "./lib/openlayers/img/none.png";
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;

// Get rid of address bar on iphone/ipod
var fixSize = function() {
    window.scrollTo(0,0);
    document.body.style.height = '100%';
    if (!(/(iphone|ipod)/.test(navigator.userAgent.toLowerCase()))) {
        if (document.body.parentNode) {
            document.body.parentNode.style.height = '100%';
        }
    }
};
setTimeout(fixSize, 700);
setTimeout(fixSize, 1500);


/**
 * Base Map Layer
 */
var tmsOverlay;

/**
 * Vector and KML Layers
 */
var sampleLayer;
var currentLocationLayer;
var positionHistoryLayer;
var kmlLayer;

/**
 * Map Events
 */
var renderer;
var selectControl;
var layerListeners;

/**
 * Function: initMapProperties
 * Get mapProperties for this map from the Android interface and set them.
 *
 * Parameters:
 * initMapProperties - {JSON String}
 */
function initMapProperties(mapProperties) {    
    mapProjection = new OpenLayers.Projection("EPSG:900913"); // Default: Web Mercator
    displayProjection = new OpenLayers.Projection("EPSG:4326")
    mapBounds = new OpenLayers.Bounds(-122.134518893, 37.3680027864, -121.998720996, 37.4691074792);
    extent = mapBounds.transform(displayProjection, mapProjection);
    mapMinZoom = 11;
    mapMaxZoom = 15;
}

/**
 * Function: init
 * Entry point to the file where all important map and layer properties
 * are created and set.
 *
 * Parameters:
 * -
 */
function init() {

    // Initialize map properties
    initMapProperties();

    // Set Map options    
    var options = {
            div: "map",
            theme: null,
            controls: [
                new OpenLayers.Control.Attribution(),
                new OpenLayers.Control.TouchNavigation({
                    dragPanOptions: {
                        enableKinetic: true
                    }
                }),                
            ],
            projection: mapProjection,
            displayProjection: displayProjection, // Spherical Mercator
            tileSize: new OpenLayers.Size(256, 256)
        };
    
    // Create map
    map = new OpenLayers.Map(options);

    // Create TMS Overlay (Base map)
    tmsOverlay = new OpenLayers.Layer.TMS("TMS Overlay", "", {
        serviceVersion: '.',
        layername: 'tiles',        
        alpha: true,
        type: 'png',
        isBaseLayer: true, 
        getURL: getURL
    });

    // Add TMS overlay
    map.addLayer(tmsOverlay);

    // Listen to zoom levels for preventing the user going beyond the min zoom level
    map.events.register("zoomend", map, function() {
         checkMinZoomLevel();
    });

    // Add popup events to base layer
    layerListeners = {
        'featureselected': onFeatureSelect,
        'featureunselected': onFeatureUnselect
    };

    // Allow testing of specific renderers via "?renderer=Canvas", etc
    renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
    renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

    // Layer for displaying samples
    sampleLayer = new OpenLayers.Layer.Vector("Samples", {
                style: layer_style,
                renderers: renderer                
            });

    // Layer for displaying the current location of the user
    currentLocationLayer = new OpenLayers.Layer.Vector("CurrentLocation", {
                style: layer_style,
                renderers: renderer                
            });

    // Layer for displaying the position history of the user
    positionHistoryLayer = new OpenLayers.Layer.Vector("PositionHistory", {
                style: layer_style,
                renderers: renderer                
            });

    // Register layers for event listeners
    sampleLayer.events.on(layerListeners);

    // Add layers to map
    map.addLayers([sampleLayer, currentLocationLayer, positionHistoryLayer]);

    // Add this control to all vector layers on the map
    selectControl = new OpenLayers.Control.SelectFeature(
                [sampleLayer, currentLocationLayer, positionHistoryLayer]
            );
    map.addControl(selectControl);
    selectControl.activate();

    // Zoom to extent
    map.zoomToExtent(extent);
    map.setOptions({restrictedExtent: extent});    
}

/**
 * Function: getURL
 * This function gets the correct tiles (for the TMS Overlay) to display on the map
 * from the device.
 *
 * Parameters:
 * bounds - {OpenLayers.Bounds}
 */
function getURL(bounds) {
    bounds = this.adjustBounds(bounds);
    var res = this.getServerResolution();
    var x = Math.round((bounds.left - this.tileOrigin.lon) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.tileOrigin.lat) / (res * this.tileSize.h));
    var z = this.getServerZoom();
        
    var path = "file:///sdcard/trailscribe" + "/" + this.layername + "/" + z + "/" + x + "/" + y + "." + this.type;
    var url = this.url;
    
    if (OpenLayers.Util.isArray(url)) {
        url = this.selectUrl(path, url);
    }
    if (mapBounds.intersectsBounds(bounds) && (z >= mapMinZoom) && (z <= mapMaxZoom)) {        
        return url + path;
    } else {
        return emptyTileURL;
    }
}

/**
 * Function: checkMinZoomLevel
 * If the user tries to zoom further back then the min zoom level, 
 * zoom back to mapMinZoom
 *
 * Parameters:
 * -
 */
function checkMinZoomLevel() {
    if (map.zoom < mapMinZoom) {
        map.zoomTo(mapMinZoom);
    }
}

/**
 * Function: onPopupClose
 * Mark this vector feature as unselected
 *
 * Parameters:
 * evt - {OpenLayers.Event}
 */
function onPopupClose(evt) {
    // 'this' is the popup.
    selectControl.unselect(this.feature);
}

/**
 * Function: onFeatureSelect
 * Select a feature that was clicked and show a popup on map.
 *
 * Parameters:
 * evt - {OpenLayers.Event}
 */
function onFeatureSelect(evt) {
    feature = evt.feature;

    popup = new OpenLayers.Popup.FramedCloud("pop",
          feature.geometry.getBounds().getCenterLonLat(),
          null,
          '<div class="markerContent">Example popup.</div>',
          null,
          true,
          onPopupClose);

    feature.popup = popup;
    popup.feature = feature;
    map.addPopup(popup);
}

/**
 * Function: onFeatureUnselect
 * Unselect a feature that was selected and remove popup from map.
 *
 * Parameters:
 * evt - {OpenLayers.Event}
 */
function onFeatureUnselect(evt) {
    feature = evt.feature;
    if (feature.popup) {
        popup.feature = null;
        map.removePopup(feature.popup);
        feature.popup.destroy();
        feature.popup = null;
    }
}

/**
 * Function: getKmlUrl
 * Given a kml file name, find the location on device for the kml file.
 * 
 * Return url example:
 * file:///sdcard/trailscribe/kml/sample_kml.kml
 *
 * Parameters:
 * kml - {String}
 */
function getKmlUrl(kml) {    
    return "file:///sdcard/trailscribe" + "/kml/" + kml + "." + "kml";
}

/**
 * Functions to Access Android Interface
 */

/**
 * Function: setLayers
 * When the user toggles one of the menu items, 
 * a message is passed to this method, which in turn calls 
 * the appropriate Android/Java function to get the correct set of vector geometry.
 *
 * Parameters:
 * msg - {String}
 */
function setLayers(msg) {
    switch (msg) {
        case "DisplaySamples":            
            sampleLayer.addFeatures(getPointsFromJava(msg));
            break;
        case "HideSamples":            
            hideLayer(sampleLayer);
            break;
        case "DisplayCurrentLocation":            
            currentLocationLayer.addFeatures(getPointsFromJava(msg));            
            break;
        case "HideCurrentLocation":            
            hideLayer(currentLocationLayer);            
            break;            
		case "DisplayPositionHistory":			
            positionHistoryLayer.addFeatures(getLinesFromJava(msg));            
			break;			
		case "HidePositionHistory":			
            hideLayer(positionHistoryLayer);
			break;
        case "DisplayKML":            
            displayKML("test_layer"); // TO DO: This is hardcoded. 
            break;
        case "HideKML":
            map.removeLayer(kmlLayer);            
            break;
        default:
            break;
    }
}

/**
 * Function: hideLayer
 * Given a layer, remove all popups if there are any
 * features with open popups on this layer. Finally, 
 * remove all vector features from this layer.
 *
 * Parameters:
 * layer - {OpenLayers.Layer.Vector}
 */
function hideLayer(layer) {
    // If a feature on this layer has an open popup, close that first
    for (var i = 0; i < layer.features.length; i++) {
        if (layer.features[i].popup) {
            popup.feature = null;
            map.removePopup(layer.features[i].popup);
            layer.features[i].popup.destroy();
            layer.features[i].popup = null;
        }
    }
    // Remove all features from this layer
    layer.removeAllFeatures();
}

/**
 * Function: displayKML
 * Given a kml file name, display the KML overlay with that file.
 *
 * Parameters:
 * kml - {String}
 */
function displayKML(kml) {
    kmlLayer = new OpenLayers.Layer.Vector("KML", new OpenLayers.Layer.Vector("KML", {
            projection: map.displayProjection,
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: getKmlUrl(kml),                    
                format: new OpenLayers.Format.KML({
                    extractStyles: true, 
                    extractAttributes: true,
                    maxDepth: 2
                })
            }),
            eventListeners: layerListeners
        }));

    // Add KML Overlay
    map.addLayer(kmlLayer);
}

/**
 * Function: getPointsFromJava
 * Given a message, summon the correct Android/Java method 
 * to get a list of vector points. 
 *
 * Parameters:
 * msg - {String}
 */
function getPointsFromJava(msg) {
    var points;
    var marker_style;
    var azimuth = -1;

    switch (msg) {
        case "DisplaySamples":
            points = android.getSamples();
            marker_style = marker_red;
            break;
        case "DisplayCurrentLocation":
            points = android.getCurrentLocation();
            marker_style = style_current_location;
            
            var orientation = android.getOrientation();
            orientation = JSON.parse(orientation);

            for (data in orientation['orientation']) {
                azimuth = orientation['orientation'][data].azimuth;
            }

            break;
        default:
            return;
    }

    points = JSON.parse(points);
    var pointList = [];
    var pointFeatures = [];
    for(data in points['points']){
	    var point = new OpenLayers.Geometry.Point(points['points'][data].x, points['points'][data].y);		
        point = point.transform(map.displayProjection, map.projection);
        var pointFeature = new OpenLayers.Feature.Vector(point, null, marker_style);

        if (msg == "DisplayCurrentLocation") {
            pointFeature.style.rotation = azimuth;
        }

        pointFeatures.push(pointFeature);
        pointList.push(point);
    }

    return pointFeatures;
}

/**
 * Function: getLinesFromJava
 * Given a message, summon the correct Android/Java method
 * to get a list of vector lines. 
 *
 * Parameters:
 * msg - {String}
 */
function getLinesFromJava(msg) {
    var points;
    var line_style;

    switch (msg) {
		case "DisplayPositionHistory":
			points = android.getPositionHistory();
			line_style = style_line_thick;
			break;        
        default:
            return;
    }
    
    points = JSON.parse(points);
    var pointList = [];
    var pointFeatures = [];
    for(data in points['points']){
	    var point = new OpenLayers.Geometry.Point(points['points'][data].x, points['points'][data].y);		
        point = point.transform(map.displayProjection, map.projection);    
        var pointFeature = new OpenLayers.Feature.Vector(point, null, line_style);
        pointFeatures.push(pointFeature);
        pointList.push(point);
    }
    var lineFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(pointList), 
    null, line_style);
    pointFeatures.push(lineFeature);

    return lineFeature;
}
