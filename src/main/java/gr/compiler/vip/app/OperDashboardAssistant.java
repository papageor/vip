package gr.compiler.vip.app;

import io.jmix.dashboards.model.parameter.Parameter;
import io.jmix.dashboards.model.parameter.type.StringParameterValue;
import io.jmix.dashboardsui.component.Dashboard;
import io.jmix.dashboardsui.dashboard.assistant.DashboardViewAssistant;
import io.jmix.dashboardsui.event.DashboardUpdatedEvent;
import io.jmix.ui.screen.ScreenFragment;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component("VIP_OperDashboardAssistant")
@Scope("prototype")

public class OperDashboardAssistant implements DashboardViewAssistant {
    protected Dashboard dashboard;

    private String vesselName;

    public void setVesselName(String vesselName)
    {
        this.vesselName = vesselName;
    }
    @Override
    public void init(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @EventListener
    public void dashboardEventListener(DashboardUpdatedEvent event){
        //ScreenFragment widget = dashboard.getWidget("visits-calendar");
        List<Parameter> parameterList = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setId(UUID.randomUUID());
        parameter.setName("Vessel Name");
        parameter.setAlias("vesselName");

        StringParameterValue parameterValue = new StringParameterValue();
        parameterValue.setValue(vesselName);
        parameter.setValue(parameterValue);

        parameterList.clear();
        parameterList.add(parameter);

        dashboard.setXmlParameters(parameterList);
    }
}