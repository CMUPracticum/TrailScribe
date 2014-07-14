// Note: this code needs to run *after* map init.

QUnit.test("Map init", function(assert) {
    assert.notEqual(map, undefined, "Map properly initialized");
});

QUnit.test("Init layers", function(assert) {
    assert.equal(map.layers.length, 1, "Base layer loaded");
});

QUnit.test("Current location", function(assert) {
    // show current location
    setLayers("DisplayCurrentLocation");
    assert.equal(map.layers.length, 2, "Displaying current location added another layer");
    assert.equal(map.layers[1].features.length, 1, "Current location layer had one feature");

    // check current position (based on mock data)
    var geometry = map.layers[1].features[0].geometry;
    assert.equal(geometry.x, 111319.49077777777, "Current location correct (x coordinate)");
    assert.equal(geometry.y, 111325.14285088828, "Current location correct (y coordinate)");

    // hide the layer
    setLayers("HideCurrentLocation");
    assert.equal(map.layers.length, 1, "Hiding current location removed a layer");
});

QUnit.test("Samples", function(assert) {
    // show some mock samples
    setLayers("DisplaySamples");
    assert.equal(map.layers.length, 2, "Displaying samples added another layer");
    assert.equal(map.layers[1].features.length, 3, "Sample layer had three features");

    // check samples and their location
    var geo1 = map.layers[1].features[0].geometry;
    var geo2 = map.layers[1].features[1].geometry;
    var geo3 = map.layers[1].features[2].geometry;
    assert.equal(geo1.x, 111319.49077777777, "Sample 1 location correct (x coordinate)");
    assert.equal(geo1.y, 111325.14285088828, "Sample 1 location correct (y coordinate)");
    assert.equal(geo2.x, 222638.98155555554, "Sample 2 location correct (x coordinate)");
    assert.equal(geo2.y, 222684.20847454257, "Sample 2 location correct (y coordinate)");
    assert.equal(geo3.x, 333958.47233333334, "Sample 3 location correct (x coordinate)");
    assert.equal(geo3.y, 334111.17135544843, "Sample 3 location correct (y coordinate)");

    // hide the layer
    setLayers("HideSamples");
    assert.equal(map.layers.length, 1, "Hiding samples removed a layer");
});

QUnit.test("Position history", function(assert) {
    // show some mock position history
    setLayers("DisplayPositionHistory");
    assert.equal(map.layers.length, 2, "Displaying position history added another layer");
    assert.equal(map.layers[1].features.length, 1, "Position history layer had one feature");
    assert.equal(map.layers[1].features[0].geometry.components.length, 3, "Position history feature had three components");

    // check waypoints and their location
    var geo1 = map.layers[1].features[0].geometry.components[0];
    var geo2 = map.layers[1].features[0].geometry.components[1];
    var geo3 = map.layers[1].features[0].geometry.components[2];
    assert.equal(geo1.x, 111319.49077777777, "Waypoint 1 location correct (x coordinate)");
    assert.equal(geo1.y, 111325.14285088828, "Waypoint 1 location correct (y coordinate)");
    assert.equal(geo2.x, 222638.98155555554, "Waypoint 2 location correct (x coordinate)");
    assert.equal(geo2.y, 222684.20847454257, "Waypoint 2 location correct (y coordinate)");
    assert.equal(geo3.x, 333958.47233333334, "Waypoint 3 location correct (x coordinate)");
    assert.equal(geo3.y, 334111.17135544843, "Waypoint 3 location correct (y coordinate)");

    // hide the layer
    setLayers("HidePositionHistory");
    assert.equal(map.layers.length, 1, "Hiding position history removed a layer");
});

QUnit.test("Feature popup", function(assert) {
    // show mock samples
    setLayers("DisplaySamples");

    // get object handles
    var layer = map.layers[1];
    var feature = layer.features[0];

    // trigger featureselected event
    layer.events.triggerEvent("featureselected", {
        feature: feature
    });

    // there should now be a popup
    assert.equal(map.popups.length, 1, "Selecting a feature displayed a popup");

    // unselect feature
    layer.events.triggerEvent("featureunselected", {
        feature: feature
    });

    // there should now not be a popup
    assert.equal(map.popups.length, 0, "Unselected the feature hid the popup");
});
