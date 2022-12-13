ui_components_javascript_barchartjs = function () {
    var connector = this;
    var element = connector.getElement();
    element.innerHTML = "<div style=\"width: 800px;height: 400px;\"><canvas id=\"barChart\"></canvas></div>";

    connector.onStateChange = function () {
        var state = connector.getState();
        var data = state.data;

        const ctx = document.getElementById('barChart');

          new Chart(ctx, {
            type: 'bar',
            data: {
              labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
              datasets: [{
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                borderWidth: 1
              }]
            },
            options: {
              scales: {
                y: {
                  beginAtZero: true
                }
              }
            }
          });

    }
};