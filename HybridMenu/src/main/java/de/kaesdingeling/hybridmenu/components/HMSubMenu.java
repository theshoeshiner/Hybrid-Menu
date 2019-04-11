package de.kaesdingeling.hybridmenu.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import de.kaesdingeling.hybridmenu.data.MenuConfig;
import de.kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import de.kaesdingeling.hybridmenu.utils.Styles;

@SuppressWarnings("hiding")
public class HMSubMenu extends VerticalLayout implements MenuComponent<HMSubMenu> {
	private static final long serialVersionUID = 5694189462883703860L;
	
	private HMButton button = new HMButton("");
	private VerticalLayout content = new VerticalLayout();
	
	public static HMSubMenu get() {
		return new HMSubMenu("");
	}
	
	public HMSubMenu(String caption) {
		build(caption, null);
	}
	
	public HMSubMenu(Icon icon) {
		build(null, icon);
	}
	
	public HMSubMenu(String caption, Icon icon) {
		build(caption, icon);
	}
	
	private void build(String caption, Icon icon) {
		button = new HMButton(caption, icon);
		button.withOtherIcon(VaadinSession.getCurrent().getAttribute(MenuConfig.class).getSubMenuIcon());
		button.addClickListener(e -> toggle());
		button.getClassNames().add(button.getClass().getSimpleName());
		button.getClassNames().add(Styles.leftMenuItem);
		content.setMargin(false);
		content.setPadding(false);
		content.setSpacing(false);
		super.setMargin(false);
		super.setPadding(false);
		super.setSpacing(false);
		super.getClassNames().add(Styles.subMenu);
		super.add(button, content);
	}
	
	public HMButton getButton() {
		return button;
	}
	
	public HMSubMenu withCaption(String caption) {
		button.withCaption(caption);
		return this;
	}
	
	public HMSubMenu withIcon(Icon icon) {
		button.withIcon(icon);
		return this;
	}
	
	public HMSubMenu withIcon(VaadinIcon icon) {
		return withIcon(icon.create());
	}
	
	public HMSubMenu toggle() {
		if (isOpen()) {
			close();
		} else {
			open();
		}
		return this;
	}
	
	public HMSubMenu open() {
		getClassNames().add(Styles.open);
		button.setActive(true);
		return this;
	}
	
	public HMSubMenu close() {
		getClassNames().remove(Styles.open);
		button.setActive(false);
		return this;
	}
	
	public boolean isOpen() {
		return getClassNames().contains(Styles.open);
	}
	
	@Override
	public <MenuComponent extends Component> MenuComponent add(MenuComponent c) {
		if (this.getClassNames().contains(Styles.leftMenuItem) && c instanceof HasStyle) {
			((HasStyle) c).getClassNames().add(Styles.leftMenuItem);
		}
		content.add(c);
		return c;
	}
	
	@Override
	public <MenuComponent extends Component> MenuComponent addAsFirst(MenuComponent c) {
		//content.addAsFirst(c);
		return c;
	}

	@Override
	public <MenuComponent extends Component> MenuComponent addAt(MenuComponent c, int index) {
		//content.addAt(c, index);
		return c;
	}
	
	@Override
	public <MenuComponent extends Component> HMSubMenu remove(MenuComponent c) {
		content.remove(c);
		return this;
	}

	@Override
	public int count() {
		return getList().size();
	}
	
	public List<MenuComponent<?>> getList() {
		List<MenuComponent<?>> menuComponentList = new ArrayList<MenuComponent<?>>();
		for (int i = 0; i < content.getComponentCount(); i++) {
			Component component = content.getComponentAt(i);
			if (component instanceof MenuComponent<?>) {
				menuComponentList.add((MenuComponent<?>) component);
			}
		}
		return menuComponentList;
	}
}