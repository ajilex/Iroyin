/*
 * Utilities.java
 *
 * Copyright © 1998-2010 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.naijapapers;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.StringUtilities;


/**
 * This class provides common functions required by the 
 * BrowserContentManagerDemo and BrowserFieldDemo. This class allows the
 * aforementioned classes to make a connection to a specified url.
 */
class Utilities 
{
    /**
     * Connect to a web resource
     * @param url The url of the resource
     * @param requestHeaders The request headers describing the connection to be made
     * @param postData The data to post to the web resource
     * @return The HttpConnection object representing the connection to the resource, null if no connection could be made
     */
    static HttpConnection makeConnection(String url, HttpHeaders requestHeaders, byte[] postData) 
    {
        HttpConnection conn = null;
        OutputStream out = null;
        String _connSuffix = updateConnectionSuffix();
        
        try 
        {
            conn = (HttpConnection) Connector.open(url+_connSuffix);           

            if (requestHeaders != null) 
            {
                // From
                // http://www.w3.org/Protocols/rfc2616/rfc2616-sec15.html#sec15.1.3
                //
                // Clients SHOULD NOT include a Referer header field in a (non-secure) HTTP 
                // request if the referring page was transferred with a secure protocol.
            	
            	
            	//long pagesize = conn.getLength();
            	String referer = requestHeaders.getPropertyValue("referer");
                boolean sendReferrer = true;
                
                if (referer != null && StringUtilities.startsWithIgnoreCase(referer, "https:") && !StringUtilities.startsWithIgnoreCase(url, "https:")) 
                {             
                    sendReferrer = false;
                }
                
                int size = requestHeaders.size();
                for (int i = 0; i < size;) 
                {                    
                    String header = requestHeaders.getPropertyKey(i);
                    
                    // Remove referer header if needed.
                    if ( !sendReferrer && header.equals("referer")) 
                    {
                        requestHeaders.removeProperty(i);
                        --size;
                        continue;
                    }
                    
                    String value = requestHeaders.getPropertyValue( i++ );
                    if (value != null) 
                    {
                        conn.setRequestProperty( header, value);
                    }
                }                
            }                          
            
            if (postData == null) 
            {
                conn.setRequestMethod(HttpConnection.GET);
            } 
            else 
            {
                conn.setRequestMethod(HttpConnection.POST);

                conn.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH, String.valueOf(postData.length));

                out = conn.openOutputStream();
                out.write(postData);

            }
        } 
        catch (IOException e1) 
        {
            errorDialog(e1.toString());
        } 
        finally 
        {
            if (out != null) 
            {
                try 
                {
                    out.close();
                } 
                catch (IOException e2) 
                {
                    errorDialog("OutputStream#close() threw " + e2.toString());
                }
            }
        }    
        
        return conn;
    }
    
    /**
     * Presents a dialog to the user with a given message
     * @param message The text to display
     */
    public static void errorDialog(final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                Dialog.alert(message);
            } 
        });
    }
    static public String replace(String val, String fnd, String rpl, boolean igncas) {
        int                                 fl=(fnd==null ? 0 : fnd.length());

        if(fl>0 && val.length()>=fl) {
            StringBuffer sb=null;  // string buffer
            int xp=0;  // index of previous fnd

            for(int xa=0,mi=(val.length()-fl); xa<=mi; xa++) {
                if(val.regionMatches(igncas,xa,fnd,0,fl)) {
                    if(xa>xp) { sb=append(sb,val.substring(xp,xa)); }  // substring uses private construct which does not dup char[]
                    sb=append(sb,rpl);
                    xp=(xa+fl);
                    xa=(xp-1);   // -1 to account for loop xa++;
                    }
                }

            if(sb!=null) {
                if(xp<val.length()) { sb.append(val.substring(xp,val.length())); }  // substring uses private construct which does not dup char[]
                return sb.toString();
                }
            }
        return val;
        }
    static private StringBuffer append(StringBuffer sb, String txt) {
        if(sb==null) { sb=new StringBuffer(txt.length()); }
        sb.append(txt);
        return sb;
        }
    
    static String updateConnectionSuffix()
    {
    	String connSuffix = null;
    if (DeviceInfo.isSimulator()) {
        connSuffix = ";deviceside=true";
    } else
    if ( (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) &&
          RadioInfo.areWAFsSupported(RadioInfo.WAF_WLAN)) {
        connSuffix=";interface=wifi";
    } else {
        String uid = null;
        ServiceBook sb = ServiceBook.getSB();
        ServiceRecord[] records = sb.findRecordsByCid("WPTCP");
        for (int i = 0; i < records.length; i++) {
            if (records[i].isValid() && !records[i].isDisabled()) {
                if (records[i].getUid() != null &&
                    records[i].getUid().length() != 0) {
                    if ((records[i].getCid().toLowerCase().indexOf("wptcp") != -1) &&
                        (records[i].getUid().toLowerCase().indexOf("wifi") == -1) &&
                        (records[i].getUid().toLowerCase().indexOf("mms") == -1)   ) {
                        uid = records[i].getUid();
                        break;
                    }
                }
            }
        }
        if (uid != null) {
            // WAP2 Connection
             connSuffix = ";ConnectionUID="+uid;
        } 
        
        else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT)
        {
        	connSuffix = ";deviceside=true";
        } 

        //(BlackBerry Enterprise Server)
        else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_MDS) == CoverageInfo.COVERAGE_MDS)
        {

        	connSuffix = ";deviceside=false";
        } 
    }
    
    return connSuffix;
    }
    
    
    
}
