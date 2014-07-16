// Note: this code needs to run *after* map init.

QUnit.test("Map init", function(assert) {
    assert.notEqual(map, undefined, "Map properly initialized");
    // the following test is a de-facto check for the existance of sampleLayer,
    // currentLoctationLayer, and positionHistoryLayer
    assert.equal(map.layers.length, 5, "Map layers loaded");
    assert.notEqual(map.getLayer(sampleLayer.id), null,
                    "Sample layer exists in map");
    assert.notEqual(map.getLayer(currentLocationLayer.id), null,
                    "Current location layer exists in map");
    assert.notEqual(map.getLayer(positionHistoryLayer.id), null,
                    "Position history layer exists in map");
});

QUnit.test("Current location", function(assert) {
    assert.equal(currentLocationLayer.features.length, 0,
                 "Current location layer started empty");
    // show current location
    setLayers("DisplayCurrentLocation");
    assert.equal(currentLocationLayer.features.length, 1,
                 "Showing current location added one feature to "+
                 "current location layer");

    // check current position (based on mock data)
    var geometry = currentLocationLayer.features[0].geometry;
    assert.equal(geometry.x, 111319.49077777777,
                 "Current location correct (x coordinate)");
    assert.equal(geometry.y, 111325.14285088828,
                 "Current location correct (y coordinate)");

    // hide the layer
    setLayers("HideCurrentLocation");
    assert.equal(currentLocationLayer.features.length, 0,
                 "Hiding current location removed location feature");
});

QUnit.test("Samples", function(assert) {
    assert.equal(sampleLayer.features.length, 0,
                 "Sample features layer started empty");
    // show some mock samples
    setLayers("DisplaySamples");
    assert.equal(sampleLayer.features.length, 3,
                 "Showing samples added three features to "+
                 "samples layer");

    // check samples and their location
    var geo1 = sampleLayer.features[0].geometry;
    var geo2 = sampleLayer.features[1].geometry;
    var geo3 = sampleLayer.features[2].geometry;
    assert.equal(geo1.x, 111319.49077777777,
                 "Sample 1 location correct (x coordinate)");
    assert.equal(geo1.y, 111325.14285088828,
                 "Sample 1 location correct (y coordinate)");
    assert.equal(geo2.x, 222638.98155555554,
                 "Sample 2 location correct (x coordinate)");
    assert.equal(geo2.y, 222684.20847454257,
                 "Sample 2 location correct (y coordinate)");
    assert.equal(geo3.x, 333958.47233333334,
                 "Sample 3 location correct (x coordinate)");
    assert.equal(geo3.y, 334111.17135544843,
                 "Sample 3 location correct (y coordinate)");

    // hide the layer
    setLayers("HideSamples");
    assert.equal(sampleLayer.features.length, 0,
                 "Hiding samples removed sample features");
});

QUnit.test("Position history", function(assert) {
    assert.equal(positionHistoryLayer.features.length, 0,
                 "Position history layer started empty");
    // show some mock position history
    setLayers("DisplayPositionHistory");
    assert.equal(positionHistoryLayer.features.length, 1,
                 "Showing position history added one feature to "+
                 "position history layer");
    assert.equal(positionHistoryLayer.features[0].geometry.components.length, 3,
                 "Position history feature had three components");

    // check waypoints and their location
    var geo1 = positionHistoryLayer.features[0].geometry.components[0];
    var geo2 = positionHistoryLayer.features[0].geometry.components[1];
    var geo3 = positionHistoryLayer.features[0].geometry.components[2];
    assert.equal(geo1.x, 111319.49077777777,
                 "Waypoint 1 location correct (x coordinate)");
    assert.equal(geo1.y, 111325.14285088828,
                 "Waypoint 1 location correct (y coordinate)");
    assert.equal(geo2.x, 222638.98155555554,
                 "Waypoint 2 location correct (x coordinate)");
    assert.equal(geo2.y, 222684.20847454257,
                 "Waypoint 2 location correct (y coordinate)");
    assert.equal(geo3.x, 333958.47233333334,
                 "Waypoint 3 location correct (x coordinate)");
    assert.equal(geo3.y, 334111.17135544843,
                 "Waypoint 3 location correct (y coordinate)");

    // hide the layer
    setLayers("HidePositionHistory");
    assert.equal(positionHistoryLayer.features.length, 0,
                 "Hiding position history removed position history features");
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
