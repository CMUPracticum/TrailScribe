//
// Map and layers setup
//
// Map and map properties initialization
var map;
var mapBounds;
var mapMinZoom;
var mapMaxZoom;
var mapProjection;
var emptyTileURL = "./lib/openlayers/img/none.png";
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;

// Base map
var tmsOverlay;

// Map Layers
var sampleLayer;
var currentLocationLayer;
var positionHistoryLayer;
var kmlLayer;

// Renderer
var renderer;
//
// /End of map and layers setup
//

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

// TO DO: Get these properties from Android interface
function initMapProperties() {

    mapBounds = new OpenLayers.Bounds(-122.134518893, 37.3680027864, -121.998720996, 37.4691074792);
    mapMinZoom = 11;
    mapMaxZoom = 15;
    mapProjection = "EPSG:900913"; // Default: Web Mercator
}

function init() {

    initMapProperties();
        
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
            displayProjection: new OpenLayers.Projection("EPSG:4326"), // Spherical Mercator
            tileSize: new OpenLayers.Size(256, 256)
        };
    
    // Create map
    map = new OpenLayers.Map(options);

    // Create TMS Overlay 
    tmsOverlay = new OpenLayers.Layer.TMS("TMS Overlay", "",
    {
        serviceVersion: '.',
        layername: 'tiles',        
        alpha: true,
        type: 'png',
        isBaseLayer: true, 
        getURL: getURL
    });

    // Add TMS overlay
    map.addLayer(tmsOverlay);

    // Add popup events to layer
    tmsOverlay.events.on({
        'featureselected': onFeatureSelect,
        'featureunselected': onFeatureUnselect
    });
    selectControl = new OpenLayers.Control.SelectFeature(tmsOverlay);

    // Zoom to extent
    map.zoomToExtent(mapBounds.transform(map.displayProjection, map.projection));
    map.zoomTo(14);

    // Allow testing of specific renderers via "?renderer=Canvas", etc
    renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
    renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;
}

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

function onPopupClose(evt) {
    // 'this' is the popup.
    selectControl.unselect(this.feature);
}

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

function onFeatureUnselect(evt) {
    feature = evt.feature;
    if (feature.popup) {
        popup.feature = null;
        map.removePopup(feature.popup);
        feature.popup.destroy();
        feature.popup = null;
    }
}

function getKmlUrl(kmlFile) {    
    return "file:///sdcard/trailscribe" + "/kml/" + kmlFile + "." + "kml";
}

// ----------------------------------------
// Functions to access Android Interface
// ----------------------------------------
function setLayers(msg) {
    switch (msg) {
        case "DisplaySamples":
            sampleLayer = new OpenLayers.Layer.Vector("Samples", {
                style: layer_style,
                renderers: renderer
            });

            var pointFeatures = getPointsFromJava(msg);
            displayPoints(pointFeatures, sampleLayer);
            break;

        case "HideSamples":
            hideLayer(sampleLayer);
            break;

        case "DisplayCurrentLocation":
            currentLocationLayer = new OpenLayers.Layer.Vector("CurrentLocation", {
                style: layer_style,
                renderers: renderer
            });

            var pointFeatures = getPointsFromJava(msg);
            displayPoints(pointFeatures, currentLocationLayer);
            break;

        case "HideCurrentLocation":
            hideLayer(currentLocationLayer);
            break;
            
		case "DisplayPositionHistory":
			positionHistoryLayer = new OpenLayers.Layer.Vector("PositionHistory", {
				style: layer_style,
				renderers: renderer
			});
			
			var lineFeature = getLinesFromJava(msg);
			displayPoints(lineFeature, positionHistoryLayer);
			break;
			
		case "HidePositionHistory":
			hideLayer(positionHistoryLayer);
			break;

        case "DisplayKML":            
            //var kml = getKMLFromJava(msg);            
            var kml = "test_layer"; // TO DO: This is hardcoded. 
            var kmlFile = kml;
            displayKML(kmlFile);
            break;

        case "HideKML":
            hideLayer(kmlLayer);
            break;

        default:
            break;
    }
}

function hideLayer(layer) {
    map.removeLayer(layer);
}

function displayPoints(pointFeatures, layer) {
    map.addLayer(layer);
    layer.addFeatures(pointFeatures);

    layer.events.register("featureselected", layer, onFeatureSelect);
    layer.events.register("featureunselected", layer, onFeatureUnselect);

    var control = new OpenLayers.Control.SelectFeature(layer);
    map.addControl(control);
    control.activate();
}

function displayKML(kmlFile) {
    kmlLayer = new OpenLayers.Layer.Vector("KML", new OpenLayers.Layer.Vector("KML", {
            projection: map.displayProjection,
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: getKmlUrl(kmlFile),                    
                format: new OpenLayers.Format.KML({
                    extractStyles: true, 
                    extractAttributes: true,
                    maxDepth: 2
                })
            })
        }));

    // Add KML Overlay
    map.addLayer(kmlLayer);
}

function getPointsFromJava(msg) {
    var points;
    var marker_style;

    switch (msg) {
        case "DisplaySamples":
            points = android.getSamples();
            marker_style = marker_red;
            break;
        case "DisplayCurrentLocation":
            points = android.getCurrentLocation();
            marker_style = style_current_location;;
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
        pointFeatures.push(pointFeature);
        pointList.push(point);
    }

    return pointFeatures;
}

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
