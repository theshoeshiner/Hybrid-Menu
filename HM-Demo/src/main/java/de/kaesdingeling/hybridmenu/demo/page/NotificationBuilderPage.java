package de.kaesdingeling.hybridmenu.demo.page;

import org.vaadin.addon.sliders.PaperSlider;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import de.kaesdingeling.hybridmenu.components.Notification;
import de.kaesdingeling.hybridmenu.components.NotificationCenter;
import de.kaesdingeling.hybridmenu.data.enums.NotificationPosition;
import de.kaesdingeling.hybridmenu.demo.DemoRouterLayout;

@Route(value = "notification/builder", layout = DemoRouterLayout.class)
public class NotificationBuilderPage extends VerticalLayout {
	private static final long serialVersionUID = 2225084532665233458L;

	public NotificationBuilderPage() {
		add(new H2("Notification Builder"), new H5("Here you can click a notification together"));
		
		HorizontalLayout outputPosition = new HorizontalLayout(new Button("Top", e -> VaadinSession.getCurrent().getAttribute(NotificationCenter.class).setNotificationPosition(NotificationPosition.TOP)), new Button("Bottom", e -> VaadinSession.getCurrent().getAttribute(NotificationCenter.class).setNotificationPosition(NotificationPosition.BOTTOM)));
		
		TextField caption = new TextField("Title");
		TextArea content = new TextArea("Content");
		
		ComboBox<VaadinIcon> icon = new ComboBox<VaadinIcon>("Icon");
		
		icon.setItemLabelGenerator((i) -> i.name());
		icon.setItems(VaadinIcon.values());
		
		Label displayTimeLabel = new Label("Display time (ms)");
		PaperSlider displayTime = new PaperSlider(1000, 10000, 5000);
		
		displayTimeLabel.getElement().appendChild(displayTime.getElement());
		
		Checkbox closeable = new Checkbox("Closeable");
		Checkbox autoRemove = new Checkbox("Autoremove");
		Checkbox makeAsReaded = new Checkbox("Make as readed");
		Checkbox showDescriptionOnPopup = new Checkbox("Show description on popup");
		
		showDescriptionOnPopup.setValue(true);
		
		Label autoRemoveTimeLabel = new Label("Autoremove display offset delay (ms)");
		PaperSlider autoRemoveTime = new PaperSlider(0, 5000, 0);
		
		autoRemoveTimeLabel.getElement().appendChild(autoRemoveTime.getElement());
		
		Button execute = new Button("Execute", VaadinIcon.CODE.create());
		
		//execute.setDisableOnClick(true);
		execute.addClickListener(e -> {
			NotificationCenter notificationCenter = VaadinSession.getCurrent().getAttribute(NotificationCenter.class);
			if (notificationCenter.queueSize() < 10) {
				Notification notification = Notification.get()
						.withTitle(caption.getValue())
						.withContent(content.getValue())
						.withIcon(icon.getValue())
						.withDisplayTime(displayTime.getValue().longValue());
				
				if (closeable.getValue()) {
					notification.withCloseable();
				}
				
				if (makeAsReaded.getValue()) {
					notification.makeAsReaded();
				}
				
				if (autoRemove.getValue()) {
					if (autoRemoveTime.getValue() > 0) {
						notification.withAutoRemove(autoRemoveTime.getValue().longValue());
					} else {
						notification.withAutoRemove();
					}
				}
				
				notificationCenter.add(notification, showDescriptionOnPopup.getValue());
			}
		});
		
		add(outputPosition, caption, content, icon, displayTimeLabel, closeable, makeAsReaded, autoRemove, showDescriptionOnPopup, autoRemoveTimeLabel, execute);
	}
}