/**
 * OpenLayers.Style settings for all map elements.
 */

/**
 * Layer Styles
 */
var layer_style;

// We want opaque external graphics and non-opaque internal graphics
layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
layer_style.fillOpacity = 0.4;
layer_style.graphicOpacity = 1;
layer_style.strokeWidth = 1.5;

/** 
 * Vector Point and Marker Styles
 */
var style_blue;
var style_line;
var style_line_thick;
var marker_default;
var marker_blue;
var marker_red;
var marker_green;
var marker_gold;
var marker_colors = ["blue", "red", "green", "gold"];

var style_current_location;

var defaultStyle = {
  'pointRadius': 10,
  'graphicWidth': 46,
  'graphicHeight': 64,
  'graphicXOffset': -23, // -(graphicWidth/2)
  'graphicYOffset': -64, // -graphicHeight
  'fillOpacity': 1,
  'externalGraphic': './lib/openlayers/img/marker-default.png'
};

// Blue vector point style
style_blue = OpenLayers.Util.extend({}, layer_style);
style_blue.strokeColor = "blue";
style_blue.fillColor = "blue";

// Line styles
style_line = OpenLayers.Util.extend({}, layer_style);
style_line.strokeColor = "red";
style_line.strokeWidth = 2;

style_line_thick = OpenLayers.Util.extend({}, layer_style);
style_line_thick.strokeColor = "gold";
style_line_thick.strokeWidth = 4;

// Marker default style
marker_default = defaultStyle;

// Set other marker styles
for (var i = 0; i < marker_colors.length; i++) {    
    switch (marker_colors[i]) {
        case "blue":
            marker_blue = OpenLayers.Util.extend({}, defaultStyle);
            marker_blue['externalGraphic'] = "./lib/openlayers/img/marker-default-blue.png";
            break;
        case "red":
            marker_red = OpenLayers.Util.extend({}, defaultStyle);
            marker_red['externalGraphic'] = "./lib/openlayers/img/marker-default-red.png";
            break;
        case "green":
            marker_green = OpenLayers.Util.extend({}, defaultStyle);
            marker_green['externalGraphic'] = "./lib/openlayers/img/marker-default-green.png";
            break;
        case "gold":
            marker_gold = OpenLayers.Util.extend({}, defaultStyle)
            marker_gold['externalGraphic'] = "./lib/openlayers/img/marker-default-gold.png";
            break;
    }
}

// Current location style
style_current_location = OpenLayers.Util.extend({}, defaultStyle);
style_current_location['externalGraphic'] = "./lib/openlayers/img/location_arrow.png"; // Image is 106px x 84px
style_current_location['graphicWidth'] = 42; 
style_current_location['graphicHeight'] = 53;
style_current_location['graphicXOffset'] = -21;
style_current_location['graphicYOffset'] = -26;
