var samples = [
    {
        id: "0",
        userId: "0",
        mapId: "0",
        expeditionId: "0",
        x: "1.0",
        y: "1.0",
        z: "0.0",
        name: "Test Sample 1",
        description: "Test Sample 1",
        time: "0",
        customField: "",
        lastModified: "0"
    },
    {
        id: "1",
        userId: "0",
        mapId: "0",
        expeditionId: "0",
        x: "2.0",
        y: "2.0",
        z: "0.0",
        name: "Test Sample 2",
        description: "Test Sample 2",
        time: "1",
        customField: "",
        lastModified: "1"
    },
    {
        id: "2",
        userId: "0",
        mapId: "0",
        expeditionId: "0",
        x: "3.0",
        y: "3.0",
        z: "0.0",
        name: "Test Sample 2",
        description: "Test Sample 2",
        time: "2",
        customField: "",
        lastModified: "2"
    }
];
window.android = {
    getSamples: function() {
        var obj = {
            points: samples
        };
        return JSON.stringify(obj);
    },

    getSample: function(id) {
        var obj = {
            points: [
                samples[id]
            ]
        };
        return JSON.stringify(obj);
    },

    getCurrentLocation: function() {
        var obj = {
            points: [
                {
                    x: "1.0",
                    y: "1.0",
                }

            ]
        };
        return JSON.stringify(obj);
    },

    getPositionHistory: function() {
        var obj = {
            points: [
                {
                    id: "0",
                    time: "0",
                    x: "1.0",
                    y: "1.0",
                    z: "0.0",
                    userId: "0",
                    mapId: "0",
                    expeditionId: "0"
                },
                {
                    id: "1",
                    tim: "1",
                    x: "2.0",
                    y: "2.0",
                    z: "0.0",
                    userId: "0",
                    mapId: "0",
                    expeditionId: "0"
                },
                {
                    id: "2",
                    time: "2",
                    x: "3.0",
                    y: "3.0",
                    z: "0.0",
                    userId: "0",
                    mapId: "0",
                    expeditionId: "0"
                }
            ]
        };
        return JSON.stringify(obj);
    },

    getOrientation: function() {
        var obj = {
            orientation: [
                {
                    azimuth: '1.0'
                }
            ]
        };
        return JSON.stringify(obj);
    },

    getCurrentMap: function() {
        var obj = {
            map: {
                projection: "CRS:84",
                name: "Test Map",
                minZoomLevel: "3",
                maxZoomLevel: "8",
                minX: "-180",
                minY: "-90",
                maxX: "180",
                maxY: "90"
            }
        };
        return JSON.stringify(obj);
    }
};
