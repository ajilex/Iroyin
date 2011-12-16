package com.naijapapers;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import java.lang.Throwable;
import java.lang.RuntimeException;


public class PleaseWaitPopupScreen extends PopupScreen {

    //statics ------------------------------------------------------------------

    private AnimatedGIFField _ourAnimation = null;
    private LabelField _ourLabelField = null;
    
    


    private PleaseWaitPopupScreen(String text) {
    	
        super(new HorizontalFieldManager(VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR));
        GIFEncodedImage ourAnimation = (GIFEncodedImage) GIFEncodedImage.getEncodedImageResource("cycle.agif");
        _ourAnimation = new AnimatedGIFField(ourAnimation, Field.FIELD_HCENTER);
        this.add(_ourAnimation);
        _ourLabelField = new LabelField(text, Field.FIELD_HCENTER);
        this.add(_ourLabelField);
    }
    public void paint(Graphics graphics) {        
        graphics.setBackgroundColor(Color.BLACK);
        graphics.clear();
        super.paint(graphics);
    }
    
    
    protected boolean keyChar(char c, int status, int time)
    {
    	switch(c) {
    		case Characters.ESCAPE:
    			System.exit(0);
    			break;
    		default:
    			return super.keyChar(c, status, time);
    	}
    	
    	return true;
    }
    

    
    public static void showScreenAndWait(final Runnable runThis, String text) {
    final PleaseWaitPopupScreen thisScreen = new PleaseWaitPopupScreen(text);
    Thread threadToRun = new Thread() {
        public void run() {
            // First, display this screen
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    UiApplication.getUiApplication().pushScreen(thisScreen);
                }
            });
            // Now run the code that must be executed in the Background
            try {
                runThis.run();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("Exception detected while waiting: " + t.toString());
            }
            // Now dismiss this screen
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    UiApplication.getUiApplication().popScreen(thisScreen);
                }
            });
        }
    };
    threadToRun.start();
}}