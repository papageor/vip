package gr.compiler.vip.screen.notificationswidget;

import gr.compiler.vip.entity.Notification;
import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("VIP_NotificationsWidget")
@UiDescriptor("notifications-widget.xml")
@DashboardWidget(name="Alerts")
public class NotificationsWidget extends ScreenFragment {
    @Autowired
    private CollectionLoader<Notification> notificationsDl;

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        notificationsDl.load();
    }

}