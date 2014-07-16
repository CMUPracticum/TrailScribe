
// Styles
var layer_style;
var style_blue;
var style_line;
var style_fat_line;
var style_mark_blue;
var style_mark_green;
var style_mark_gold;

// Layer style
// We want opaque external graphics and non-opaque internal graphics
layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
layer_style.fillOpacity = 0.4;
layer_style.graphicOpacity = 1;
layer_style.strokeWidth = 1.5;

// Blue style
style_blue = OpenLayers.Util.extend({}, layer_style);
style_blue.strokeColor = "blue";
style_blue.fillColor = "blue";

// Line style
style_line = OpenLayers.Util.extend({}, layer_style);
style_line.strokeColor = "red";
style_line.strokeWidth = 2;

style_fat_line = OpenLayers.Util.extend({}, layer_style);
style_fat_line.strokeColor = "yellow";
style_fat_line.strokeWidth = 5;

// Mark style
style_mark_blue = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);    
style_mark_green = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
style_mark_gold = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']); 

// If graphicWidth and graphicHeight are both set, the aspect ratio of the image will be ignored
style_mark_blue.graphicWidth = 42;
style_mark_blue.graphicHeight = 50;
style_mark_blue.graphicXOffset = -(style_mark_blue.graphicWidth/2);
style_mark_blue.graphicYOffset = -style_mark_blue.graphicHeight;
style_mark_blue.externalGraphic = "./lib/openlayers/img/location_place.png";
style_mark_blue.fillOpacity = 1;
style_mark_blue.title = "this is a test tooltip"; // title only works in Firefox and Internet Explorer

style_mark_green.graphicWidth = 42;
style_mark_green.graphicHeight = 50;
style_mark_green.graphicXOffset = -(style_mark_blue.graphicWidth/2);
style_mark_green.graphicYOffset = -style_mark_blue.graphicHeight;
style_mark_green.externalGraphic = "./lib/openlayers/img/marker-green.png";

style_mark_green.graphicWidth = 42;
style_mark_green.graphicHeight = 50;
style_mark_green.graphicXOffset = -(style_mark_green.graphicWidth/2);
style_mark_green.graphicYOffset = -(style_mark_green.graphicHeight/2);
style_mark_green.externalGraphic = "./lib/openlayers/img/location_found.png";

style_mark_green.fillOpacity = 1;
style_mark_green.title = "this is a test tooltip";

style_mark_gold.graphicWidth = 42;
style_mark_gold.graphicHeight = 50;
style_mark_gold.graphicXOffset = -(style_mark_gold.graphicWidth/2);
style_mark_gold.graphicYOffset = -style_mark_gold.graphicHeight;
style_mark_gold.externalGraphic = "./lib/openlayers/img/marker-gold.png";
style_mark_gold.fillOpacity = 1;
style_mark_gold.title = "this is a test tooltip"; // TODO: change this
