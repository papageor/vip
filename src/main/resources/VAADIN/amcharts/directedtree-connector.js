ui_components_javascript_DirectedTree = function () {
    var connector = this;
    var element = connector.getElement();
    element.innerHTML = "<div id=\"tchartdiv\" style=\"width: 100%;height: 400px;\"></div>";

    connector.onStateChange = function () {
        var state = connector.getState();
        var data = state.data;

        am5.ready(function() {

        // Create root element
        // https://www.amcharts.com/docs/v5/getting-started/#Root_element
        var root = am5.Root.new("tchartdiv");


        // Set themes
        // https://www.amcharts.com/docs/v5/concepts/themes/
        root.setThemes([
          am5themes_Animated.new(root)
        ]);


        // Create wrapper container
        var container = root.container.children.push(am5.Container.new(root, {
          width: am5.percent(100),
          height: am5.percent(100),
          layout: root.verticalLayout
        }));


        // Create series
        // https://www.amcharts.com/docs/v5/charts/hierarchy/#Adding
        var series = container.children.push(am5hierarchy.ForceDirected.new(root, {
          singleBranchOnly: false,
          downDepth: 1,
          initialDepth: 2,
          valueField: "value",
          categoryField: "name",
          childDataField: "children",
          centerStrength: 0.5,
          minRadius: 30,
          maxRadius: am5.percent(15)
        }));

        series.labels.template.setAll({
          fontSize: 20,
          fill: am5.color(0x550000),
          text: "{name}"
        });


        // Generate and set data
        // https://www.amcharts.com/docs/v5/charts/hierarchy/#Setting_data
        var maxLevels = 2;
        var maxNodes = 5;
        var maxValue = 100;

        var data = {
          name: "Fleet",
          children: []
        }

        var childTanker = {
            name:"Tankers",
            value:300000,
            children:[{name:"Enterprise",value:120000},{name:"Beaver",value:160000},{name:"Trusty",value:120000}]
        }
        data.children.push(childTanker);


        var childBulker = {
                    name:"Bulk Curriers",
                    value:95000,
                    children:[{name:"Modena",value:45000},{name:"Brace",value:50000}]
                }
        data.children.push(childBulker);

        var childContainer = {
                            name:"Containers",
                            value:45000,
                            children:[{name:"Hoffen",value:45000}]
                        }
                data.children.push(childContainer);

        series.data.setAll([data]);
        series.set("selectedDataItem", series.dataItems[0]);

        series.nodes.template.events.on("hit", function(ev) {
          //const point = ev.target.dataItem.dataContext.name;
          alert('Hi!');
          //alert(point);
        });

        series.events.on("hit", function(ev) {
                  //const point = ev.target.dataItem.dataContext.name;
                  alert('Hi!');
                  //alert(point);
                });


        // Make stuff animate on load
        series.appear(1000, 100);

        }); // end am5.ready()

    }
};