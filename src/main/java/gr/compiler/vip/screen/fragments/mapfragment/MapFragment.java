package gr.compiler.vip.screen.fragments.mapfragment;

import elemental.json.JsonArray;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.JavaScriptComponent;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@UiController("VIP_MapFragment")
@UiDescriptor("map-fragment.xml")
public class MapFragment extends ScreenFragment {
    @Autowired
    private JavaScriptComponent mapComponent;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onInit(InitEvent event) {
        MapState state = new MapState();

        //https://json2csharp.com/code-converters/json-to-pojo
        String jsonObject="{\"type\": \"FeatureCollection\", \"features\": [{\"type\": \"Feature\",\"properties\": {\"name\": \"New York City\"},\"geometry\": {\"type\": \"Point\",\"coordinates\": [-73.778137, 40.641312]}}]}";

        state.dataItems = jsonObject.replace("\n",",");

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


}

class MapState {
    public Map<String, Object> options;
    public String dataItems;
}