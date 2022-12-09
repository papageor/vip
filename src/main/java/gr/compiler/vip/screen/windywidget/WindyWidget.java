package gr.compiler.vip.screen.windywidget;

import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

@UiController("VIP_WindyWidget")
@UiDescriptor("windy-widget.xml")
@DashboardWidget(name = "Windy")
public class WindyWidget extends ScreenFragment {
}