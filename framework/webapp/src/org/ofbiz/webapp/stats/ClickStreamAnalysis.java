package org.ofbiz.webapp.stats;


import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;

/**
 * <p>Counts server hits and tracks statistics for request, events and views
 * <p>Handles total stats since the server started and binned
 *  stats according to settings in the serverstats.properties file.
 */
public class ClickStreamAnalysis {
    // Debug module name
    public static final String module = ClickStreamAnalysis.class.getName();

    public static final int REQUEST = 1;
    public static final int EVENT = 2;
    public static final int VIEW = 3;
    public static final int ENTITY = 4;
    public static final int SERVICE = 5;
    public static final String[] typeNames = {"", "Request", "Event", "View", "Entity", "Service"};
    public static final String[] typeIds = {"", "REQUEST", "EVENT", "VIEW", "ENTITY", "SERVICE"};

    

    public static void saveBIHit(String webappName,HttpServletRequest request, long startTime, long runningTime, GenericValue userLogin,Delegator delegator) throws GenericEntityException {


            String visitId = delegator.getNextSeqId("ClickStreamAnalysis");
            GenericValue serverHit = delegator.makeValue("ClickStreamAnalysis");

            serverHit.set("visitId", visitId);
            serverHit.set("hitStartDateTime", new java.sql.Timestamp(startTime));
            serverHit.set("hitTypeId", "REQUEST");
            
            String fullRequestUrl = UtilHttp.getFullRequestUrl(request).toString();

            serverHit.set("requestUrl", fullRequestUrl.length() > 250 ? fullRequestUrl.substring(0, 250) : fullRequestUrl);
            String referrerUrl = request.getHeader("Referer") != null ? request.getHeader("Referer") : "";

            serverHit.set("referrerUrl", referrerUrl.length() > 250 ? referrerUrl.substring(0, 250) : referrerUrl);
            
            serverHit.set("requestUrl",fullRequestUrl);
            serverHit.set("referrerUrl",referrerUrl);
            
            
            
//            serverHit.set("visitId", "");
            if (userLogin != null) {
                serverHit.set("userLoginId", userLogin.get("userLoginId"));
                ModelEntity modelUserLogin = userLogin.getModelEntity();
                if (modelUserLogin.isField("partyId")) {
                    serverHit.set("partyId", userLogin.get("partyId"));
                }
            }
            serverHit.set("runningTimeMillis", Long.valueOf(runningTime));


//            serverHit.set("requestUrl",request.toString());

            // get localhost ip address and hostname to store
            try {
                InetAddress address = InetAddress.getLocalHost();

                if (address != null) {
                    serverHit.set("serverIpAddress", address.getHostAddress());
                    serverHit.set("serverHostName", address.getHostName());
                } else {
                    Debug.logError("Unable to get localhost internet address, was null", module);
                }
            } catch (java.net.UnknownHostException e) {
                Debug.logError("Unable to get localhost internet address: " + e.toString(), module);
            }

            serverHit.create();
        }
    }
