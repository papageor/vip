package gr.compiler.vip.screen.linechartcommontagwidget;

import gr.compiler.vip.entity.CommonTag;
import io.jmix.dashboards.model.Widget;
import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

@UiController("VIP_LinechartCommontagWidget")
@UiDescriptor("linechart-commontag-widget.xml")
@DashboardWidget(name = "LineChartForCommonTags")
public class LinechartCommontagWidget extends ScreenFragment {
    @WindowParam
    @WidgetParam
    protected String vesselName;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag2;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag3;


    @WindowParam
    protected Widget widget;

    @Subscribe
    public void onInit(InitEvent event) {
        
    }
    
    
}