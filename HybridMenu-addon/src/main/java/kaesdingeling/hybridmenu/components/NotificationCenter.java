package kaesdingeling.hybridmenu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.data.MenuConfig;
import kaesdingeling.hybridmenu.data.enums.NotificationPosition;
import kaesdingeling.hybridmenu.data.interfaces.MenuComponent;

@SuppressWarnings("deprecation")
public class NotificationCenter extends VerticalLayout {
	private static final long serialVersionUID = 4526129172208540022L;
	
	public static final String CLASS_NAME = "notificationCenter";
	
	public HybridMenu hybridMenu = null;
	public UI ui = UI.getCurrent();

	private VerticalLayout content = new VerticalLayout();
	private HorizontalLayout buttonLine = new HorizontalLayout();
	private CssLayout showNotificationRoot = new CssLayout();
	private CssLayout showNotifications = new CssLayout();
	
	private ArrayBlockingQueue<Notification> notificationQueue = new ArrayBlockingQueue<Notification>(MenuConfig.notificationQueueMax);
	
	private HMButton notiButton = null;
	
	public ExecutorService exe = Executors.newSingleThreadExecutor();
	
	public NotificationCenter(HybridMenu hybridMenu) {
		super();
		setHeight(100, Unit.PERCENTAGE);
		setWidth(0, Unit.PIXELS);
		setStyleName(CLASS_NAME);
		setMargin(false);
		setSpacing(false);
		
		this.hybridMenu = hybridMenu;
		
		exe.execute(() -> {
			try {
				boolean initBoot = false;
				while (this.getUI() != null && this.getUI().isAttached() || !initBoot) {
					initBoot = true;
					
					TimeUnit.MILLISECONDS.sleep(500);
					
					Notification noti = notificationQueue.take();
					
					noti.build(this);
					
					while (this.getUI() != null && this.getUI().isAttached() && showNotifications.getComponentCount() >= hybridMenu.getConfig().getNotificationDisplayCount()) {
						TimeUnit.SECONDS.sleep(1);
					}
					
					ui.access(() -> {
						showNotifications.addComponent(noti);
						
						runOneAttached(noti, () -> {
							noti.addStyleName("show");
							
							runOneAttached(noti, () -> {
								noti.removeStyleName("show");
								runOneAttached(noti, () -> {
									showNotifications.removeComponent(noti);
									
								}, 1000);
							}, noti.getDisplayTime());
						}, 100);
					});
				}
			} catch (Exception e) {
				com.vaadin.ui.Notification vaadinNotification = new com.vaadin.ui.Notification("Notification of the NotificationCenter is no longer possible!", Type.WARNING_MESSAGE);
				vaadinNotification.setPosition(Position.BOTTOM_RIGHT);
				vaadinNotification.setDescription("Error: " + e.getMessage());
				vaadinNotification.show(ui.getPage());
				e.printStackTrace();
			}
		});
		
		buttonLine.setSpacing(true);
		buttonLine.setMargin(false);
		buttonLine.setStyleName("buttonLine");
		
		content.setSpacing(false);
		content.setMargin(false);
		content.setStyleName("content");
		
		showNotifications.setHeight(0, Unit.PIXELS);
		showNotifications.setStyleName("lastNotification");
		
		VaadinSession.getCurrent().setAttribute(NotificationCenter.class, this);
		
		addButtonLine(HMButton.get().withIcon(FontAwesome.ANGLE_RIGHT).withClickListener(e -> close()));
	}
	
	public void build() {
		if (hybridMenu.getConfig().getNotificationPopupPosition().equals(NotificationPosition.TOP) && !showNotifications.getStyleName().contains("top")) {
			showNotifications.addStyleName("top");
		} else {
			showNotifications.removeStyleName("top");
		}
		if (hybridMenu.getConfig().getNotificationButtonLinePosition().equals(NotificationPosition.TOP) && !showNotifications.getStyleName().contains("top")) {
			addComponents(buttonLine, content);
		} else {
			addComponents(content, buttonLine);
		}
		showNotificationRoot.setHeight(0, Unit.PIXELS);
		showNotificationRoot.addComponent(showNotifications);
		addComponent(showNotificationRoot);
		setExpandRatio(content, 1);
	}
	
