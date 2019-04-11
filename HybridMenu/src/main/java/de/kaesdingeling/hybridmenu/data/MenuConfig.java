package de.kaesdingeling.hybridmenu.data;

import com.vaadin.flow.component.icon.VaadinIcon;

import de.kaesdingeling.hybridmenu.data.enums.NotificationPosition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuConfig {
	@Builder.Default
	private VaadinIcon subMenuIcon = VaadinIcon.ANGLE_UP;

	@Builder.Default
	private VaadinIcon notificationButtonIcon = VaadinIcon.BELL;
	@Builder.Default
	private VaadinIcon notificationButtonEmptyIcon = VaadinIcon.BELL_O;
	@Builder.Default
	private VaadinIcon notificationCenterCloseIcon = VaadinIcon.ANGLE_RIGHT;
	@Builder.Default
	private VaadinIcon notificationRemoveIcon = VaadinIcon.CLOSE;

	@Builder.Default
	private NotificationPosition notificationPosition = NotificationPosition.BOTTOM;

	@Builder.Default
	private long notificationDisplayTime = 5000;

	@Builder.Default
	private VaadinIcon breadcrumbSeperatorIcon = VaadinIcon.ANGLE_RIGHT;
	@Builder.Default
	private boolean breadcrumbs = true;
	
	public static int notificationQueueMax = 200;
}