package kaesdingeling.hybridmenu.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import kaesdingeling.hybridmenu.data.interfaces.MenuComponent;

public class HMButton extends Button implements MenuComponent<HMButton> {
	private static final long serialVersionUID = -2388630513509376470L;
	
	private String toolTip = null;
	private String navigateTo = null;
	private String navigateParameters = null;
	private ClickListener clickListener = null;
	private Registration clickListenerRegistration;
	private List<MenuComponent<?>> subComponentList = new ArrayList<>();
	private VerticalLayout content = new VerticalLayout();
	private Boolean root;
	private Boolean overrideBreadcrumb = false;
	
	public static HMButton get() {
		return new HMButton("");
	}
	
	/**
	 * Only for the left menu
	 * 
	 * The caption is not displayed in the left menu
	 * 
	 * @param caption
	 */
	public HMButton(String caption) {
		build(caption, null, null);
	}
	
	public HMButton(Resource icon) {
		build(null, icon, null);
	}
	
	/**
	 * Only for the left menu
	 * 
	 * The caption is not displayed in the left menu
	 * 
	 * @param caption
	 */
	public HMButton(String caption, Resource icon) {
		build(caption, icon, null);
	}
	
	/**
	 * Only for the left menu
	 * 
	 * The caption is not displayed in the left menu
	 * 
	 * @param caption
	 */
	public HMButton(String caption, ClickListener clickListener) {
		build(caption, null, clickListener);
	}
	
	public HMButton(Resource icon, ClickListener clickListener) {
		build(null, icon, clickListener);
	}
	
	/**
	 * Only for the left menu
	 * 
	 * The caption is not displayed in the left menu
	 * 
	 * @param caption
	 */
	public HMButton(String caption, Resource icon, ClickListener clickListener) {
		build(caption, icon, clickListener);
	}
	
	private void build(String caption, Resource icon, ClickListener clickListener) {
		withCaption(caption);
		withIcon(icon);
		if (clickListener != null) {
			withClickListener(clickListener);
		}
	}
	
	public HMButton withStyleName(String style) {
		addStyleName(style);
		return this;
	}
	
	public String getToolTip() {
		return toolTip;
	}
	
	/**
	 * set value toolTip
	 */
	public HMButton setToolTip(String toolTip) {
		this.toolTip = toolTip;
		return this;
	}
	
	/**
	 * Only for the left menu
	 * 
	 * The caption is not displayed in the left menu
	 * 
	 * @param caption
	 */
	public HMButton withCaption(String caption) {
		super.setCaption(caption);
		removeToolTip();
		updateToolTip();
		return this;
	}
	
	public HMButton withIcon(Resource icon) {
		super.setIcon(icon);
		return this;
	}
	
	public HMButton asRoot(boolean r) {
		this.root = r;
		return this;
	}
	
	public HMButton withOverrideBreadcrumb(Boolean r) {
		this.overrideBreadcrumb =  r;
		return this;
	}
	
	public HMButton withClickListener(ClickListener clickListener) {
		removeClickListener();
		clickListenerRegistration = super.addClickListener(clickListener);
		this.clickListener = clickListener;
		return this;
	}
	
	public HMButton withDescription(String description) {
		super.setDescription(description);
		return this;
	}
	
	public HMButton withNavigateTo(String viewName) {
		return withNavigateTo(viewName,null);
	}
	
	public HMButton withNavigateTo(String viewName,String viewParameters) {
		navigateTo = viewName;
		navigateParameters = viewParameters;
		removeClickListener();
		clickListenerRegistration = super.addClickListener(e -> {
			UI.getCurrent().getNavigator().navigateTo(navigateTo+(navigateParameters!=null?"/"+navigateParameters:""));
		});
		return this;
	}
	
	public void removeClickListener() {
		if(clickListenerRegistration != null) {
			clickListenerRegistration.remove();
			clickListenerRegistration = null;
		}
	}
	