	public <C extends MenuComponent<?>> C addButtonLine(C c) {
		c.setPrimaryStyleName(c.getClass().getSimpleName());
		buttonLine.addComponentAsFirst(c);
		return c;
	}
	
	public <C extends MenuComponent<?>> NotificationCenter removeButtonLine(C c) {
		buttonLine.removeComponent(c);
		return this;
	}
	
	public boolean add(Notification notification) {
		return add(notification, true);
	}
	
	public boolean add(Notification notification, boolean showDescriptionOnPopup) {
		if (isOpen()) {
			notification.makeAsReaded();
		}

		Notification notificationClone = notification.clone();
		
		content.addComponentAsFirst(notification.build(this));
		updateToolTip();
		
		if (!showDescriptionOnPopup) {
			notificationClone.withContent("");
		} else {
			if (notificationClone.getContent().length() > hybridMenu.getConfig().getNotificationPopupMaxContentLength()) {
				notificationClone.withContent(notificationClone.getContent().substring(0, hybridMenu.getConfig().getNotificationPopupMaxContentLength()));
			}
		}
		
		return notificationQueue.add(notificationClone);
	}
	
	public NotificationCenter remove(Notification notification) {
		content.removeComponent(notification);
		return this;
	}
	
	public int queueSize() {
		return notificationQueue.size();
	}
	
	public List<Notification> getAll() {
		List<Notification> notificationsList = new ArrayList<Notification>();
		for (int i = 0; i < content.getComponentCount(); i++) {
			Component component = content.getComponent(i);
			if (component instanceof Notification) {
				notificationsList.add((Notification) component);
			}
		}
		return notificationsList;
	}
	
	public NotificationCenter open() {
		addStyleName("open");
		getAll().forEach(e -> {
			e.update(this);
			e.makeAsReaded();
		});
		updateToolTip();
		return this;
	}
	
	public NotificationCenter close() {
		removeStyleName("open");
		return this;
	}
	
	public NotificationCenter toggle() {
		if (isOpen()) {
			close();
		} else {
			open();
		}
		return this;
	}
	
	public boolean isOpen() {
		return getStyleName().contains("open");
	}
	
	public NotificationCenter updateToolTip() {
		int unreaded = 0;
		for (Notification notification : getAll()) {
			if (!notification.isReaded()) {
				unreaded++;
			}
		}
		notiButton.withToolTip(unreaded);
		if (unreaded > 0) {
			notiButton.setIcon(VaadinSession.getCurrent().getAttribute(MenuConfig.class).getNotificationButtonIcon());
		} else {
			notiButton.setIcon(VaadinSession.getCurrent().getAttribute(MenuConfig.class).getNotificationButtonEmptyIcon());
		}
		return this;
	}
	
	public NotificationCenter setNotiButton(HMButton notiButton) {
		this.notiButton = notiButton;
		this.notiButton.addClickListener(e -> toggle());
		updateToolTip();
		return this;
	}
	
	public static Thread runWhileAttached(final Component component, final Runnable task, final long initSleep, final long sleep) {
		return initThread(() -> {
            try {
                Thread.sleep(initSleep);
                while (component.getUI() != null && component.getUI().isAttached()) {
                    Future<Void> future = component.getUI().access(task);
                    future.get();
                    Thread.sleep(sleep);
                }
            } catch (Exception e) {
			}
        });
	}
	
	public static Thread runOneAttached(final Component component, final Runnable task, final long initSleep) {
        return initThread(() -> {
        	 try {
                 Thread.sleep(initSleep);
                 if (component.getUI() != null && component.getUI().isAttached()) {
                     Future<Void> future = component.getUI().access(task);
                     future.get();
                 }
             } catch (Exception e) {
             }
        });
	}
	
	public static Thread initThread(final Runnable task) {
		final Thread thread = new Thread(task);
		thread.start();
		return thread;
	}
}