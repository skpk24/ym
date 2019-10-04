/*
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
 */
package org.ofbiz.webtools.artifactinfo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

//import javolution.util.FastMap;
import javolution.util.FastSet;
import java.util.HashMap;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entityext.eca.EntityEcaRule;
import org.ofbiz.entityext.eca.EntityEcaUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.eca.ServiceEcaRule;
import org.ofbiz.service.eca.ServiceEcaUtil;
import org.ofbiz.webapp.control.ConfigXMLReader;
import org.ofbiz.webapp.control.ConfigXMLReader.ControllerConfig;
import org.ofbiz.widget.form.FormFactory;
import org.ofbiz.widget.form.ModelForm;
import org.ofbiz.widget.screen.ModelScreen;
import org.ofbiz.widget.screen.ScreenFactory;
import org.xml.sax.SAXException;

/**
 *
 */
public class ArtifactInfoFactory {

    public static final String module = ArtifactInfoFactory.class.getName();

    protected static UtilCache<String, ArtifactInfoFactory> artifactInfoFactoryCache = UtilCache.createUtilCache("ArtifactInfoFactory");

    public static final String EntityInfoTypeId = "entity";
    public static final String ServiceInfoTypeId = "service";
    public static final String ServiceEcaInfoTypeId = "serviceEca";
    public static final String FormWidgetInfoTypeId = "form";
    public static final String ScreenWidgetInfoTypeId = "screen";
    public static final String ControllerRequestInfoTypeId = "request";
    public static final String ControllerViewInfoTypeId = "view";

    protected String delegatorName;
    protected ModelReader entityModelReader;
    protected DispatchContext dispatchContext;
    protected Map<String, Map<String, List<EntityEcaRule>>> entityEcaCache;
    protected Map<String, Map<String, List<ServiceEcaRule>>> serviceEcaCache;

    public Map<String, EntityArtifactInfo> allEntityInfos =new HashMap<String, EntityArtifactInfo>();// FastMap.newInstance();
    public Map<String, ServiceArtifactInfo> allServiceInfos = new HashMap<String, ServiceArtifactInfo>();//FastMap.newInstance();
    public Map<ServiceEcaRule, ServiceEcaArtifactInfo> allServiceEcaInfos =new HashMap<ServiceEcaRule, ServiceEcaArtifactInfo>(); //FastMap.newInstance();
    public Map<String, FormWidgetArtifactInfo> allFormInfos = new HashMap<String, FormWidgetArtifactInfo>();//FastMap.newInstance();
    public Map<String, ScreenWidgetArtifactInfo> allScreenInfos = new HashMap<String, ScreenWidgetArtifactInfo>();//FastMap.newInstance();
    public Map<String, ControllerRequestArtifactInfo> allControllerRequestInfos = new HashMap<String, ControllerRequestArtifactInfo>();//FastMap.newInstance();
    public Map<String, ControllerViewArtifactInfo> allControllerViewInfos = new HashMap<String, ControllerViewArtifactInfo>();//FastMap.newInstance();

    // reverse-associative caches for walking backward in the diagram
    public Map<String, Set<ServiceEcaArtifactInfo>> allServiceEcaInfosReferringToServiceName = new HashMap<String, Set<ServiceEcaArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ServiceArtifactInfo>> allServiceInfosReferringToServiceName = new HashMap<String, Set<ServiceArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosReferringToServiceName = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosBasedOnServiceName = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ScreenWidgetArtifactInfo>> allScreenInfosReferringToServiceName =new HashMap<String, Set<ScreenWidgetArtifactInfo>>();// FastMap.newInstance();
    public Map<String, Set<ControllerRequestArtifactInfo>> allRequestInfosReferringToServiceName = new HashMap<String, Set<ControllerRequestArtifactInfo>>();//FastMap.newInstance();

    public Map<String, Set<ServiceArtifactInfo>> allServiceInfosReferringToEntityName = new HashMap<String, Set<ServiceArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosReferringToEntityName = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ScreenWidgetArtifactInfo>> allScreenInfosReferringToEntityName =new HashMap<String, Set<ScreenWidgetArtifactInfo>>();// FastMap.newInstance();

    public Map<ServiceEcaRule, Set<ServiceArtifactInfo>> allServiceInfosReferringToServiceEcaRule = new HashMap<ServiceEcaRule, Set<ServiceArtifactInfo>>();//FastMap.newInstance();

    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosExtendingForm = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ScreenWidgetArtifactInfo>> allScreenInfosReferringToForm = new HashMap<String, Set<ScreenWidgetArtifactInfo>>();//FastMap.newInstance();

    public Map<String, Set<ScreenWidgetArtifactInfo>> allScreenInfosReferringToScreen = new HashMap<String, Set<ScreenWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ControllerViewArtifactInfo>> allViewInfosReferringToScreen = new HashMap<String, Set<ControllerViewArtifactInfo>>();//FastMap.newInstance();

