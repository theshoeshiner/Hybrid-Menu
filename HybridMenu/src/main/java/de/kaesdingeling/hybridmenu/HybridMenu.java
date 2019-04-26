package de.kaesdingeling.hybridmenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

import de.kaesdingeling.hybridmenu.components.BreadCrumbs;
import de.kaesdingeling.hybridmenu.components.LeftMenu;
import de.kaesdingeling.hybridmenu.components.NotificationCenter;
import de.kaesdingeling.hybridmenu.components.TopMenu;
import de.kaesdingeling.hybridmenu.data.DefaultViewChangeManager;
import de.kaesdingeling.hybridmenu.data.MenuConfig;
import de.kaesdingeling.hybridmenu.data.interfaces.HybridMenuRouter;
import de.kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import de.kaesdingeling.hybridmenu.data.interfaces.TopMenuButtons;
import de.kaesdingeling.hybridmenu.data.interfaces.ViewChangeManager;
import de.kaesdingeling.hybridmenu.utils.Styles;
import lombok.Getter;

@StyleSheet("frontend://HybridMenu/Styles/Styles.css")
@HtmlImport("frontend://HybridMenu/Styles/Styles.html")
public abstract class HybridMenu extends VerticalLayout implements RouterLayout, AfterNavigationObserver, HybridMenuRouter {
	private static final long serialVersionUID = -4055770717384786366L;

	@Getter private ViewChangeManager viewChangeManager = new DefaultViewChangeManager();
	@Getter private MenuConfig config = null;
	private boolean buildRunning = false;

	/* Components */
	@Getter private HorizontalLayout content = new HorizontalLayout();
	private BreadCrumbs breadCrumbs = null;
	@Getter private VerticalLayout rootContent = new VerticalLayout();
	@Getter private TopMenu topMenu = new TopMenu();
	@Getter private LeftMenu leftMenu = new LeftMenu();
	@Getter private NotificationCenter notificationCenter = new NotificationCenter();
	
	private List<Component> tempTopMenuComponents = new ArrayList<Component>();
	
	protected HybridMenu() {
		setSizeFull();
		getClassNames().add(Styles.hybridMenu);
		setMargin(false);
		setPadding(false);
		setSpacing(false);
		
		VaadinSession.getCurrent().setAttribute(MenuConfig.class, MenuConfig.builder().build());
		
		boolean build = init(VaadinSession.getCurrent(), UI.getCurrent());
		
		if (build) {
			build();
		}
	}

	public HybridMenu build() {
		if (!buildRunning) {
			content.setSizeFull();
			content.setMargin(false);
			content.setSpacing(false);
			content.getClassNames().add(Styles.rootContent);
			content.add(leftMenu, notificationCenter);
			
			add(topMenu, content);
			expand(content);
			
			if (config.isBreadcrumbs()) {
				rootContent.add(getBreadCrumbs());
			}
			
			rootContent.setWidth("100%");
			rootContent.setHeight("100%");
			rootContent.setMargin(false);
			rootContent.setPadding(false);
			rootContent.setSpacing(false);
			
			content.add(rootContent);
			content.expand(rootContent);
			
			notificationCenter.setNotificationPosition(config.getNotificationPosition());
			
			VaadinSession.getCurrent().setAttribute(MenuConfig.class, config);
			VaadinSession.getCurrent().setAttribute(HybridMenu.class, this);
			buildRunning = true;
		}
		return this;
	}
	
	public BreadCrumbs getBreadCrumbs() {
		if (breadCrumbs == null) {
			breadCrumbs = new BreadCrumbs();
		}
		
		return breadCrumbs;
	}

	public HybridMenu withConfig(MenuConfig config) {
		VaadinSession.getCurrent().setAttribute(MenuConfig.class, config);
		this.config = config;
		return this;
	}

	public void setViewChangeManager(ViewChangeManager viewChangeManager) {
		this.viewChangeManager = viewChangeManager;
	}
	
	@Override
	public void showRouterLayoutContent(HasElement content) {
		clearTempTopMenu();
        if (content != null) {
        	Component component = content.getElement().getComponent().get();
        	
        	content.getElement().getStyle().set("width", "100%");
        	content.getElement().getClassList().add(Styles.contentBox);
        	
        	if (!config.isBreadcrumbs()) {
        		component.getElement().getClassList().add(Styles.paddingTopContent);
			}
        	
        	this.rootContent.add(component);
        	this.rootContent.expand(component);
        	
        	if (component instanceof TopMenuButtons) {
        		setTempTopMenu((TopMenuButtons) component); 
        	}
        }
    }
	
	private void setTempTopMenu(TopMenuButtons topMenuButtons) {
		tempTopMenuComponents = topMenuButtons.topMenuButtons(tempTopMenuComponents);
		for (Component component : tempTopMenuComponents) {
			topMenu.add(component);
		}
	}
	
	private void clearTempTopMenu() {
		for (Component component : tempTopMenuComponents) {
			topMenu.remove(component);
		}
		tempTopMenuComponents.clear();
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		List<MenuComponent<?>> menuContentList = viewChangeManager.init(HybridMenu.this);
		viewChangeManager.manage(HybridMenu.this, leftMenu, event, menuContentList);
		viewChangeManager.manage(HybridMenu.this, topMenu, event, menuContentList);
		viewChangeManager.finish(HybridMenu.this, menuContentList);
	}
	
	public static String fileToString(File file) {
		StringBuilder result = new StringBuilder("");

		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return result.toString();
	}
	
	public static InputStream fileToInputStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}
	
	public static File getFile(String fileName) {
		return new File(HybridMenu.class.getClassLoader().getResource(fileName).getFile());
	}
}