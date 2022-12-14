ui_components_javascript_linechartjs = function () {
    var connector = this;
    var element = connector.getElement();
    var elementId = Date.now().toString(36) + Math.random().toString(36).substr(2);

    element.innerHTML = "<div style=\"max-width: 800px;margin: 0 auto;\"><canvas id=\"" + elementId + "\"></canvas></div>";

    connector.onStateChange = function () {
        var state = connector.getState();
        var jmix_data = state.data;

        //Data
        const data = [];
        const data2 = [];
        let prev = 100;
        let prev2 = 80;
        for (let i = 0; i < 100; i++) {
          prev += 5 - Math.random() * 10;

          var xDate = new Date();
          xDate.setDate(xDate.getDate() + i);

          data.push({x: xDate, y: prev});
          prev2 += 5 - Math.random() * 10;
          data2.push({x: xDate, y: prev2});
        }

        //Amimation
        const totalDuration = 10000;
        const delayBetweenPoints = totalDuration / data.length;
        const previousY = (ctx) => ctx.index === 0 ? ctx.chart.scales.y.getPixelForValue(100) : ctx.chart.getDatasetMeta(ctx.datasetIndex).data[ctx.index - 1].getProps(['y'], true).y;
        const animation = {
          x: {
            type: 'number',
            easing: 'linear',
            duration: delayBetweenPoints,
            from: NaN, // the point is initially skipped
            delay(ctx) {
              if (ctx.type !== 'data' || ctx.xStarted) {
                return 0;
              }
              ctx.xStarted = true;
              return ctx.index * delayBetweenPoints;
            }
          },
          y: {
            type: 'number',
            easing: 'linear',
            duration: delayBetweenPoints,
            from: previousY,
            delay(ctx) {
              if (ctx.type !== 'data' || ctx.yStarted) {
                return 0;
              }
              ctx.yStarted = true;
              return ctx.index * delayBetweenPoints;
            }
          }
        };

        //Configuration
        const config = {
            type: 'line',
            data: {
              datasets: [{
                borderWidth: 1,
                radius: 0,
                data: data,
              },
              {
                borderWidth: 1,
                radius: 0,
                data: data2,
              }]
            },
            options: {
              animation,
              interaction: {
                intersect: false
              },
              plugins: {
                legend: false
              },
              scales: {
                x: {
                  type: 'time',
                  time: {
                       unit: 'month'
                  }
                }
              }
            }
          };

        const ctx = document.getElementById(elementId).getContext('2d');

        const lineChart = new Chart(ctx, config);

    }
};