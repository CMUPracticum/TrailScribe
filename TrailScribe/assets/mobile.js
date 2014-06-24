//
// Map and layers setup
//

// Map and map properties initialization
var map;
var mapBounds = new OpenLayers.Bounds(-122.134518893, 37.3680027864, -121.998720996, 37.4691074792);
var mapMinZoom = 11;
var mapMaxZoom = 15;
var emptyTileURL = "./lib/openlayers/img/none.png";
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;

// TMS Overlay init
var tmsOverlay;

// Vector Layer
var vectorLayer;

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

function init() {

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
                new OpenLayers.Control.Zoom()
            ],
            projection: "EPSG:900913", // web mercator
            displayProjection: new OpenLayers.Projection("EPSG:4326"), // spherical mercator
            numZoomLevels: 16
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
    map.addLayers([tmsOverlay]);

    // Add popup events to layer
    tmsOverlay.events.on({
        'featureselected': onFeatureSelect,
        'featureunselected': onFeatureUnselect
    });
    selectControl = new OpenLayers.Control.SelectFeature(tmsOverlay);

    // Zoom to extent
    map.zoomToExtent(mapBounds.transform(map.displayProjection, map.projection));
    map.zoomTo(13);    

    // Show mouse position-coordinate relation
    //map.addControls([new OpenLayers.Control.MousePosition()]);

    /////////////////////////////////////////////////////////////////////////////////////////
    // Geometry layer
    /////////////////////////////////////////////////////////////////////////////////////////

    // Allow testing of specific renderers via "?renderer=Canvas", etc
    var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
    renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

    // Layer style
    // We want opaque external graphics and non-opaque internal graphics
    var layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
    layer_style.fillOpacity = 0.4;
    layer_style.graphicOpacity = 1;
    layer_style.strokeWidth = 1.5;

    // Blue style
    var style_blue = OpenLayers.Util.extend({}, layer_style);
    style_blue.strokeColor = "blue";
    style_blue.fillColor = "blue";

    // Line style
    var style_line = OpenLayers.Util.extend({}, layer_style);
    style_line.strokeColor = "red";
    style_line.strokeWidth = 2;

    
    // Mark style
    var style_mark = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);    

    // if graphicWidth and graphicHeight are both set, the aspect ratio of the image will be ignored
    style_mark.graphicWidth = 21;
    style_mark.graphicHeight = 25;
    style_mark.graphicXOffset = -(style_mark.graphicWidth/2);
    style_mark.graphicYOffset = -style_mark.graphicHeight;
    style_mark.externalGraphic = "./lib/openlayers/img/marker.png";
    style_mark.fillOpacity = 1;
    style_mark.title = "this is a test tooltip"; // title only works in Firefox and Internet Explorer

    // Initialize vector layer
    vectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", {
                style: layer_style,
                renderers: renderer
            });

    // Create point features
    var point = new OpenLayers.Geometry.Point(-122.04451, 37.41800);    
    point = point.transform(map.displayProjection, map.projection);    
    var pointFeature = new OpenLayers.Feature.Vector(point, null, style_mark);
    var point2 = new OpenLayers.Geometry.Point(-122.07451, 37.41800);
    point2 = point2.transform(map.displayProjection, map.projection);
    var pointFeature2 = new OpenLayers.Feature.Vector(point2, null);
    var point3 = new OpenLayers.Geometry.Point(-122.10451, 37.39800);
    point3 = point3.transform(map.displayProjection, map.projection);
    var pointFeature3 = new OpenLayers.Feature.Vector(point3, null, style_blue);

    // Create a line feature from a list of points
    var tmpPoint = new OpenLayers.Geometry.Point(-122.05451, 37.40800);

    pointList = [];
    pointList.push(point);
    pointList.push(point2);
    pointList.push(point3);

    var lineFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(pointList), null, style_line);

    // Create a polygon feature from a linear ring of points
    var pointList = [];
    for(var p = 0; p < 6; ++p) {
        var a = p * (2 * Math.PI) / 7;
        var r = (Math.random(1) + 1) / 100;        
        var newPoint = new OpenLayers.Geometry.Point(tmpPoint.x + (r * Math.cos(a)),
                                                     tmpPoint.y + (r * Math.sin(a)));

        pointList.push(newPoint.transform(map.displayProjection, map.projection));
    }
    pointList.push(pointList[0]);
    var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
    var polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]));

    // Add vector layer to map
    map.addLayer(vectorLayer);
    // Add features to vector layer
    vectorLayer.addFeatures([pointFeature, pointFeature2, pointFeature3, lineFeature, polygonFeature]);

    // Add vector layer interaction
    // Register events    
    vectorLayer.events.register("featureselected", vectorLayer, onFeatureSelect);
    vectorLayer.events.register("featureunselected", vectorLayer, onFeatureUnselect);

    var control = new OpenLayers.Control.SelectFeature(vectorLayer);
    map.addControl(control);
    control.activate();
}

function getURL(bounds) {
    bounds = this.adjustBounds(bounds);
    var res = this.getServerResolution();
    var x = Math.round((bounds.left - this.tileOrigin.lon) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.tileOrigin.lat) / (res * this.tileSize.h));
    var z = this.getServerZoom();
    
    var path = this.serviceVersion + "/" + this.layername + "/" + z + "/" + x + "/" + y + "." + this.type; 
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
