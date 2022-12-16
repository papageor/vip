ui_components_javascript_barstackedhc = function () {
    var connector = this;
    var element = connector.getElement();
    var elementId = Date.now().toString(36) + Math.random().toString(36).substr(2);

    element.innerHTML = "<div id=\"" + elementId + "\"></div class=\"highcharts-description\">";

    connector.onStateChange = function () {
        var state = connector.getState();
        var jmix_data = state.data;

         Highcharts.chart(elementId, {
                              chart: {
                                  type: 'bar'
                              },
                              title: {
                                  text: jmix_data.title
                              },
                              xAxis: {
                                  categories: JSON.parse(jmix_data.categories)
                              },
                              yAxis: {
                                  min: 0,
                                  title: {
                                      text: jmix_data.xAxisTitle
                                  }
                              },
                              legend: {
                                  reversed: true
                              },
                              plotOptions: {
                                  series: {
                                      stacking: 'normal'
                                  }
                              },
                              series: [{
                                  name: jmix_data.dataSeriesTitle,
                                  data: JSON.parse(jmix_data.dataItems)
                              },
                              {
                                  name: jmix_data.dataSeriesTitle2,
                                  data: JSON.parse(jmix_data.dataItems2)

                              }]
                          });

    }
};