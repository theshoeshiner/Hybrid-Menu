package kaesdingeling.hybridmenu.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import kaesdingeling.hybridmenu.HybridMenu;
import kaesdingeling.hybridmenu.components.HMButton;
import kaesdingeling.hybridmenu.components.HMLabel;
import kaesdingeling.hybridmenu.components.HMSubMenu;
import kaesdingeling.hybridmenu.components.LeftMenu;
import kaesdingeling.hybridmenu.data.interfaces.MenuComponent;
import kaesdingeling.hybridmenu.data.interfaces.ViewChangeManager;

public class DefaultViewChangeManager implements ViewChangeManager, Serializable {
	private static final long serialVersionUID = -8043573562772975566L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultViewChangeManager.class);

	
	
	@Override
	public List<MenuComponent<?>> init(HybridMenu hybridMenu) {
		if (hybridMenu.getBreadCrumbs() != null) {
			hybridMenu.getBreadCrumbs().clear();
		}
		return new ArrayList<MenuComponent<?>>();
	}
	
	public boolean manage(HybridMenu hybridMenu, MenuComponent<?> menuComponent, ViewChangeEvent event, List<MenuComponent<?>> menuContentList) {
		
		
		LOGGER.debug("manage: {} {}",menuComponent,menuComponent.getCaption());
		boolean foundActiveButton = false;
		if (menuComponent != null) {
			if (menuComponent instanceof HMButton) {
				LOGGER.debug("checking button: {}",((HMButton)menuComponent).getCaption());
				if (checkButton((HMButton) menuComponent, event)) {
					LOGGER.debug("found the button");
					//TODO if event has params
					
					
					//HMButton bc = HMButton.get().withCaption(menuComponent.getCaption());
					HMButton bc = ((HMButton) menuComponent).clone();
					if(!hybridMenu.getConfig().getBreadcrumbClickCurrent()) {
						//bc.withStyleName("clickable");
						bc.removeStyleName("clickable");
						bc.removeClickListener();
					}
					if(!hybridMenu.getConfig().getBreadcrumbShowIcon())bc.withIcon(null);
					
					/*else {
						bc.removeStyleName("clickable");
						bc.removeClickListener();
					}*/
					
					add(hybridMenu, bc, menuContentList);
					foundActiveButton = true;
				}
				
				//if its a button we should still checl underneath it
				/*List<MenuComponent<?>> cacheMenuContentList = new ArrayList<MenuComponent<?>>();
				for (MenuComponent<?> cacheMenuComponent : menuComponent.getList()) {
					LOGGER.debug("checking sub item: {}",cacheMenuComponent.getCaption());
					if (manage(hybridMenu, cacheMenuComponent, event, cacheMenuContentList)) {
						foundActiveButton = true;
					}
				}*/
				
				
			}
			if (!foundActiveButton && (menuComponent instanceof LeftMenu || menuComponent instanceof HMSubMenu || menuComponent instanceof HMButton)) {
				
				
				//LOGGER.debug("checking sub items");
				List<MenuComponent<?>> cacheMenuContentList = new ArrayList<MenuComponent<?>>();
				List<MenuComponent<?>> sub = menuComponent.getList();
				
				for (MenuComponent<?> cacheMenuComponent : sub) {
					LOGGER.debug("checking sub item: {}",cacheMenuComponent.getCaption());
					if (manage(hybridMenu, cacheMenuComponent, event, cacheMenuContentList)) {
						foundActiveButton = true;
					}
				}
				
				if (menuComponent instanceof HMButton && sub.size()>0) {
					HMButton hmSubMenu = (HMButton) menuComponent;
					LOGGER.debug("checking HMButton: {}",menuComponent.getCaption());
					if (foundActiveButton ) {
						
						HMButton breadCrumButton = hmSubMenu.clone();
						if(!hybridMenu.getConfig().getBreadcrumbShowIcon())breadCrumButton.withIcon(null);
						breadCrumButton.withStyleName("clickable");
						
						/*HMButton breadCrumButton = HMButton.get()
							.withCaption(hmSubMenu.getCaption())
							.withStyleName("clickable")
							.withClickListener(e -> ((HMButton) menuComponent).click());*/
						
						//breadCrumButton.setToolTip(hmSubMenu.getToolTip());
						//breadCrumButton.removeToolTip();
						add(hybridMenu, breadCrumButton, menuContentList);
					} 
				}
				
				if (menuComponent instanceof HMSubMenu) {
					HMSubMenu hmSubMenu = (HMSubMenu) menuComponent;
					LOGGER.debug("checking hmSubMenu: {}",menuComponent.getCaption());
					if (foundActiveButton || checkSubView(hmSubMenu, event)) {
						
						HMButton breadCrumButton = HMButton.get().withCaption(hmSubMenu.getCaption());
						breadCrumButton.setToolTip(hmSubMenu.getButton().getToolTip());
						breadCrumButton.removeToolTip();
						add(hybridMenu, breadCrumButton, menuContentList);
						hmSubMenu.open();
					} else {
						hmSubMenu.close();
					}
				}
				
				menuContentList.addAll(cacheMenuContentList);
			}
		}
		LOGGER.debug("menuContentList: {}",menuContentList);
		LOGGER.debug("manage done: {} {}",menuComponent,menuComponent.getCaption());
		return foundActiveButton;
	}
	
	public void add(HybridMenu hybridMenu, MenuComponent<?> menuComponent, List<MenuComponent<?>> menuContentList) {
		
		if (hybridMenu.getBreadCrumbs() != null && (hybridMenu.getBreadCrumbs().getRoot() != null || menuContentList.size() > 0)) {
			LOGGER.debug("adding breadcrumb sep");
			menuContentList.add(HMLabel.get().withIcon(hybridMenu.getConfig().getBreadcrumbSeperatorIcon()));
		}
		LOGGER.debug("adding breadcrumb {}",menuComponent.getCaption());
		menuContentList.add(menuComponent);
	}
	
	public boolean checkSubView(HMSubMenu subMenu, ViewChangeEvent event) {
		for (String view : subMenu.getdSubViewList()) {
			if (checkView(view, event)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean checkButton(HMButton button, ViewChangeEvent event) {
		boolean check = checkView(button.getNavigateTo(), event);
		
		button.setActive(check);
		
		return check;
	}
	
	public boolean checkView(String navigateTo, ViewChangeEvent event) {
		if (StringUtils.isNotEmpty(navigateTo)) {
			if (navigateTo.startsWith(event.getNewView().getClass().getSimpleName())) {
				if (navigateTo.equals(event.getViewName())) {
					return true;
				} else if (navigateTo.equals(event.getViewName() + "/" + event.getParameters())) {
					return true;
				}
			} else {
				if (navigateTo.equals(event.getViewName())) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void finish(HybridMenu hybridMenu, List<MenuComponent<?>> menuContentList) {
		LOGGER.debug("finish");
		LOGGER.debug("menuContentList: {}",menuContentList);
		if (hybridMenu.getBreadCrumbs() != null) {
			if (hybridMenu.getBreadCrumbs().getRoot() != null && menuContentList.size() == 2) {
				MenuComponent<?> menuContent = menuContentList.get(1);
				if (hybridMenu.getBreadCrumbs().getRoot().getCaption().equals(menuContent.getCaption())) {
					menuContentList.clear();
				}
			}
			
			for (MenuComponent<?> menuComponent : menuContentList) {
				LOGGER.debug("adding to breadcrumb: {}",menuComponent);
				hybridMenu.getBreadCrumbs().add(menuComponent);
			}
		}
	}
}