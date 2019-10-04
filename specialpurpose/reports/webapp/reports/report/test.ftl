<table>
<tr>
<td>
<b>Parent Product Id</b>
</td>
<td/><td/><td/><td/><td/>
<td>
<b>Variant ProductIds</b>
</td>
</tr>

<#if wanted?has_content>

<#list wanted as myList>

<#assign vari = myList.variants?if_exists>
<#assign size = 0>
<#if vari?has_content>
<#assign size = vari.size()?int>
<#--@@@@@@@@@@@@@@@@@@@${size}${myList.parentProductId?if_exists}@@@@@@@@@@@@@@@@@@@@@@-->
</#if>
<#if (size>0) >
<tr>
<td>
${myList.parentProductId?if_exists}
</td><td/><td/><td/><td/>
<#assign variants = myList.variants?if_exists>

<#if variants?has_content>
<#list variants as variant>
<td>
${variant.variantProductId?if_exists}
</td>
<#assign features = variant.features?if_exists>
<#if variants?has_content>
<#list features as feature>
<td>
(${feature.productFeatureTypeId?if_exists},${feature.description?if_exists})
</td>
</#list>
</#if>

</#list>
</#if>
</tr>
<tr></tr><tr></tr><tr></tr>

</#if>


</#list>
</#if>

</table>