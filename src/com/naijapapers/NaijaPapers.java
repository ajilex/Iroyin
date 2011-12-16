
/* 
 * BrowserContentManagerDemo.java
 * 
 * Copyright © 1998-2008 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

/*TODO
 * ================
 * add menu
 * configure back button
 * 
 * 
 */


package com.naijapapers;

import javax.microedition.io.HttpConnection;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.browser.field.*;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.system.*;

/**
 * This sample application demonstrates how to render web content using
 * the BrowserContentManager class and the RenderingApplication interface.
 */

final public class NaijaPapers extends UiApplication implements RenderingApplication 
{    
	private static final String REFERER = "referer";   

	private BrowserContentManager _browserContentManager;
	private NaijaPapersScreen _mainScreen;
	private String _urlPart1 = "http://www.naijapapers.org/BlackBerry?page=";
	private int _page = 1;
	HorizontalFieldManager myFieldManager2;
	HorizontalFieldManager myFieldManager;
	private HorizontalFieldManager _newsMenu;
	private HorizontalFieldManager _articleMenu;
	ButtonField nextpagebutton = new ButtonField("Next", ButtonField.NEVER_DIRTY);
	ButtonField previouspagebutton = new ButtonField("Previous", ButtonField.NEVER_DIRTY);
	ButtonField fetchNewsButton = new ButtonField("Read Article", ButtonField.NEVER_DIRTY);
	ButtonField backButton = new ButtonField("Go Back", ButtonField.NEVER_DIRTY);
	//PleaseWaitPopupScreen popscreen = new PleaseWaitPopupScreen();
	String fetch_art = "";

	/***************************************************************************
	 * Main.
	 **************************************************************************/
	public static void main(String[] args) 
	{
		NaijaPapers app = new NaijaPapers();
		app.enterEventDispatcher();

	}

