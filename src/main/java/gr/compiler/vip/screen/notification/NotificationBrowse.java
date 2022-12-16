package gr.compiler.vip.screen.notification;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.Notification;

@UiController("VIP_Notification.browse")
@UiDescriptor("notification-browse.xml")
@LookupComponent("notificationsTable")
public class NotificationBrowse extends StandardLookup<Notification> {
}