(function () {
    console.log("JS Started in console");
    console.log("Map has " + map.layers.length + " layer(s)");
    var layer = map.getLayersByName("Simple Geometry")[0] || {notALayer: true};
    if (layer.notALayer == true) {
        console.log("Layer not found");
        return "not what you want"; // failure to find layer
    }
    console.log("Layer found");
    var features = layer.features.map(function process(feature) {
        var point = feature.geometry.clone();
        var pos = {
            x: point.x,
            y: point.y
        };
        return pos;
    });
    var jsonFeatures = JSON.stringify(features);
    console.log("Returning JSON features");
    return jsonFeatures;
})();
