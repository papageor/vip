package gr.compiler.vip.screen.vesselwidget;

import gr.compiler.vip.entity.Vessel;
import gr.compiler.vip.screen.vesseloperation.VesselOperationScreen;
import io.jmix.core.session.SessionData;
import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.ui.Screens;
import io.jmix.ui.component.DataGrid;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("VIP_VesselWidget")
@UiDescriptor("vessel-widget.xml")
@DashboardWidget(name = "Vessels")
public class VesselWidget extends ScreenFragment {
    @Autowired
    private Screens screens;
    @Autowired
    private ObjectProvider<SessionData> sessionDataProvider;

    @Subscribe("vesselsTable")
    public void onVesselsTableItemClick(DataGrid.ItemClickEvent<Vessel> event) {
        Vessel vessel = event.getItem();
        if (vessel != null)
        {
            sessionDataProvider.getObject().setAttribute("session-vessel-sid", vessel.getReferenceId().toString());
            VesselOperationScreen screen = screens.create(VesselOperationScreen.class, OpenMode.NEW_TAB);
            screen.setVesselName(vessel.getName());

            screen.show();
        }
    }

}