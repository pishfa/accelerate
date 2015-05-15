package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.portal.entity.Page;
import co.pishfa.accelerate.ui.controller.MenuBuilder;
import co.pishfa.accelerate.utility.StrUtils;
import org.primefaces.component.menubar.Menubar;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import java.io.Serializable;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class PrimeMenuBuilder implements MenuBuilder, Serializable {

    @Override
    public MenuModel getMenu(Page root) {
        DefaultMenuModel model = new DefaultMenuModel();
        /*DefaultMenuItem home = new DefaultMenuItem("","ui-icon-home");
        home.setTitle(getLocale().getMessages().get("home"));
        home.setOutcome("ac:home");
        model.addElement(home);*/
        buildMenuModel(model, null, root);
        return model;
    }

    private static Menubar FAKE_MENUBAR = new Menubar();
    public static class MenubarSubmenu extends DefaultSubMenu {

        public MenubarSubmenu(String label) {
            super(label);
        }

        @Override
        public Object getParent() {
            return FAKE_MENUBAR;
        }
    }

    protected void buildMenuModel(DefaultMenuModel model, DefaultSubMenu menu, Page root) {
        for(Page page : root.getChildren()) {
            if (page.isVisible()) {
                if(!page.hasAnyVisibleChild()) {
                    DefaultMenuItem item = new DefaultMenuItem();
                    item.setValue(page.getTitle());
                    item.setAjax(false);
                    item.setIcon(page.getIcon());
                    item.setTitle(page.getHelp());
                    if(StrUtils.isEmpty(page.getOutcome())) {
                        item.setUrl(page.getUrl());
                    } else {
                        item.setOutcome(page.getOutcome());
                    }
                    if(menu == null)
                        model.addElement(item);
                    else
                        menu.addElement(item);
                } else {
                    DefaultSubMenu sub = null;
                    if(menu == null) {
                        sub = new MenubarSubmenu(page.getTitle());
                        model.addElement(sub);
                    } else {
                        sub = new DefaultSubMenu(page.getTitle());
                        menu.addElement(sub);
                    }
                    sub.setIcon(page.getIcon());
                    buildMenuModel(model, sub, page);
                }
            }
        }
    }
}
