ui_components_javascript_Windy = function () {
    var connector = this;
    var element = connector.getElement();
    element.innerHTML = "<div id=\"windy\" style=\"width: 100%;height: 550px;\"></div>";

    connector.onStateChange = function () {
        var state = connector.getState();
        var data = state.data;

        const options = {
            // Required: API key
            key: 'oYu3MpTbG3ZiImPuNIgRoQ3ddpCfKqCD',

            // Put additional console output
            verbose: true,

            // Optional: Initial state of the map
            lat: 50.4,
            lon: 14.3,
            zoom: 5,
        };

        windyInit(options, windyAPI => {
            const { picker, utils, broadcast, store } = windyAPI;

            picker.on('pickerOpened', ({ lat, lon, values, overlay }) => {
                // -> 48.4, 14.3, [ U,V, ], 'wind'
                console.log('opened', lat, lon, values, overlay);

                const windObject = utils.wind2obj(values);
                console.log(windObject);
            });

            picker.on('pickerMoved', ({ lat, lon, values, overlay }) => {
                // picker was dragged by user to latLon coords
                console.log('moved', lat, lon, values, overlay);
            });

            picker.on('pickerClosed', () => {
                // picker was closed
            });

            store.on('pickerLocation', ({ lat, lon }) => {
                console.log(lat, lon);

                const { values, overlay } = picker.getParams();
                console.log('location changed', lat, lon, values, overlay);
            });

            // Wait since wather is rendered
            broadcast.once('redrawFinished', () => {
                // Opening of a picker (async)
                picker.open({ lat: 48.4, lon: 14.3 });
            });
        });

        }
};