	public <T extends View> HMButton withNavigateTo(Class<T> viewClass) {
		withNavigateTo(viewClass.getSimpleName(), null);
		return this;
	}
	
	/*public <T extends View> HMButton withNavigateTo(String viewName, Class<T> viewClass) {
		navigateTo = viewName;
		
		Navigator navigator = UI.getCurrent().getNavigator();
		return this.withClickListener(e -> {
			navigator.navigateTo(navigateTo);
		});
	}*/
	
	public HMButton updateToolTip() {
		String toolTip = "";
		String caption = getCaption();
		if (caption != null && !caption.isEmpty()) {
			toolTip += caption;
		}
		if (this.toolTip != null && !this.toolTip.isEmpty()) {
			toolTip += "<div class=\"toolTip\">" + this.toolTip + "</div>";
		}
		setCaption(toolTip);
		return this;
	}
	
	/**
	 * Only for the top menu and internal
	 * 
	 * @param toolTip
	 * @return
	 */
	public HMButton withToolTip(String toolTip) {
		setCaptionAsHtml(true);
		removeToolTip();
		if (toolTip == null || toolTip.isEmpty()) {
			this.toolTip = null;
		} else {
			this.toolTip = toolTip;
		}
		updateToolTip();
		return this;
	}
	
	/**
	 * Only for the top menu
	 * 
	 * @param toolTip
	 * @return
	 */
	public HMButton withToolTip(int toolTip) {
		setCaptionAsHtml(true);
		removeToolTip();
		if (toolTip == 0) {
			this.toolTip = null;
		} else {
			this.toolTip = String.valueOf(toolTip);
		}
		updateToolTip();
		return this;
	}
	
	public HMButton removeToolTip() {
		String caption = getCaption();
		if (toolTip != null && !toolTip.isEmpty() && caption != null && !caption.isEmpty()) {
			setCaption(caption.replaceAll("<div class=\"toolTip\">" + toolTip + "</div>", ""));
		}
		return this;
	}
	
	
	
	public String getNavigateParameters() {
		return navigateParameters;
	}

	public boolean isActive() {
		return getStyleName().contains("active");
	}
	
	public HMButton setActive(boolean active) {
		if (active != isActive()) {
			if (active) {
				addStyleName("active");
			} else {
				removeStyleName("active");
			}
		}
		return this;
	}
	
	public String getNavigateTo() {
		return navigateTo;
	}
	

	public Boolean getOverrideBreadcrumb() {
		return overrideBreadcrumb;
	}

	@Override
	public String getRootStyle() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public <C extends MenuComponent<?>> C add(C c) {
		content.addComponent(c);
		return c;
	}

	@Override
	public <C extends MenuComponent<?>> C addAsFirst(C c) {
		return null;
	}

	@Override
	public <C extends MenuComponent<?>> C addAt(C c, int index) {
		return null;
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public <C extends MenuComponent<?>> HMButton remove(C c) {
		return null;
	}

	@Override
	public List<MenuComponent<?>> getList() {
		List<MenuComponent<?>> menuComponentList = new ArrayList<MenuComponent<?>>();
		for (int i = 0; i < content.getComponentCount(); i++) {
			Component component = content.getComponent(i);
			if (component instanceof MenuComponent<?>) {
				menuComponentList.add((MenuComponent<?>) component);
			}
		}
		return menuComponentList;
	}
	
	public HMButton clone() {
		HMButton clone = HMButton.get();
		clone.withCaption(getCaption());
		clone.withDescription(getDescription());
		clone.withIcon(getIcon());
		clone.withStyleName(getStyleName());
		clone.withToolTip(getToolTip());
		clone.withOverrideBreadcrumb(overrideBreadcrumb);
		if(navigateTo != null) {
			clone.withNavigateTo(getNavigateTo(), getNavigateParameters());
		}
		else clone.withClickListener(clickListener);
		return clone;
	}
}