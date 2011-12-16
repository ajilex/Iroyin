package com.naijapapers;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class NaijaPapersScreen extends MainScreen {

	/**
	 * 
	 */
	public NaijaPapersScreen() {
		super(MainScreen.VERTICAL_SCROLLBAR);
		// TODO Auto-generated constructor stub
	}
	
	public boolean onMenu(int instance) {
        if (instance == Menu.INSTANCE_CONTEXT) {
            // Retrieve the main menu
            Menu mainMenu = this.getMenu(Menu.INSTANCE_DEFAULT);
            String _item = "Get Link";
            String _item1 = "Open Link";
            int numItems = mainMenu.getSize();
            for (int i = 0; i < numItems; i++) {
                MenuItem curItem = mainMenu.getItem(i);
                String name = curItem.toString();
                if (name.equals(_item)|| name.equals(_item1)) {
                    curItem.run();
                    break;
                }
            }
            return false;
        } else
            return super.onMenu(instance);
}

}