    public Map<String, Set<ControllerRequestArtifactInfo>> allRequestInfosReferringToView = new HashMap<String, Set<ControllerRequestArtifactInfo>>();//FastMap.newInstance();

    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosTargetingRequest = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<FormWidgetArtifactInfo>> allFormInfosReferringToRequest = new HashMap<String, Set<FormWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ScreenWidgetArtifactInfo>> allScreenInfosReferringToRequest = new HashMap<String, Set<ScreenWidgetArtifactInfo>>();//FastMap.newInstance();
    public Map<String, Set<ControllerRequestArtifactInfo>> allRequestInfosReferringToRequest =new HashMap<String, Set<ControllerRequestArtifactInfo>>();// FastMap.newInstance();

    public static ArtifactInfoFactory getArtifactInfoFactory(String delegatorName) throws GeneralException {
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = "default";
        }

        ArtifactInfoFactory aif = artifactInfoFactoryCache.get(delegatorName);
        if (aif == null) {
            aif = new ArtifactInfoFactory(delegatorName);
            artifactInfoFactoryCache.put(delegatorName, aif);
        }
        return aif;
    }

    protected ArtifactInfoFactory(String delegatorName) throws GeneralException {
        this.delegatorName = delegatorName;
        this.entityModelReader = ModelReader.getModelReader(delegatorName);
        this.dispatchContext = new DispatchContext("ArtifactInfoDispCtx", null, this.getClass().getClassLoader(), null);
        this.entityEcaCache = EntityEcaUtil.getEntityEcaCache(EntityEcaUtil.getEntityEcaReaderName(delegatorName));
        this.serviceEcaCache = ServiceEcaUtil.ecaCache;

        this.prepareAll();
    }

    public void prepareAll() throws GeneralException {
        Set<String> entityNames = this.getEntityModelReader().getEntityNames();
        for (String entityName: entityNames) {
            this.getEntityArtifactInfo(entityName);
        }

        Set<String> serviceNames = this.getDispatchContext().getAllServiceNames();
        for (String serviceName: serviceNames) {
            this.getServiceArtifactInfo(serviceName);
        }

        // how to get all Service ECAs to prepare? don't worry about it, will be populated from service load, ie all ECAs for each service

        Collection<ComponentConfig> componentConfigs = ComponentConfig.getAllComponents();
        for (ComponentConfig componentConfig: componentConfigs) {
            String componentName = componentConfig.getGlobalName();
            String rootComponentPath = componentConfig.getRootLocation();
            List<File> screenFiles;
            List<File> formFiles;
            List<File> controllerFiles;
            try {
                screenFiles = FileUtil.findXmlFiles(rootComponentPath, null, "screens", "widget-screen.xsd");
                formFiles = FileUtil.findXmlFiles(rootComponentPath, null, "forms", "widget-form.xsd");
                controllerFiles = FileUtil.findXmlFiles(rootComponentPath, null, "site-conf", "site-conf.xsd");
            } catch (IOException ioe) {
                throw new GeneralException(ioe.getMessage());
            }
            for (File screenFile: screenFiles) {
                String screenFilePath = screenFile.getAbsolutePath();
                screenFilePath = screenFilePath.replace('\\', '/');
                String screenFileRelativePath = screenFilePath.substring(rootComponentPath.length());
                String screenLocation = "component://" + componentName + "/" + screenFileRelativePath;
                Map<String, ModelScreen> modelScreenMap = null;
                try {
                    modelScreenMap = ScreenFactory.getScreensFromLocation(screenLocation);
                } catch (Exception exc) {
                    throw new GeneralException(exc.toString(), exc);
                }
                for (String screenName : modelScreenMap.keySet()) {
                    this.getScreenWidgetArtifactInfo(screenName, screenLocation);
                }
            }
            for (File formFile: formFiles) {
                String formFilePath = formFile.getAbsolutePath();
                formFilePath = formFilePath.replace('\\', '/');
                String formFileRelativePath = formFilePath.substring(rootComponentPath.length());
                String formLocation = "component://" + componentName + "/" + formFileRelativePath;
                Map<String, ModelForm> modelFormMap = null;
                try {
                    modelFormMap = FormFactory.getFormsFromLocation(formLocation, this.getEntityModelReader(), this.getDispatchContext());
                } catch (Exception exc) {
                    throw new GeneralException(exc.toString(), exc);
                }
                for (String formName : modelFormMap.keySet()) {
                    this.getFormWidgetArtifactInfo(formName, formLocation);
                }
            }
            for (File controllerFile: controllerFiles) {
                URL controllerUrl = null;
                try {
                    controllerUrl = controllerFile.toURI().toURL();
                } catch (MalformedURLException mue) {
                    throw new GeneralException(mue.getMessage());
                }
                ControllerConfig cc = ConfigXMLReader.getControllerConfig(controllerUrl);
                for (String requestUri: cc.getRequestMapMap().keySet()) {
                    try {
                        this.getControllerRequestArtifactInfo(controllerUrl, requestUri);
                    } catch (GeneralException e) {
                        Debug.logWarning(e.getMessage(), module);
                    }
                }
                for (String viewUri: cc.getViewMapMap().keySet()) {
                    try {
                        this.getControllerViewArtifactInfo(controllerUrl, viewUri);
                    } catch (GeneralException e) {
                        Debug.logWarning(e.getMessage(), module);
                    }
                }
            }
        }
    }

    public ModelReader getEntityModelReader() {
        return this.entityModelReader;
    }

    public DispatchContext getDispatchContext() {
        return this.dispatchContext;
    }

    public ModelEntity getModelEntity(String entityName) throws GenericEntityException {
        return this.getEntityModelReader().getModelEntity(entityName);
    }

    public ModelService getModelService(String serviceName) throws GenericServiceException {
        return this.getDispatchContext().getModelService(serviceName);
    }

    public ModelForm getModelForm(String formNameAndLocation) throws ParserConfigurationException, SAXException, IOException {
        return getModelForm(formNameAndLocation.substring(formNameAndLocation.indexOf("#") + 1), formNameAndLocation.substring(0, formNameAndLocation.indexOf("#")));
    }
    public ModelForm getModelForm(String formName, String formLocation) throws ParserConfigurationException, SAXException, IOException {
        return FormFactory.getFormFromLocation(formLocation, formName, this.entityModelReader, this.dispatchContext);
    }

    public ModelScreen getModelScreen(String screenName, String screenLocation) throws ParserConfigurationException, SAXException, IOException {
        return ScreenFactory.getScreenFromLocation(screenLocation, screenName);
    }

    public ConfigXMLReader.RequestMap getControllerRequestMap(URL controllerXmlUrl, String requestUri) {
        return ConfigXMLReader.getControllerConfig(controllerXmlUrl).getRequestMapMap().get(requestUri);
    }

    public ConfigXMLReader.ViewMap getControllerViewMap(URL controllerXmlUrl, String viewUri) {
        ControllerConfig cc = ConfigXMLReader.getControllerConfig(controllerXmlUrl);
        return cc.getViewMapMap().get(viewUri);
    }

    public EntityArtifactInfo getEntityArtifactInfo(String entityName) throws GeneralException {
        EntityArtifactInfo curInfo = this.allEntityInfos.get(entityName);
        if (curInfo == null) {
            curInfo = new EntityArtifactInfo(entityName, this);
            this.allEntityInfos.put(entityName, curInfo);
            curInfo.populateAll();
        }
        return curInfo;
    }

    public ServiceArtifactInfo getServiceArtifactInfo(String serviceName) throws GeneralException {
        ServiceArtifactInfo curInfo = this.allServiceInfos.get(serviceName);
        if (curInfo == null) {
            curInfo = new ServiceArtifactInfo(serviceName, this);
            this.allServiceInfos.put(serviceName, curInfo);
            curInfo.populateAll();
        }
        return curInfo;
    }

    public ServiceEcaArtifactInfo getServiceEcaArtifactInfo(ServiceEcaRule ecaRule) throws GeneralException {
        ServiceEcaArtifactInfo curInfo = this.allServiceEcaInfos.get(ecaRule);
        if (curInfo == null) {
            curInfo = new ServiceEcaArtifactInfo(ecaRule, this);
            this.allServiceEcaInfos.put(ecaRule, curInfo);
            curInfo.populateAll();
        }
        return curInfo;
    }

    public FormWidgetArtifactInfo getFormWidgetArtifactInfo(String formNameAndLocation) throws GeneralException {
        return getFormWidgetArtifactInfo(formNameAndLocation.substring(formNameAndLocation.indexOf("#") + 1), formNameAndLocation.substring(0, formNameAndLocation.indexOf("#")));
    }
    public FormWidgetArtifactInfo getFormWidgetArtifactInfo(String formName, String formLocation) throws GeneralException {
        FormWidgetArtifactInfo curInfo = this.allFormInfos.get(formLocation + "#" + formName);
        if (curInfo == null) {
            curInfo = new FormWidgetArtifactInfo(formName, formLocation, this);
            this.allFormInfos.put(curInfo.getUniqueId(), curInfo);
            curInfo.populateAll();
        }
        return curInfo;
    }

    public ScreenWidgetArtifactInfo getScreenWidgetArtifactInfo(String screenName, String screenLocation) throws GeneralException {
        ScreenWidgetArtifactInfo curInfo = this.allScreenInfos.get(screenLocation + "#" + screenName);
        if (curInfo == null) {
            try {
                curInfo = new ScreenWidgetArtifactInfo(screenName, screenLocation, this);
                this.allScreenInfos.put(curInfo.getUniqueId(), curInfo);
                curInfo.populateAll();
            } catch (GeneralException e) {
                Debug.logWarning("Error loading screen [" + screenName + "] from resource [" + screenLocation + "]: " + e.toString(), module);
                return null;
            }
        }
        return curInfo;
    }

    public ControllerRequestArtifactInfo getControllerRequestArtifactInfo(URL controllerXmlUrl, String requestUri) throws GeneralException {
        ControllerRequestArtifactInfo curInfo = this.allControllerRequestInfos.get(controllerXmlUrl.toExternalForm() + "#" + requestUri);
        if (curInfo == null) {
            curInfo = new ControllerRequestArtifactInfo(controllerXmlUrl, requestUri, this);
            this.allControllerRequestInfos.put(curInfo.getUniqueId(), curInfo);
            curInfo.populateAll();
        }
        return curInfo;
    }

    public ControllerViewArtifactInfo getControllerViewArtifactInfo(URL controllerXmlUrl, String viewUri) throws GeneralException {
        ControllerViewArtifactInfo curInfo = this.allControllerViewInfos.get(controllerXmlUrl.toExternalForm() + "#" + viewUri);
        if (curInfo == null) {
            curInfo = new ControllerViewArtifactInfo(controllerXmlUrl, viewUri, this);
            this.allControllerViewInfos.put(curInfo.getUniqueId(), curInfo);
        }
        return curInfo;
    }

    public ArtifactInfoBase getArtifactInfoByUniqueIdAndType(String uniqueId, String type) {
        if (uniqueId.contains("#")) {
            int poundIndex = uniqueId.indexOf('#');
            return getArtifactInfoByNameAndType(uniqueId.substring(poundIndex+1), uniqueId.substring(0, poundIndex), type);
        } else {
            return getArtifactInfoByNameAndType(uniqueId, null, type);
        }
    }

    public ArtifactInfoBase getArtifactInfoByNameAndType(String artifactName, String artifactLocation, String type) {
        try {
            if ("entity".equals(type)) {
                return this.getEntityArtifactInfo(artifactName);
            } else if ("service".equals(type)) {
                return this.getServiceArtifactInfo(artifactName);
            } else if ("form".equals(type)) {
                return this.getFormWidgetArtifactInfo(artifactName, artifactLocation);
            } else if ("screen".equals(type)) {
                return this.getScreenWidgetArtifactInfo(artifactName, artifactLocation);
            } else if ("request".equals(type)) {
                return this.getControllerRequestArtifactInfo(new URL(artifactLocation), artifactName);
            } else if ("view".equals(type)) {
                return this.getControllerViewArtifactInfo(new URL(artifactLocation), artifactName);
            }
        } catch (GeneralException e) {
            Debug.logError(e, "Error getting artifact info: " + e.toString(), module);
        } catch (MalformedURLException e) {
            Debug.logError(e, "Error getting artifact info: " + e.toString(), module);
        }
        return null;
    }

    public Set<ArtifactInfoBase> getAllArtifactInfosByNamePartial(String artifactNamePartial, String type) {
        Set<ArtifactInfoBase> aiBaseSet = FastSet.newInstance();

        if (UtilValidate.isEmpty(artifactNamePartial)) {
            return aiBaseSet;
        }

        if (UtilValidate.isEmpty(type) || "entity".equals(type)) {
            for (Map.Entry<String, EntityArtifactInfo> curEntry: allEntityInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }
        if (UtilValidate.isEmpty(type) || "service".equals(type)) {
            for (Map.Entry<String, ServiceArtifactInfo> curEntry: allServiceInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }
        if (UtilValidate.isEmpty(type) || "form".equals(type)) {
            for (Map.Entry<String, FormWidgetArtifactInfo> curEntry: allFormInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }
        if (UtilValidate.isEmpty(type) || "screen".equals(type)) {
            for (Map.Entry<String, ScreenWidgetArtifactInfo> curEntry: allScreenInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }
        if (UtilValidate.isEmpty(type) || "request".equals(type)) {
            for (Map.Entry<String, ControllerRequestArtifactInfo> curEntry: allControllerRequestInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }
        if (UtilValidate.isEmpty(type) || "view".equals(type)) {
            for (Map.Entry<String, ControllerViewArtifactInfo> curEntry: allControllerViewInfos.entrySet()) {
                if (curEntry.getKey().toUpperCase().contains(artifactNamePartial.toUpperCase())) {
                    aiBaseSet.add(curEntry.getValue());
                }
            }
        }

        return aiBaseSet;
    }
}
