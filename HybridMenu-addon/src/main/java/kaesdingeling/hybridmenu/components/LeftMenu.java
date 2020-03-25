package kaesdingeling.hybridmenu.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import kaesdingeling.hybridmenu.data.enums.ToggleMode;
import kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import kaesdingeling.hybridmenu.utils.Utils;

public class LeftMenu extends VerticalLayout implements MenuComponent<VerticalLayout> {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(LeftMenu.class);
	
	private static final long serialVersionUID = 8774849625123603883L;
	
	public static final String CLASS_NAME = "leftMenu";
	
	/*
	private VerticalLayout content = new VerticalLayout();
	private HorizontalLayout footer = null;
	*/
	
	String normalWidth = "225px";
	String minimalWidth = "50px";

	public LeftMenu() {
		super();
		setWidth(normalWidth);
		setHeight(100, Unit.PERCENTAGE);
		setStyleName(CLASS_NAME);
		setMargin(false);
		setSpacing(false);
		LOGGER.info("LeftMenu()");
		/*
		content.setSizeFull();
		content.setSpacing(false);
		content.setMargin(false);
		content.setStyleName("content");
		addComponentsAndExpand(content);
		*/
	}
	
	
	
	public String getNormalWidth() {
		return normalWidth;
	}



	public void setNormalWidth(String normalWidth) {
		this.normalWidth = normalWidth;
		this.setWidth(normalWidth);
	}



	public String getMinimalWidth() {
		return minimalWidth;
	}



	public void setMinimalWidth(String minimalWidth) {
		this.minimalWidth = minimalWidth;
	}



	public LeftMenu toggleSize() {
		
		if (getToggleMode().equals(ToggleMode.NORMAL)) {
			setToggleMode(ToggleMode.MINIMAL);
		} else {
			setToggleMode(ToggleMode.NORMAL);
		}
		return this;
	}
	
	public LeftMenu setToggleMode(ToggleMode toggleMode) {
		if (toggleMode != null) {
			switch (toggleMode) {
				case MINIMAL:
					setWidth(minimalWidth);
					getParent().addStyleName(ToggleMode.MINIMAL.name().toLowerCase());
					break;
				case NORMAL:
					setWidth(normalWidth);
					getParent().removeStyleName(ToggleMode.MINIMAL.name().toLowerCase());
					break;
			}
			VaadinSession session = VaadinSession.getCurrent();
			if (session != null) {
				session.setAttribute(ToggleMode.class, toggleMode);
			}
		}
		return this;
	}
	
	public void applyToggleMode() {
		LOGGER.info("applyToggleMode: {}",getToggleMode());
		ToggleMode toggleMode = getToggleMode();
		setToggleMode(toggleMode);
			

	
	}
	
	public ToggleMode getToggleMode() {
		
		VaadinSession session = VaadinSession.getCurrent();
		LOGGER.info("gettogglemode session: {}"+session);
		if (session != null) {
			ToggleMode toggleMode = session.getAttribute(ToggleMode.class);
			LOGGER.info("gettogglemode: {}"+toggleMode);
			if (toggleMode != null) {
				return toggleMode;
			}
		}
		return ToggleMode.NORMAL;
	}
	
	public <C extends MenuComponent<?>> C add(C c) {
		addComponent(Utils.setDefaults(c));
		return c;
	}
	
	@Override
	public <C extends MenuComponent<?>> C addAsFirst(C c) {
		addComponentAsFirst(Utils.setDefaults(c));
		return c;
	}

	@Override
	public <C extends MenuComponent<?>> C addAt(C c, int index) {
		addComponent(Utils.setDefaults(c), index);
		return c;
	}
	
	@Override
	public int count() {
		return getList().size();
	}
	
	@Override
	public <C extends MenuComponent<?>> LeftMenu remove(C c) {
		removeComponent(c);
		return this;
	}
	
	/*
	public HMButton addToFooter(HMButton c) {
		if (footer == null) {
			footer = new HorizontalLayout();
			footer.setMargin(false);
			footer.setSpacing(true);
			footer.setStyleName("footer");
			footer.setWidth(100, Unit.PERCENTAGE);
			addComponent(footer);
		}
		footer.addComponent(c);
		return c;
	}
	*/
	
	public LeftMenu remove(Component component) {
		removeComponent(component);
		return this;
	}
	
	/*
	public LeftMenu removeFromFooter(HMButton component) {
		if (footer != null) {
			footer.removeComponent(component);
		}
		return this;
	}
	
	public List<Component> getFooterComponents() {
		List<Component> componentsList = new ArrayList<Component>();
		if (footer != null) {
			for (int i = 0; i < footer.getComponentCount(); i++) {
				componentsList.add(footer.getComponent(i));
			}
		}
		return componentsList;
	}
	*/
	
	@Override
	public List<MenuComponent<?>> getList() {
		List<MenuComponent<?>> menuComponentList = new ArrayList<MenuComponent<?>>();
		for (int i = 0; i < getComponentCount(); i++) {
			Component component = getComponent(i);
			if (component instanceof MenuComponent<?>) {
				menuComponentList.add((MenuComponent<?>) component);
			}
		}
		return menuComponentList;
	}

	@Override
	public String getRootStyle() {
		return super.getPrimaryStyleName();
	}
}