package gr.compiler.vip.screen.animatedmapfragment;

import amcharts.Feature;
import amcharts.Geometry;
import amcharts.Properties;
import amcharts.Root;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.compiler.vip.app.DataService;
import gr.compiler.vip.entity.Vessel;
import gr.compiler.vip.entity.VesselPoint;
import io.jmix.core.DataManager;
import io.jmix.dashboards.model.Widget;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@UiController("VIP_AnimatedMapFragment")
@UiDescriptor("animatedmap-fragment.xml")
public class AnimatedMapFragment extends ScreenFragment {
    @WindowParam
    @WidgetParam
    protected String vesselName;

    @WindowParam
    protected Widget widget;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private DataService dataService;
    @Autowired
    private JavaScriptComponent animatedMapComponent;

    @Subscribe
    public void onInit(InitEvent event)  {
        AnimatedMapState state = new AnimatedMapState();

        Root root = getVesseRoute();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try{
            jsonString = mapper.writeValueAsString(root);
        }
        catch (JsonProcessingException e)
        {
            jsonString = "";
        }
        state.dataItems = jsonString;

        animatedMapComponent.setState(state);

    }

    private Root getVesseRoute()
    {
        Root root = new Root();
        root.type = "FeatureCollection";
        root.features = new ArrayList<>();

        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime before30Days = today.minusDays(30);

        Date fromDate = Date.from(before30Days.atZone(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());


        Optional<Vessel> vesselOpt = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v " +
                        "where v.name = :vesselname")
                .parameter("vesselname", vesselName)
                .maxResults(1)
                .optional();
        if (vesselOpt.isPresent())
        {
            TreeMap<Date, VesselPoint> vesselPoints =  dataService.getVesselRoute(vesselOpt.get(),fromDate,toDate);

            if (!vesselPoints.isEmpty())
            {
                for(Map.Entry<Date, VesselPoint> entry: vesselPoints.entrySet())
                {
                    Properties properties = new Properties();
                    properties.name = "";

                    ArrayList<Double> coordinates = new ArrayList<>();
                    coordinates.add(entry.getValue().getLongitude());
                    coordinates.add(entry.getValue().getLatitude());

                    Geometry geometry = new Geometry();
                    geometry.type="Point";
                    geometry.coordinates = coordinates;

                    Feature feature = new Feature();
                    feature.type = "Feature";
                    feature.properties = properties;
                    feature.geometry = geometry;

                    root.features.add(feature);
                }
            }
        }

        return root;
    }
}

class AnimatedMapState {
    public Map<String, Object> options;
    public String dataItems;
}