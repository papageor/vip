package gr.compiler.vip.screen.camerawidget;

import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;

@UiController("VIP_CameraWidget")
@UiDescriptor("camera-widget.xml")
@DashboardWidget(name="ImageViewer")
public class CameraWidget extends ScreenFragment {
}