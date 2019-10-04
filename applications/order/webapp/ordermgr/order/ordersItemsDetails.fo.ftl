<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="8in" page-width="14.5in"
                    margin-top="0.1in" margin-bottom="1in" margin-left="0.2in" margin-right="0.5in">
                <fo:region-body margin-top="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="main">
            <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                <#if pId?exists && pId?has_content>
                    <fo:block>Order Items Details
                    </fo:block>
                    <fo:block>
                        <fo:table>
                            <fo:table-column column-width="50mm"/>
                            <fo:table-column column-width="50mm"/>
                            <fo:table-column column-width="50mm"/>
                            <fo:table-column column-width="50mm"/>
                            <fo:table-header>
                            	<fo:table-cell border="1pt solid" border-width=".2mm">
                                    <fo:block text-align="center" font-size="12pt">PRODUCT ID</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".2mm">
                                    <fo:block text-align="center" font-size="12pt">PRODUCT NAME</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid" border-width=".2mm">
                                    <fo:block text-align="center" font-size="12pt">TOTAL ORDERED</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="2pt solid" border-width=".2mm">
                                    <fo:block text-align="center" font-size="12pt">ATP/QOH</fo:block>
                                </fo:table-cell>
                            </fo:table-header>
                            <fo:table-body>
                            <#if pId.size()!= 0 >
			
								<#assign keys = pId.keySet()>
								<#if keys?has_content>
				 				<#list keys as key>
                                    <fo:table-row>
                                    	<fo:table-cell border="1pt solid" border-width=".2mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                ${key?if_exists}
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".2mm">
                                            <fo:block text-align="center" font-size="10pt">
                                            <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", key))>
                                                ${(product.internalName)?if_exists}
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".2mm">
                                            <fo:block text-align="center" font-size="10pt">
                                                ${pId.get(key)?if_exists}
                                            </fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell border="1pt solid" border-width=".2mm">
                                            <fo:block text-align="center" font-size="10pt">
                                            <#assign mainInven = dispatcher.runSync("getInventoryAvailableByFacility", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", key, "facilityId","WebStoreWarehouse"))/>
                                                ${mainInven.availableToPromiseTotal?if_exists} / ${mainInven.quantityOnHandTotal?if_exists}
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </#list>
                                </#if>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                <#else>
                    <fo:block text-align="center">NO RECORDS FOUND</fo:block>
                </#if></#if>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</#escape>