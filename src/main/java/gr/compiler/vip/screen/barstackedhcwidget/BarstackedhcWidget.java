package gr.compiler.vip.screen.barstackedhcwidget;

import chartjs.TimeSeriesJS;
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
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@UiController("VIP_BarstackedhcWidget")
@UiDescriptor("barstackedhc-widget.xml")
@DashboardWidget(name="StackedBarHC")
public class BarstackedhcWidget extends ScreenFragment {
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
    private JavaScriptComponent BarStackedHCComponent;

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        BarStackedHCState state = new BarStackedHCState();

        String jsonCategories = "";
        String jsonValues = "";
        String jsonValues2 = "";

        List<Date> categories = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Double> values2 = new ArrayList<>();
        List<TimeSeriesJS> timeSeries = getVesselConsumptionData();

        List<String> dates = new ArrayList<>();
        for(TimeSeriesJS point:timeSeries.stream().sorted(Comparator.comparing(TimeSeriesJS::getX)).collect(Collectors.toList()))
        {
            LocalDate localDate = convertToLocalDate(point.getX());
            String localDateText = String.format("%d-%d-%d",localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth());
            if (!dates.contains(localDateText)){
                categories.add(point.getX());
                values.add(point.getY());
                values2.add(getRandomFactor() * point.getY());

                dates.add(localDateText);
            }

        }

        ObjectMapper mapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("MM-dd");
        mapper.setDateFormat(df);

        try{
            jsonCategories = mapper.writeValueAsString(categories);
            jsonValues = mapper.writeValueAsString(values);
            jsonValues2 = mapper.writeValueAsString(values2);
        }
        catch (JsonProcessingException e)
        {
            jsonCategories = "";
            jsonValues = "";
            jsonValues2 = "";
        }

        state.categories = jsonCategories;
        state.chartTitle = "Chart showing vessel's daily consumption by type (Mt/Day)";
        state.xAxisTitle = "Daily Quantities";

        state.dataItems = jsonValues;
        state.dataSeriesTitle = "HFO";

        state.dataItems2 = jsonValues2;
        state.dataSeriesTitle2 = "LSFO";


        BarStackedHCComponent.setState(state);
    }

    private List<TimeSeriesJS> getVesselConsumptionData()
    {
        List<TimeSeriesJS> timeSeries = new ArrayList<>();

        TreeMap<Date, Double> timeSeriesMap = getVesselConsumptionTimeSeries();
        if (!timeSeriesMap.isEmpty())
        {
            for(Map.Entry<Date,Double> entry:timeSeriesMap.entrySet())
            {
                TimeSeriesJS point = new TimeSeriesJS();
                point.setX(entry.getKey());
                point.setY(entry.getValue());

                timeSeries.add(point);
            }
        }

        return timeSeries;
    }
    private TreeMap<Date, Double> getVesselConsumptionTimeSeries()
    {
        TreeMap<Date, Double> timeSeries = new TreeMap<>();

        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime beforeDays = today.minusDays(10);

        Date fromDate = Date.from(beforeDays.atZone(ZoneId.systemDefault()).toInstant());
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
            timeSeries = dataService.getVesselConsumption(vesselOpt.get(), fromDate,toDate);
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

    public LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public double getRandomFactor()
    {
        double factor = 0.0;

        factor = 2.0 * Math.random() / 10.0;

        BigDecimal a = new BigDecimal(factor);
        BigDecimal roundedFactor = a.setScale(2, RoundingMode.UP);

        return roundedFactor.doubleValue();
    }
}

class BarStackedHCState {
    public Map<String, Object> options;
    public String chartTitle;
    public String categories;
    public String dataItems;
    public String dataItems2;
    public String dataSeriesTitle;
    public String dataSeriesTitle2;
    public String xAxisTitle;

}