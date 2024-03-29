/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.webapp.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

//import javolution.util.FastMap;
import java.util.HashMap;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/** Utility methods needed to implement a WebDAV servlet. */
public class WebDavUtil {

    public static final String module = WebDavUtil.class.getName();
    public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    public static final String RFC1123_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static String formatDate(String formatString, Date date) {
        DateFormat df = new SimpleDateFormat(formatString);
        df.setTimeZone(GMT_TIMEZONE);
        return df.format(date);
    }

    public static Document getDocumentFromRequest(HttpServletRequest request) throws IOException, SAXException, ParserConfigurationException {
        Document document = null;
        InputStream is = null;
        try {
            is = request.getInputStream();
            document = UtilXml.readXmlDocument(is, false, "WebDAV request");
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return document;
    }

    /** Returns a <code>Map</code> containing user credentials found in the request. Returns
     * <code>null</code> if no user credentials were found. The returned <code>Map</code> is
     * intended to be used as parameters for the <code>userLogin</code> service. <p>The method
     * checks for the request parameters <code>USERNAME</code> and <code>PASSWORD</code>. If
     * those aren't found, then the request is checked for the HTTP Authorization header.
     * Currently, only Basic authorization is supported.</p>
     *
     * @param request The WebDAV request
     * @return A <code>Map</code> containing <code>login.username</code> and
     * <code>login.password</code> elements.
     */
    public static Map<String, Object> getCredentialsFromRequest(HttpServletRequest request) {
        String username = request.getParameter("USERNAME");
        String password = request.getParameter("PASSWORD");
        if (UtilValidate.isEmpty(username) || UtilValidate.isEmpty(password)) {
            String credentials = request.getHeader("Authorization");
            if (credentials != null && credentials.startsWith("Basic ")) {
                credentials = Base64.base64Decode(credentials.replace("Basic ", ""));
                Debug.logVerbose("Found HTTP Basic credentials", module);
                String[] parts = credentials.split(":");
                if (parts.length < 2) {
                    return null;
                }
                username = parts[0];
                password = parts[1];
            } else {
                return null;
            }
        }
        if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase"))) {
            username = username.toLowerCase();
        }
        if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))) {
            password = password.toLowerCase();
        }
        Map<String, Object> result =new HashMap<String, Object>();//FastMap.newInstance();
        result.put("login.username", username);
        result.put("login.password", password);
        return result;
    }
}
