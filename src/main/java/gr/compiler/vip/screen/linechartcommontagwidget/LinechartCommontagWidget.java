package gr.compiler.vip.screen.linechartcommontagwidget;

import amcharts.TimeSeries;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.compiler.vip.app.DataService;
import gr.compiler.vip.entity.CommonTag;
import gr.compiler.vip.entity.Vessel;
import io.jmix.core.DataManager;
import io.jmix.core.session.SessionData;
import io.jmix.dashboards.model.Widget;
import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@UiController("VIP_LinechartCommontagWidget")
@UiDescriptor("linechart-commontag-widget.xml")
@DashboardWidget(name = "LineChartForCommonTags")
public class LinechartCommontagWidget extends ScreenFragment {
    @WindowParam
    @WidgetParam
    protected String vesselName;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag2;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag3;

    @WindowParam
    protected Widget widget;

    @Autowired
    private SessionData sessionData;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DataService dataService;
    @Autowired
    private JavaScriptComponent linechartTagComponent;


    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        LineChartState state = new LineChartState();

        List<TimeSeries> timeSeries = new ArrayList<>();

        TreeMap<Date, Double> timeSeriesMap = getVesselTagTimeSeries();
        if (!timeSeriesMap.isEmpty())
        {
            for(Map.Entry<Date,Double> entry:timeSeriesMap.entrySet())
            {
                TimeSeries point = new TimeSeries();
                point.date = entry.getKey();
                point.value = entry.getValue();

                timeSeries.add(point);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);

        String jsonString = "";
        try{
            jsonString = mapper.writeValueAsString(timeSeries);
        }
        catch (JsonProcessingException e)
        {
            jsonString = "";
        }
        state.dataItems = jsonString;

        linechartTagComponent.setState(state);
    }


    private TreeMap<Date, Double> getVesselTagTimeSeries()
    {
        TreeMap<Date, Double> timeSeries = new TreeMap<>();

        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime before30Days = today.minusDays(30);

        Date fromDate = Date.from(before30Days.atZone(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());

        int referenceId = getVesselReferenceId();

        Optional<Vessel> vesselOpt = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v " +
                        "where v.referenceId = :sid")
                .parameter("sid", referenceId)
                .maxResults(1)
                .optional();
        if (vesselOpt.isPresent())
        {
            timeSeries = dataService.getTagValues(vesselOpt.get(), commonTag,fromDate,toDate);
        }

        return timeSeries;
    }
    private int getVesselReferenceId()
    {
        int referenceId = 0;

        if (StringUtils.isNotEmpty(vesselName))
        {
            Optional<Vessel> vesselOpt = dataManager.load(Vessel.class)
                    .query("select v from VIP_Vessel v " +
                            "where v.name = :name")
                    .parameter("name", vesselName)
                    .maxResults(1)
                    .optional();
            if (vesselOpt.isPresent())
            {
                return vesselOpt.get().getReferenceId();
            }
        }


        if (sessionData.getAttribute("session-vessel-sid") != null){
            String sessionValue = sessionData.getAttribute("session-vessel-sid").toString();

            referenceId = Integer.parseInt(sessionValue);
        }


        return referenceId;
    }

}

class LineChartState {
    public Map<String, Object> options;
    public String dataItems;
}