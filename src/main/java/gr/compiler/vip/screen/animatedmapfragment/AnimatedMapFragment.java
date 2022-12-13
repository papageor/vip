package gr.compiler.vip.screen.animatedmapfragment;

import amcharts.*;
import amcharts.Properties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.compiler.vip.app.DataService;
import gr.compiler.vip.entity.Vessel;
import gr.compiler.vip.entity.VesselPoint;
import io.jmix.core.DataManager;
import io.jmix.core.session.SessionData;
import io.jmix.dashboards.model.Widget;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private SessionData sessionData;

    @Subscribe
    public void onInit(InitEvent event)  {
        AnimatedMapState state = new AnimatedMapState();

        List<AnimatedRoot> animatedRoots = getVesseRoute();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try{
            jsonString = mapper.writeValueAsString(animatedRoots);
        }
        catch (JsonProcessingException e)
        {
            jsonString = "";
        }
        state.dataItems = jsonString;

        animatedMapComponent.setState(state);

    }

    private int getVesselReferenceId()
    {
        int referenceId = 0;

        //if (StringUtils.isNotEmpty(vesselName))
        //    return vesselName;

        if (sessionData.getAttribute("session-vessel-sid") != null){
            String sessionValue = sessionData.getAttribute("session-vessel-sid").toString();

            referenceId = Integer.parseInt(sessionValue);
        }


        return referenceId;
    }

    private List<AnimatedRoot> getVesseRoute()
    {
        int referenceId = getVesselReferenceId();

        List<AnimatedRoot> animatedRoots = new ArrayList<>();

        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime before30Days = today.minusDays(30);

        Date fromDate = Date.from(before30Days.atZone(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());


        Optional<Vessel> vesselOpt = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v " +
                        "where v.referenceId = :sid")
                .parameter("sid", referenceId)
                .maxResults(1)
                .optional();
        if (vesselOpt.isPresent())
        {
            TreeMap<Date, VesselPoint> vesselPoints =  dataService.getVesselRoute(vesselOpt.get(),fromDate,toDate);

            if (!vesselPoints.isEmpty())
            {
                for(Map.Entry<Date, VesselPoint> entry: vesselPoints.entrySet())
                {
                    AnimatedRoot root = new AnimatedRoot();
                    root.latitude = entry.getValue().getLatitude();
                    root.longitude = entry.getValue().getLongitude();

                    animatedRoots.add(root);

                }
            }
        }

        return animatedRoots;
    }
}

class AnimatedMapState {
    public Map<String, Object> options;
    public String dataItems;
}