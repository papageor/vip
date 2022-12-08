package gr.compiler.vip.screen.vesselwidgeteditor;

import gr.compiler.vip.entity.Vessel;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("VIP_VesselWidgetEditor")
@UiDescriptor("vessel-widget-editor.xml")
public class VesselWidgetEditor extends ScreenFragment {
    @Autowired
    private EntityPicker<Vessel> vesselPicker;

    @WindowParam
    @WidgetParam
    protected Vessel vessel;

    @Subscribe("vesselPicker")
    public void onVesselPickerValueChange(HasValue.ValueChangeEvent<Vessel> event) {
        vessel = vesselPicker.getValue();
    }
}