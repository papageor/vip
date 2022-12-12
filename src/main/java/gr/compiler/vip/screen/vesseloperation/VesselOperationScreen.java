package gr.compiler.vip.screen.vesseloperation;

import gr.compiler.vip.app.OperDashboardAssistant;
import io.jmix.core.EntityEntry;
import io.jmix.dashboards.model.parameter.Parameter;
import io.jmix.dashboards.model.parameter.type.ParameterValue;
import io.jmix.dashboards.model.parameter.type.StringParameterValue;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import io.jmix.dashboardsui.component.Dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UiController("VIP_VesselOperationScreen")
@UiDescriptor("vessel-operation-screen.xml")
public class VesselOperationScreen extends Screen {

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

    }
    private String vesselName;

    public void setVesselName(String vesselName)
    {
        this.vesselName = vesselName;
    }


}