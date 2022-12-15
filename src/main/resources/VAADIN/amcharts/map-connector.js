ui_components_javascript_Map = function () {
    var connector = this;
    var element = connector.getElement();
    element.innerHTML = "<div id=\"chartdiv\" style=\"width: 100%;height: 400px;\"></div>";

    connector.onStateChange = function () {
        var state = connector.getState();
        var data = state.data;

        am5.ready(function() {
            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element
            var root = am5.Root.new("chartdiv");

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
              am5themes_Animated.new(root)
            ]);


            // Create the map chart
            // https://www.amcharts.com/docs/v5/charts/map-chart/
            var chart = root.container.children.push(am5map.MapChart.new(root, {
              panX: "rotateX",
              panY: "rotateY",
              projection: am5map.geoOrthographic(),
              paddingBottom: 20,
              paddingTop: 20,
              paddingLeft: 20,
              paddingRight: 20
            }));


            // Create main polygon series for countries
            // https://www.amcharts.com/docs/v5/charts/map-chart/map-polygon-series/
            var polygonSeries = chart.series.push(am5map.MapPolygonSeries.new(root, {
              geoJSON: am5geodata_worldLow
            }));

            polygonSeries.mapPolygons.template.setAll({
              tooltipText: "{name}",
              toggleKey: "active",
              interactive: true
            });

            polygonSeries.mapPolygons.template.states.create("hover", {
              fill: root.interfaceColors.get("primaryButtonHover")
            });


            // Create series for background fill
            // https://www.amcharts.com/docs/v5/charts/map-chart/map-polygon-series/#Background_polygon
            var backgroundSeries = chart.series.push(am5map.MapPolygonSeries.new(root, {}));
            backgroundSeries.mapPolygons.template.setAll({
              fill: root.interfaceColors.get("alternativeBackground"),
              fillOpacity: 0.1,
              strokeOpacity: 0
            });
            backgroundSeries.data.push({
              geometry: am5map.getGeoRectangle(90, 180, -90, -180)
            });


            // Create graticule series
            // https://www.amcharts.com/docs/v5/charts/map-chart/graticule-series/
            var graticuleSeries = chart.series.push(am5map.GraticuleSeries.new(root, {}));
            graticuleSeries.mapLines.template.setAll({ strokeOpacity: 0.1, stroke: root.interfaceColors.get("alternativeBackground") })

            // GeoJSON data
            var cities = {
              "type": "FeatureCollection",
              "features": [{
                "type": "Feature",
                "properties": {
                  "name": "New York City"
                },
                "geometry": {
                  "type": "Point",
                  "coordinates": [-73.778137, 40.641312]
                }
              }, {
                "type": "Feature",
                "properties": {
                  "name": "London"
                },
                "geometry": {
                  "type": "Point",
                  "coordinates": [-0.454296, 51.470020]
                }
              }, {
                "type": "Feature",
                "properties": {
                  "name": "Beijing "
                },
                "geometry": {
                  "type": "Point",
                  "coordinates": [116.597504, 40.072498]
                }
              }]
            };

            // Create point series

            var pointSeries = chart.series.push(
              am5map.MapPointSeries.new(root, {
                geoJSON: JSON.parse(data.dataItems)
              })
            );

            /*
            for (var i = 0; i < cities.length; i++) {
                  var city = cities[i];
                  addCity(city.longitude, city.latitude, city.title);
                }

                function addCity(longitude, latitude, title) {
                  pointSeries.data.push({
                    geometry: { type: "Point", coordinates: [longitude, latitude] },
                    title: title
                  });
                }
            */
            pointSeries.events.on("click",function(ev){

                //alert(ev.target.dataItem.dataContext.name);

            });

            pointSeries.bullets.push(function() {
              const circle = am5.Circle.new(root, {
                                               radius: 5,
                                               tooltipText: "{name}",
                                               fill: am5.color(0xffba00)
                                             });

              circle.events.on("click", function(ev) {
                  //alert( ev.target.dataItem.dataContext.name);

                  const point = ev.target.dataItem.dataContext.name;

                  connector.bulletClicked(point);
                  //alert(ev.target.dataItem.properties.name);
                  //connector.bulletClicked(ev.target);

                    // Original data object
                    //alert(ev.target.dataItem.dataContext);

                    // Series
                    //alert(ev.target.dataItem.component)
                });

              return am5.Bullet.new(root, {
                sprite: circle
              });
            });


            // Rotate animation
            chart.animate({
              key: "rotationX",
              from: 0,
              to: 360,
              duration: 30000,
              loops: Infinity
            });


            // Make stuff animate on load
            chart.appear(1000, 100);

        }); // end am5.ready()

    }
};