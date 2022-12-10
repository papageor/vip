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
import io.jmix.core.DataManager;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Subscribe
    public void onInit(InitEvent event) throws JsonProcessingException {
        MapState state = new MapState();

        Root root = loadVesselPositionsOnMap();

        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(root);

        //https://json2csharp.com/code-converters/json-to-pojo
        //String jsonObject="{\"type\": \"FeatureCollection\", \"features\": [{\"type\": \"Feature\",\"properties\": {\"name\": \"New York City\"},\"geometry\": {\"type\": \"Point\",\"coordinates\": [-73.778137, 40.641312]}}]}";

        //state.dataItems = jsonObject.replace("\n",",");
        state.dataItems = jsonString;

        mapComponent.setState(state);

        mapComponent.addFunction("bulletClicked", javaScriptCallbackEvent ->{
            String value = "";

            JsonArray array = javaScriptCallbackEvent.getArguments();
            if (array != null)
            {
                String vesselName = array.getString(0);

                notifications.create().withDescription(vesselName).show();
            }

            int i = 0;
        });
    }

    private Root loadVesselPositionsOnMap()
    {
        Root root = new Root();
        root.type = "FeatureCollection";
        root.features = new ArrayList<>();

        vesselPositions = influxConnectorService.getFleetCoordinates();

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


}

class MapState {
    public Map<String, Object> options;
    public String dataItems;
}