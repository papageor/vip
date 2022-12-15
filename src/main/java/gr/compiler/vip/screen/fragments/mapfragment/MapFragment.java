package gr.compiler.vip.screen.fragments.mapfragment;

import amcharts.Feature;
import amcharts.Geometry;
import amcharts.Properties;
import amcharts.Root;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import elemental.json.JsonArray;
import gr.compiler.vip.app.InfluxConnectorService;
import gr.compiler.vip.entity.Coordinates;
import gr.compiler.vip.entity.Vessel;
import gr.compiler.vip.screen.vesseloperation.VesselOperationScreen;
import io.jmix.core.DataManager;
import io.jmix.core.session.SessionData;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.Screens;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@UiController("VIP_MapFragment")
@UiDescriptor("map-fragment.xml")
public class MapFragment extends ScreenFragment {
    @Autowired
    private JavaScriptComponent mapComponent;
    @Autowired
    private Notifications notifications;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private InfluxConnectorService influxConnectorService;
    private HashMap<String, Coordinates> vesselPositions;
    @Autowired
    private Screens screens;

    @Autowired
    private ObjectProvider<SessionData> sessionDataProvider;

    @Subscribe
    public void onInit(InitEvent event) throws JsonProcessingException {
        MapState state = new MapState();

        Root root = loadVesselPositionsOnMap();

        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(root);

        //https://json2csharp.com/code-converters/json-to-pojo
        state.dataItems = jsonString;

        mapComponent.setState(state);

        mapComponent.addFunction("bulletClicked", javaScriptCallbackEvent ->{
            String value = "";

            JsonArray array = javaScriptCallbackEvent.getArguments();
            if (array != null)
            {
                String vesselName = array.getString(0);

                Optional<Vessel> vesselOpt = dataManager.load(Vessel.class)
                        .query("select v from VIP_Vessel v " +
                                "where v.name = :name")
                        .parameter("name", vesselName)
                        .maxResults(1)
                        .optional();
                if (vesselOpt.isPresent())
                {
                    //Set session data
                    sessionDataProvider.getObject().setAttribute("session-vessel-sid", vesselOpt.get().getReferenceId().toString());

                    VesselOperationScreen screen = screens.create(VesselOperationScreen.class, OpenMode.NEW_TAB);
                    screen.setVesselName(vesselName);

                    screen.show();
                }

            }

            int i = 0;
        });
    }

    private Root loadVesselPositionsOnMap()
    {
        Root root = new Root();
        root.type = "FeatureCollection";
        root.features = new ArrayList<>();

        //vesselPositions = influxConnectorService.getFleetCoordinates();
        vesselPositions = getFleetCoordinatesMockUp();

        if (!vesselPositions.isEmpty())
        {
            for(Map.Entry<String,Coordinates> vposition: vesselPositions.entrySet())
            {
                Properties properties = new Properties();
                properties.name = vposition.getKey();

                ArrayList<Double> coordinates = new ArrayList<>();
                coordinates.add(vposition.getValue().getLongitude());
                coordinates.add(vposition.getValue().getLatitude());

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

        return root;
    }

    private HashMap<String,Coordinates> getFleetCoordinatesMockUp()
    {
        HashMap<String,Coordinates> coordinates = new HashMap<>();

        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(57.9665);
            coordinate.setLongitude(21.2066);
            coordinates.put("Modena", coordinate);
        }

        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(27.8422);
            coordinate.setLongitude(-32.0592);
            coordinates.put("Enterpise", coordinate);
        }

        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(-9.098576);
            coordinate.setLongitude(76.3973);
            coordinates.put("Beaver", coordinate);
        }


        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(33.7273);
            coordinate.setLongitude(156.2020);
            coordinates.put("Trusty", coordinate);
        }

        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(33.2876);
            coordinate.setLongitude(-75.3018);
            coordinates.put("Brave", coordinate);
        }

        {
            Coordinates coordinate = new Coordinates();
            coordinate.setLatitude(16.3024);
            coordinate.setLongitude(-60.0088);
            coordinates.put("Hoffen", coordinate);
        }

        return coordinates;
    }


}

class MapState {
    public Map<String, Object> options;
    public String dataItems;
}