package kaesdingeling.hybridmenu.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import kaesdingeling.hybridmenu.data.DefaultViewChangeManager;
import kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import kaesdingeling.hybridmenu.utils.Utils;

public class BreadCrumbs extends HorizontalLayout implements MenuComponent<BreadCrumbs> {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(BreadCrumbs.class);
	private static final long serialVersionUID = 5825929144162024583L;
	
	private HMButton root = null;
	private List<MenuComponent<?>> crumbs = new ArrayList<MenuComponent<?>>();
	
	public BreadCrumbs() {
		setStyleName("breadcrumbs");
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(true);
	}
	
	public BreadCrumbs setRoot(HMButton root) {
		this.root = HMButton.get().withCaption(root.getCaption()).withStyleName("clickable").withClickListener(e -> root.click());
		add(this.root);
		return this;
	}
	
	public HMButton getRoot() {
		return this.root;
	}
	
	public BreadCrumbs clear() {
		for (MenuComponent<?> menuComponent : getList()) {
			if (!menuComponent.equals(root)) {
				remove(menuComponent);
			}
		}
		crumbs.clear();
		crumbs.add(root);
		return this;
	}

	public <C extends MenuComponent<?>> C add(C c) {
		addComponent(Utils.setDefaults(c));
		if(c instanceof MenuComponent<?> && !(c instanceof HMLabel)) crumbs.add(c);
		return c;
	}
	
	@Override
	public <C extends MenuComponent<?>> C addAsFirst(C c) {
		throw new NotImplementedException("Need to add Items to this in order");
		//addComponentAsFirst(Utils.setDefaults(c));
		//return c;
	}

	@Override
	public <C extends MenuComponent<?>> C addAt(C c, int index) {
		throw new NotImplementedException("Need to add Items to this in order");
		//addComponent(Utils.setDefaults(c), index);
		//return c;
	}

	@Override
	public int count() {
		return getList().size();
	}

	@Override
	public <C extends MenuComponent<?>> BreadCrumbs remove(C c) {
		removeComponent(c);
		return this;
	}

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

	
	public List<MenuComponent<?>> getCrumbs() {
		return crumbs;
	}
	
	public void setCaptionSuffix(int reverseIndex,String suffix) {
		MenuComponent<?> c = crumbs.get(crumbs.size()-(reverseIndex+1));
		c.setCaption(c.getCaption()+suffix);
	}
	
	public void setCaption(int reverseIndex,String suffix) {
		MenuComponent<?> c = crumbs.get(crumbs.size()-(reverseIndex+1));
		c.setCaption(suffix);
	}
	
	public void setNavigationParameters(int reverseIndex,String params) {
		MenuComponent<?> c = crumbs.get(crumbs.size()-(reverseIndex+1));
		if(c instanceof HMButton) {
			HMButton b = (HMButton) c;
			b.withNavigateTo(b.getNavigateTo(), params);
		}
	}
	
	public void setNavigationParametersAndCaptionSuffix(int reverseIndex,String caption,String params) {
		setNavigationParameters(reverseIndex, params);
		setCaptionSuffix(reverseIndex, caption);
	}
	
	public void setNavigationParametersAndCaption(int reverseIndex,String caption,String params) {
		setNavigationParameters(reverseIndex, params);
		setCaption(reverseIndex, caption);
	}
	
	public MenuComponent<?> getLast() {
		List<MenuComponent<?>> l = getList();
		return l.get(l.size()-1);
	}

	@Override
	public String getRootStyle() {
		return null;
	}
}