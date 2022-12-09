package gr.compiler.vip.screen.vesselwidgeteditor;

import gr.compiler.vip.entity.Vessel;
import io.jmix.core.DataManager;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.component.ComboBox;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@UiController("VIP_VesselWidgetEditor")
@UiDescriptor("vessel-widget-editor.xml")
public class VesselWidgetEditor extends ScreenFragment {
    @Autowired
    private ComboBox<Integer> vesselComboBox;

    @Autowired
    private DataManager dataManager;

    @WindowParam
    @WidgetParam
    protected int vesselId;

    @Subscribe
    public void onInit(InitEvent event) {
        List<Vessel> vessels = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v")
                .list();

        if (!vessels.isEmpty()) {
            Map<String, Integer> map = new LinkedHashMap<>();
            for (Vessel vessel : vessels) {
                map.put(vessel.getName(), vessel.getId().intValue());
            }
            vesselComboBox.setOptionsMap(map);

        }
    }



    @Subscribe("vesselComboBox")
    public void onVesselComboBoxValueChange(HasValue.ValueChangeEvent event) {
        if (event.getValue() == null)
            vesselId = 0;

        int vesselId = (int)event.getValue();

        Optional<Vessel> vesselOptional = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v where v.id = :vesselId")
                .parameter("vesselId", vesselId)
                .optional();

        if (vesselOptional.isPresent()) {
            vesselId = vesselOptional.get().getId();
        }
    }
    
    

}