package gr.compiler.vip.screen.notification;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.Notification;

@UiController("VIP_Notification.edit")
@UiDescriptor("notification-edit.xml")
@EditedEntityContainer("notificationDc")
public class NotificationEdit extends StandardEditor<Notification> {
}