	/**
	 * Constructor.
	 */
	private NaijaPapers() 
	{               
		_browserContentManager = new BrowserContentManager( 0 );
		RenderingOptions renderingOptions = _browserContentManager.getRenderingSession().getRenderingOptions();

		// Turn off images in html.
		renderingOptions.setProperty(RenderingOptions.CORE_OPTIONS_GUID, RenderingOptions.SHOW_IMAGES_IN_HTML, false);

		_mainScreen = new NaijaPapersScreen();
		FieldChangeListener customListener = new FieldChangeListener() {   
			public void fieldChanged(Field field, int context) {    

				try {
					_page++;
					_fetch(_urlPart1+_page);
					
					myFieldManager2.replace(myFieldManager2.getField(2),getPage(_page));
					if(_page>1){
					//TODO	previouspagebutton.setEnabled(true);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				_mainScreen.invalidate();


			}   
		};
		FieldChangeListener _backcustomListener = new FieldChangeListener() {   
			public void fieldChanged(Field field, int context) {    
				if (_page>1){
					_page--;
					try {
						myFieldManager2.replace(myFieldManager2.getField(2),getPage(_page));
						_fetch(_urlPart1+_page);
						if(_page==1){
						//TODO	previouspagebutton.setEnabled(false);
						}

					} catch (Exception e) {
						
						e.printStackTrace();
					}
				
				}
				

				_mainScreen.invalidate();


			}   
		};
		FieldChangeListener gobackButtonListner = new FieldChangeListener() {   
			public void fieldChanged(Field field, int context) {
				try{

					_fetch(_urlPart1+_page);
					myFieldManager2.replace(myFieldManager2.getField(2),getPage(_page));
					_newsMenu.invalidate();
					_mainScreen.setStatus(_newsMenu);                             


				} catch (Exception e) {

					e.printStackTrace();
				}

				_mainScreen.invalidate();


			}   
		};
		FieldChangeListener articleButtonListener = new FieldChangeListener() {   
			public void fieldChanged(Field field, int context) {    

				try {
					//Dialog.alert(fetch_art);
					BrowserSession browserSession = Browser.getDefaultSession();

					// Then display the page using the browser session
					browserSession.displayPage(fetch_art);




				} catch (Exception e) {
					
					e.printStackTrace();
				}

				_mainScreen.invalidate();


			}   
		};   

		nextpagebutton.setChangeListener(customListener);
		previouspagebutton.setChangeListener(_backcustomListener);
		fetchNewsButton.setChangeListener(articleButtonListener);
		backButton.setChangeListener(gobackButtonListner);




		myFieldManager2 = new HorizontalFieldManager(Field.USE_ALL_WIDTH) {
			protected void paint(Graphics graphics) {
				graphics.setBackgroundColor(Color.BLACK);
				graphics.clear();
				super.paint(graphics);
			}};
			if(_page==1){
				
				//TODO previouspagebutton.setEnabled(false);
			}
			else if(_page>1){
				//TODO previouspagebutton.setEnabled(true);
			}
			
				myFieldManager2.add(previouspagebutton);
			
			
			
			myFieldManager2.add(nextpagebutton);
			myFieldManager2.add(getPage(_page));

			_newsMenu = myFieldManager2;
			myFieldManager = new HorizontalFieldManager(Field.USE_ALL_WIDTH) {
				protected void paint(Graphics graphics) {
					graphics.setBackgroundColor(Color.BLACK);
					graphics.clear();
					super.paint(graphics);
				}};
				myFieldManager.add(fetchNewsButton);
				myFieldManager.add(backButton);
				_articleMenu = myFieldManager;

				//_mainScreen.add(new LabelField("Label before the content", Field.FOCUSABLE));
				_mainScreen.setStatus(_newsMenu);      
				_mainScreen.add(_browserContentManager);

				//_mainScreen.add(new LabelField("Label after the content", Field.FOCUSABLE));                
				pushScreen(_mainScreen);
				//Dialog.alert(_urlPart1+_page);
				PrimaryResourceFetchThread thread = new PrimaryResourceFetchThread(_urlPart1+_page, null, null, null, this);
				PleaseWaitPopupScreen.showScreenAndWait(thread, "  Loading..");


	}


	private LabelField getPage(int _page2) {
		LabelField _pagetxt = new LabelField("Page "+_page2, LabelField.FIELD_LEFT ){
			protected void paint(Graphics graphics) {
				graphics.setColor(Color.WHITE);
				graphics.clear();
				super.paint(graphics);
			}};
			_pagetxt.setMargin(10, 1, 1, 20);
			return _pagetxt;

	}

	protected void _fetch(String string) {
		PrimaryResourceFetchThread thread = new PrimaryResourceFetchThread(string, null, null, null, this);
		PleaseWaitPopupScreen.showScreenAndWait(thread, "  Fetching Page..");

	}

	void processConnection(HttpConnection connection, Event e)
    {

        try
        {
            _browserContentManager.setContent(connection, this, e);
        }
        finally
        {
            SecondaryResourceFetchThread.doneAddingImages();
        }
    }

	/**
	 * @see net.rim.device.api.browser.RenderingApplication#eventOccurred(net.rim.device.api.browser.Event)
	 */
	public Object eventOccurred(Event event) 
	{
		int eventId = event.getUID();

		switch (eventId) 
		{
		case Event.EVENT_URL_REQUESTED : 
		{	

			UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent) event;
			if(urlRequestedEvent.getURL().startsWith("http://www.naijapapers.org/News/Extract")){
				_mainScreen.setStatus(_articleMenu);
			}
			/*
			 else if(urlRequestedEvent.getURL().startsWith("http://www.naijapapers.org")){
			 
				_mainScreen.setStatus(_newsMenu);
			}
			else {
				_mainScreen.setStatus(null);

			}
			*/
			_mainScreen.invalidate();
			
			String urlsuffix = "="+_page;
			if (!urlRequestedEvent.getURL().endsWith(urlsuffix) && urlRequestedEvent.getURL().startsWith("http://www.naijapapers.org") ){
				PrimaryResourceFetchThread thread = new PrimaryResourceFetchThread(urlRequestedEvent.getURL(), urlRequestedEvent
	                    .getHeaders(), urlRequestedEvent.getPostData(), event, this);
	            
				PleaseWaitPopupScreen.showScreenAndWait(thread, "  Extacting News..");
			}
            break;

		} 
		case Event.EVENT_BROWSER_CONTENT_CHANGED: 
		{                

			// Browser field title might have changed update title.
			BrowserContentChangedEvent browserContentChangedEvent = (BrowserContentChangedEvent) event; 

			if (browserContentChangedEvent.getSource() instanceof BrowserContent) 
			{ 
				BrowserContent browserField = (BrowserContent) browserContentChangedEvent.getSource(); 
				final String newTitle = browserField.getTitle();

				fetch_art = browserField.getURL();

				if(fetch_art.startsWith("http://www.naijapapers.org/Extract/?furl=")){
					String ext_url = fetch_art;
					int url_index = ext_url.indexOf("Extract/?furl=")+14;
					int url_lastindex = ext_url.indexOf("ajx1xja");
					ext_url = ext_url.substring(url_index, url_lastindex);
					ext_url = Utilities.replace(ext_url, "85fffs", "&", true);
					fetch_art = ext_url;


				}

				if (newTitle != null) 

				{
					Application.getApplication().invokeAndWait(new Runnable() 
					{
						public void run() 
						{	

							_mainScreen.setTitle(newTitle);


						}
					});
				}
			}                   

			break;                

		} 
		case Event.EVENT_REDIRECT : 
		{
			RedirectEvent e = (RedirectEvent) event;
			String referrer = e.getSourceURL();

			switch (e.getType()) 
			{
			case RedirectEvent.TYPE_SINGLE_FRAME_REDIRECT :
				// Show redirect message.
				Application.getApplication().invokeAndWait(new Runnable() 
				{
					public void run() 
					{
						Status.show("You are being redirected to a different page...");
					}
				});

				break;

			case RedirectEvent.TYPE_JAVASCRIPT :
				break;

			case RedirectEvent.TYPE_META :
				// MSIE and Mozilla don't send a Referer for META Refresh.
				referrer = null;     
				break;

			case RedirectEvent.TYPE_300_REDIRECT :
				// MSIE, Mozilla, and Opera all send the original
				// request's Referer as the Referer for the new
				// request.
				Object eventSource = e.getSource();
				if (eventSource instanceof HttpConnection) 
				{
					referrer = ((HttpConnection)eventSource).getRequestProperty(REFERER);
				}
				break;
			}

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setProperty(REFERER, referrer);
			PrimaryResourceFetchThread thread = new PrimaryResourceFetchThread(e.getLocation(), requestHeaders,null, event, this);
			PleaseWaitPopupScreen.showScreenAndWait(thread, "  Fetching News");
			break;

		} 
		case Event.EVENT_CLOSE :
			
			break;

		case Event.EVENT_SET_HEADER :        // No cache support.
		case Event.EVENT_SET_HTTP_COOKIE :   // No cookie support.
		case Event.EVENT_HISTORY :           // No history support.             
		case Event.EVENT_EXECUTING_SCRIPT :  // No progress bar is supported.
		case Event.EVENT_FULL_WINDOW :       // No full window support.
		case Event.EVENT_STOP :              // No stop loading support.

		default :
		}

		return null;
	}

	/**
	 * @see net.rim.device.api.browser.RenderingApplication#getAvailableHeight(net.rim.device.api.browser.BrowserContent)
	 */
	public int getAvailableHeight(BrowserContent browserField) 
	{
		// Field has full screen.
		return Display.getHeight();
	}


	/**
	 * @see net.rim.device.api.browser.RenderingApplication#getAvailableWidth(net.rim.device.api.browser.BrowserContent)
	 */
	public int getAvailableWidth(BrowserContent browserField) 
	{
		// Field has full screen.
		return Display.getWidth();
	}

	/**
	 * @see net.rim.device.api.browser.RenderingApplication#getHistoryPosition(net.rim.device.api.browser.BrowserContent)
	 */
	public int getHistoryPosition(BrowserContent browserField) 
	{
		// No history support.
		return 0;
	}


	/**
	 * @see net.rim.device.api.browser.RenderingApplication#getHTTPCookie(java.lang.String)
	 */
	public String getHTTPCookie(String url) 
	{
		// No cookie support.
		return null;
	}

	/**
	 * @see net.rim.device.api.browser.RenderingApplication#getResource(net.rim.device.api.browser.RequestedResource,
	 *      net.rim.device.api.browser.BrowserContent)
	 */
	public HttpConnection getResource( RequestedResource resource, BrowserContent referrer) 
	{
		if (resource == null) 
		{
			return null;
		}

		// Check if this is cache-only request.
		if (resource.isCacheOnly()) 
		{
			// No cache support.
			return null;
		}

		String url = resource.getUrl();

		if (url == null) 
		{
			return null;
		}

		// If referrer is null we must return the connection.
		if (referrer == null) 
		{
			HttpConnection connection = Utilities.makeConnection(resource.getUrl(), resource.getRequestHeaders(), null);
			return connection;

		} 
		else 
		{

			// If referrer is provided we can set up the connection on a separate thread.
			SecondaryResourceFetchThread.enqueue(resource, referrer);

		}

		return null;
	}

	/**
	 * @see net.rim.device.api.browser.RenderingApplication#invokeRunnable(java.lang.Runnable)
	 */
	public void invokeRunnable(Runnable runnable) 
	{         
		(new Thread(runnable)).start();
	} 

}

final class PrimaryResourceFetchThread extends Thread
{
    private NaijaPapers _application;
    private Event _event;
    private byte[] _postData;
    private HttpHeaders _requestHeaders;
    private String _url;

    /**
     * Constructor to create a PrimaryResourceFetchThread which fetches the 
     * resource from the specified url.
     * 
     * @param url The url to fetch the content from
     * @param requestHeaders The http request headers used to fetch the content
     * @param postData Data which is to be posted to the url
     * @param event The event triggering the connection
     * @param application The application requesting the connection
     */
    PrimaryResourceFetchThread(String url, HttpHeaders requestHeaders, byte[] postData, Event event, NaijaPapers application)
    {
        _url = url;
        _requestHeaders = requestHeaders;
        _postData = postData;
        _application = application;
        _event = event;
    }

    /**
     * Connects to the url associated with this object
     * 
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        HttpConnection connection = Utilities.makeConnection(_url, _requestHeaders, _postData);
        _application.processConnection(connection, _event);
    }
}
