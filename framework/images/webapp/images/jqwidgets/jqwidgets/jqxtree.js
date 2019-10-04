/*
jQWidgets v2.0.0 (2012-April-06)
Copyright (c) 2011-2012 jQWidgets.
License: http://jqwidgets.com/license/
*/

(function(a){a.jqx.jqxWidget("jqxTree","",{});a.extend(a.jqx._jqxTree.prototype,{defineInstance:function(){this.items=new Array();this.width=null;this.height=null;this.easing="easeInOutCirc";this.animationShowDuration="fast";this.animationHideDuration="fast";this.treeElements=new Array();this.disabled=false;this.enableHover=true;this.enableKeyboardNavigation=true;this.toggleMode="dblclick";this.source=null;this.checkboxes=false;this.checkSize=13;this.hasThreeStates=false;this.selectedItem=null;this.touchMode="auto";this.events=["expand","collapse","select","initialized","added","removed","checkchange"]},createInstance:function(c){var b=this;this.propertyChangeMap.disabled=function(f,h,g,j){if(b.disabled){b.host.addClass(b.toThemeProperty("jqx-tree-disabled"))}else{b.host.removeClass(b.toThemeProperty("jqx-tree-disabled"))}};if(this.width!=null&&this.width.toString().indexOf("px")!=-1){this.host.width(this.width)}else{if(this.width!=undefined&&!isNaN(this.width)){this.host.width(this.width)}}if(this.height!=null&&this.height.toString().indexOf("px")!=-1){this.host.height(this.height)}else{if(this.height!=undefined&&!isNaN(this.height)){this.host.height(this.height)}}this.host.attr("tabIndex",1);if(this.disabled){this.host.addClass(this.toThemeProperty("jqx-tree-disabled"))}this.originalInnerHTML=this.element.innerHTML;this.createdTree=false;if(this.element.innerHTML.indexOf("UL")){var e=this.host.find("ul:first");if(e.length>0){this.createTree(e[0]);this.createdTree=true}}if(this.source!=null){var d=this.loadItems(this.source);this.element.innerHTML=d;var e=this.host.find("ul:first");if(e.length>0){this.createTree(e[0]);this.createdTree=true}}this._itemslength=this.items.length;if(this.createdTree==true){this._render();this._handleKeys()}this._updateCheckLayout(0)},checkItems:function(f,h){var e=this;if(f!=null){var d=0;var g=false;var b=0;var j=a(f.element).find("li");b=j.length;a.each(j,function(k){var l=e.itemMapping[this.id].item;if(l.checked!=false){if(l.checked==null){g=true}d++}});if(f!=h){if(d==b){this.checkItem(f.element,true)}else{if(d>0){this.checkItem(f.element,null)}else{this.checkItem(f.element,false)}}}else{var c=h.checked;var j=a(h.element).find("li");a.each(j,function(){var k=e.itemMapping[this.id].item;e.checkItem(this,c)})}this.checkItems(this._parentItem(f),h)}else{var c=h.checked;var j=a(h.element).find("li");a.each(j,function(){var k=e.itemMapping[this.id].item;e.checkItem(this,c)})}},_handleKeys:function(){var b=this;this.addHandler(this.host,"keydown",function(j){var g=j.keyCode;if(b.enableKeyboardNavigation){if(b.selectedItem!=null){var f=b.selectedItem.element;switch(g){case 32:if(b.checkboxes){b.fromKey=true;var h=a(b.selectedItem.checkBoxElement).jqxCheckBox("checked");b.checkItem(b.selectedItem.element,!h);if(b.hasThreeStates){b.checkItems(b.selectedItem,b.selectedItem)}}return false;case 33:var e=b._getItemsOnPage();var d=b.selectedItem;for(i=0;i<e;i++){d=b._prevVisibleItem(d)}if(d!=null){b.selectItem(d.element);b.ensureVisible(d.element)}else{b.selectItem(b._firstItem().element);b.ensureVisible(b._firstItem().element)}return false;case 34:var e=b._getItemsOnPage();var c=b.selectedItem;for(i=0;i<e;i++){c=b._nextVisibleItem(c)}if(c!=null){b.selectItem(c.element);b.ensureVisible(c.element)}else{b.selectItem(b._lastItem().element);b.ensureVisible(b._lastItem().element)}return false;case 37:if(b.selectedItem.hasItems){b.collapseItem(f)}return false;case 39:if(b.selectedItem.hasItems){b.expandItem(f)}return false;case 13:if(b.selectedItem.hasItems){if(b.selectedItem.isExpanded){b.collapseItem(f)}else{b.expandItem(f)}}return false;case 36:b.selectItem(b._firstItem().element);b.ensureVisible(b._firstItem().element);return false;case 35:b.selectItem(b._lastItem().element);b.ensureVisible(b._lastItem().element);return false;case 38:var d=b._prevVisibleItem(b.selectedItem);if(d!=null){b.selectItem(d.element);b.ensureVisible(d.element)}return false;case 40:var c=b._nextVisibleItem(b.selectedItem);if(c!=null){b.selectItem(c.element);b.ensureVisible(c.element)}return false}}}})},_firstItem:function(){var d=null;var c=this;var f=this.host.find("ul:first");var e=a(f).find("li");for(i=0;i<=e.length-1;i++){var b=e[i];d=this.itemMapping[b.id].item;if(c._isVisible(d)){return d}}return null},_lastItem:function(){var d=null;var c=this;var f=this.host.find("ul:first");var e=a(f).find("li");for(i=e.length-1;i>=0;i--){var b=e[i];d=this.itemMapping[b.id].item;if(c._isVisible(d)){return d}}return null},_parentItem:function(d){if(d==null||d==undefined){return null}var c=d.parentElement;var b=null;a.each(this.items,function(){if(this.element==c){b=this;return false}});return b},_nextVisibleItem:function(c){if(c==null||c==undefined){return null}var b=c;while(b!=null){b=b.nextItem;if(this._isVisible(b)&&!b.disabled){return b}}return null},_prevVisibleItem:function(c){if(c==null||c==undefined){return null}var b=c;while(b!=null){b=b.prevItem;if(this._isVisible(b)&&!b.disabled){return b}}return null},_isVisible:function(c){if(c==null||c==undefined){return false}if(!this._isElementVisible(c.element)){return false}var b=this._parentItem(c);if(b==null){return true}if(b!=null){if(!this._isElementVisible(b.element)){return false}if(b.isExpanded){while(b!=null){b=this._parentItem(b);if(b!=null&&!this._isElementVisible(b.element)){return false}if(b!=null&&!b.isExpanded){return false}}}else{return false}}return true},_getItemsOnPage:function(){var d=0;var c=this.panel.jqxPanel("getVScrollPosition");var b=parseInt(this.host.height());var f=0;var e=this._firstItem();if(parseInt(a(e.element).height())>0){while(f<=b){f+=parseInt(a(e.element).outerHeight());d++}}return d},_isElementVisible:function(b){if(b==null){return false}if(a(b).css("display")!="none"&&a(b).css("visibility")!="hidden"){return true}return false},refresh:function(){if(this.width!=null&&this.width.toString().indexOf("px")!=-1){this.host.width(this.width)}else{if(this.width!=undefined&&!isNaN(this.width)){this.host.width(this.width)}}if(this.height!=null&&this.height.toString().indexOf("px")!=-1){this.host.height(this.height)}else{if(this.height!=undefined&&!isNaN(this.height)){this.host.height(this.height)}}if(this.panel){this.panel.jqxPanel("width",this.host.width());this.panel.jqxPanel("height",this.height);this.panel.jqxPanel("_arrange")}},loadItems:function(c){if(c==null){return}var b=this;this.items=new Array();var d="<ul>";a.map(c,function(e){if(e==undefined){return null}d+=b._parseItem(e)});d+="</ul>";return d},_parseItem:function(l){var f="";if(l==undefined){return null}var j=l.label;if(!l.label&&l.html){j=l.html}var g=false;if(l.expanded!=undefined&&l.expanded){g=true}var e=false;if(l.locked!=undefined&&l.locked){e=true}var c=false;if(l.selected!=undefined&&l.selected){c=true}var d=false;if(l.disabled!=undefined&&l.disabled){d=true}var k=false;if(l.checked!=undefined&&l.checked){k=true}var h=l.icon;var b=l.iconsize;f+="<li";if(g){f+=' item-expanded="true" '}if(e){f+=' item-locked="true" '}if(d){f+=' item-disabled="true" '}if(c){f+=' item-selected="true" '}if(b){f+=' item-iconsize="'+l.iconsize+'" '}if(h!=null&&h!=undefined){f+=' item-icon="'+h+'" '}if(l.label&&!l.html){f+=' item-label="'+j+'" '}if(l.value!=null){f+=' item-value="'+l.value+'" '}if(l.checked!=undefined){f+=' item-checked="'+k+'" '}f+=">"+j;if(l.items){f+=this.loadItems(l.items)}f+="</li>";return f},ensureVisible:function(d){if(d==null||d==undefined){return}var c=this.panel.jqxPanel("getVScrollPosition");var e=this.panel.jqxPanel("getHScrollPosition");var b=parseInt(this.host.height());var f=a(d).position().top;if(f<=c||f>=b+c){this.panel.jqxPanel("scrollTo",e,f-b+a(d).outerHeight())}},addTo:function(c,b){if(c==undefined||c==null){return}var d=this;var f=new Array();if(!a.isArray(c)){f[0]=c}else{f=c}if(this.element.innerHTML.indexOf("UL")){var e=d.host.find("ul:first")}a.each(f,function(){var k=this;var j=d._parseItem(k);if(j.length>0){if(b==undefined&&b==null){var h=a(j);e.append(h);d._createItem(h[0])}else{b=a(b);var g=b.find("ul:first");var h=null;if(g.length==0){ulElement=a("<ul></ul>");a(b).append(ulElement);h=a(j);g=b.find("ul:first");var k=d.itemMapping[b[0].id].item;k.subtreeElement=g[0];k.hasItems=true;g.addClass(d.toThemeProperty("jqx-tree-dropdown"));g.append(h);h=g.find("li:first")}else{h=a(j);g.append(h)}d._createItem(h[0])}}});d._updateItemsNavigation();d._render();this._raiseEvent("4",{items:c})},removeItem:function(b){if(b==undefined||b==null){return}var c=this;var e=b.id;if(this.element.innerHTML.indexOf("UL")){var d=c.host.find("ul:first")}a.each(c.items,function(){var f=this;if(f.element.id==e){return false}});if(this.host.find("#"+b.id).length>0){a(b).remove()}c._updateItemsNavigation();c._render();c.selectedItem=null;this._raiseEvent("5")},disableItem:function(b){if(b==null){return false}var c=this;a.each(c.items,function(){var d=this;if(d.element==b){c.collapseItem(d.element);d.disabled=true;a(d.titleElement).removeClass(c.toThemeProperty("jqx-state-selected"));a(d.titleElement).removeClass(c.toThemeProperty("jqx-tree-item-selected"));a(d.titleElement).addClass(c.toThemeProperty("jqx-tree-item-disabled"));return false}})},checkItem:function(b,d){if(b==null){return false}var c=this;a.each(c.items,function(){var e=this;if(e.element==b){e.checked=d;a(e.checkBoxElement).jqxCheckBox({checked:d});return false}});this._raiseEvent("6",{element:b,checked:d})},enableItem:function(b){if(b==null){return false}var c=this;a.each(c.items,function(){var d=this;if(d.element==b){d.disabled=false;a(d.titleElement).removeClass(c.toThemeProperty("jqx-tree-item-disabled"));return false}})},enableAll:function(){var b=this;a.each(b.items,function(){var c=this;c.disabled=false;a(c.titleElement).removeClass(b.toThemeProperty("jqx-tree-item-disabled"))})},lockItem:function(b){if(b==null){return false}var c=this;a.each(c.items,function(){var d=this;if(d.element==b){d.locked=true;return false}})},unlockItem:function(b){if(b==null){return false}var c=this;a.each(c.items,function(){var d=this;if(d.element==b){d.locked=false;return false}})},getItems:function(){return this.items},getItem:function(b){if(b==null||b==undefined){return null}var c=this.itemMapping[b.id].item;return c},isExpanded:function(b){if(b==null||b==undefined){return false}var c=this.itemMapping[b.id].item;if(c!=null){return c.isExpanded}return false},isSelected:function(b){if(b==null||b==undefined){return false}var c=this.itemMapping[b.id].item;if(c!=null){return c==this.selectedItem}return false},selectItem:function(b){if(this.disabled){return}var c=this;if(b==null||b==undefined){if(c.selectedItem!=null){a(c.selectedItem.titleElement).removeClass(c.toThemeProperty("jqx-state-selected"));a(c.selectedItem.titleElement).removeClass(c.toThemeProperty("jqx-tree-item-selected"));c.selectedItem=null}return}if(this.selectedItem!=null&&this.selectedItem.element==b){return}a.each(c.items,function(){var d=this;if(!d.disabled){if(d.element==b){if(c.selectedItem==null||(c.selectedItem!=null&&c.selectedItem.titleElement!=d.titleElement)){if(c.selectedItem!=null){a(c.selectedItem.titleElement).removeClass(c.toThemeProperty("jqx-state-selected"));a(c.selectedItem.titleElement).removeClass(c.toThemeProperty("jqx-tree-item-selected"))}a(d.titleElement).addClass(c.toThemeProperty("jqx-state-selected"));a(d.titleElement).addClass(c.toThemeProperty("jqx-tree-item-selected"));c.selectedItem=d}}}});this._raiseEvent("2",{element:b})},collapseAll:function(){var c=this;var b=c.items;a.each(b,function(){var d=this;if(d.isExpanded==true){c._collapseItem(c,d)}})},expandAll:function(){var b=this;a.each(this.items,function(){var c=this;if(c.hasItems){b._expandItem(b,c)}})},collapseItem:function(b){if(b==null){return false}var c=this;a.each(this.items,function(){var d=this;if(d.isExpanded==true&&d.element==b){c._collapseItem(c,d);return false}});return true},expandItem:function(b){if(b==null){return false}var c=this;a.each(c.items,function(){var d=this;if(c.checkboxes){c._updateCheckItemLayout(this)}if(d.isExpanded==false&&d.element==b&&!d.disabled&&!d.locked){c._expandItem(c,d);if(d.parentElement){c.expandItem(d.parentElement)}}});return true},_getClosedSubtreeOffset:function(c){var b=a(c.subtreeElement);var e=-b.outerHeight();var d=-b.outerWidth();d=0;return{left:d,top:e}},_collapseItem:function(g,k,d,b){if(g==null||k==null){return false}if(k.disabled){return false}if(g.disabled){return false}if(g.locked){return false}var e=a(k.subtreeElement);var l=this._getClosedSubtreeOffset(k);var h=l.top;var c=l.left;$treeElement=a(k.element);var f=g.animationHideDelay;f=0;if(e.data("timer").show!=null){clearTimeout(e.data("timer").show);e.data("timer").show=null}var j=function(){k.isExpanded=false;if(g.checkboxes){var m=e.find(".chkbox");m.stop();m.css("opacity",1);e.find(".chkbox").animate({opacity:0},50)}e.slideUp(g.animationHideDuration,function(){k.isCollapsing=false;g._calculateWidth();var n=a(k.arrow);if(n.length>0){n.removeClass();n.addClass(g.toThemeProperty("jqx-tree-item-arrow-collapse"))}g._raiseEvent("1",{element:k.element})})};if(f>0){e.data("timer").hide=setTimeout(function(){j()},f)}else{j()}},getSubItems:function(j,h){if(j==null){return false}var g=this;var b=new Array();if(h!=null){a.extend(b,h)}var c=j;var f=this.treeElements[c];var e=a(f.subtreeElement);var d=e.find(".jqx-tree-item");a.each(d,function(){b[this.id]=g.treeElements[this.id];var k=g.getSubItems(this.id,b);a.extend(b,k)});return b},_anyOpenedTopLevelItems:function(){for(i=0;i<this.items.length;i++){if(this.items[i].level==0&&this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-selected"))!=-1){return true}}return false},_getOpenedTopLevelItems:function(){for(i=0;i<this.items.length;i++){if(this.items[i].level==0&&this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-selected"))!=-1){return this.items[i]}}return null},_getSiblings:function(c){var d=new Array();var b=0;for(i=0;i<this.items.length;i++){if(this.items[i]==c){continue}if(this.items[i].parentId==c.parentId&&this.items[i].hasItems){d[b++]=this.items[i]}}return d},_getOpenedOnLevelItems:function(b){var c=b.level;for(i=0;i<this.items.length;i++){if(this.items[i]==b){continue}if(this.items[i].level==c&&(this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-selected"))!=-1||this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-selected"))!=-1||this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-hover"))!=-1||this.items[i].element.className.indexOf(this.toThemeProperty("jqx-tree-item-hover"))!=-1)){return this.items[i]}}return null},_expandItem:function(g,k){if(g==null||k==null){return false}if(k.isExpanded){return false}if(k.locked){return false}if(k.disabled){return false}if(g.disabled){return false}var e=a(k.subtreeElement);if((e.data("timer"))!=null&&e.data("timer").hide!=null){clearTimeout(e.data("timer").hide)}var j=a(k.element);var h=0;var d=0;if(parseInt(e.css("top"))==h){k.isExpanded=true;return}var c=a(k.arrow);if(c.length>0){c.removeClass();c.addClass(g.toThemeProperty("jqx-tree-item-arrow-expand"))}if(g.checkboxes){var f=e.find(".chkbox");f.stop();f.css("opacity",0);f.animate({opacity:1},g.animationShowDuration)}e.slideDown(g.animationShowDuration,g.easing,function(){k.isExpanded=true;k.isExpanding=false;g._raiseEvent("0",{element:k.element});g._calculateWidth()});if(g.checkboxes){g._updateCheckItemLayout(k);if(k.subtreeElement){var b=a(k.subtreeElement).find("li");a.each(b,function(){var l=g.getItem(this);if(l!=null){g._updateCheckItemLayout(l)}})}}},_calculateWidth:function(){var d=this;var e=this.checkboxes?20:0;var c=0;a.each(this.items,function(){var f=a(this.element).height();if(f!=0){var g=this.titleElement.outerWidth()+20+e+(1+this.level)*25;c=Math.max(c,g)}});if(c>this.host.width()){var b=c-this.host.width();d.panel.jqxPanel({horizontalScrollBarMax:b})}else{d.panel.jqxPanel({horizontalScrollBarMax:null})}d.panel.jqxPanel("_arrange")},_initialize:function(e,b){var d=this;var c=0;this.host.removeClass(d.toThemeProperty("jqx-tree-vertical"));this.host.removeClass(d.toThemeProperty("jqx-tree"));this.host.addClass(d.toThemeProperty("jqx-widget"));this.host.addClass(d.toThemeProperty("jqx-tree"));this.host.addClass(d.toThemeProperty("jqx-tree-vertical"));a.each(this.items,function(){var j=this;$element=a(j.element);var g=null;var f=a(j.arrow);if(f.length>0){f.unbind("hover");f.remove()}g=a('<span style="height: 17px; border: none; background-color: transparent;" id="arrow'+$element[0].id+'"></span>');g.prependTo($element);g.css("float","left");if(!j.isExpanded){g.addClass(d.toThemeProperty("jqx-tree-item-arrow-collapse"))}else{g.addClass(d.toThemeProperty("jqx-tree-item-arrow-expand"))}var h=parseInt(a(this.titleElement).css("padding-top"));if(isNaN(h)){h=0}h=h*2;h+=2;var k=(h+a(this.titleElement).height())/2-17/2;if(a.browser.msie&&a.browser.version<9){g.css("margin-top","3px")}else{g.css("margin-top",parseInt(k)+"px")}$element.addClass(d.toThemeProperty("jqx-disableselect"));g.addClass(d.toThemeProperty("jqx-disableselect"));g.click(function(){if(!j.isExpanded){d._expandItem(d,j)}else{d._collapseItem(d,j)}return false});d.addHandler(g,"selectstart",function(){return false});d.addHandler(g,"mouseup",function(){return false});g.hover(function(){g.removeClass();if(j.isExpanded){g.addClass(d.toThemeProperty("jqx-tree-item-arrow-expand-hover"))}else{g.addClass(d.toThemeProperty("jqx-tree-item-arrow-collapse-hover"))}},function(){g.removeClass();if(j.isExpanded){g.addClass(d.toThemeProperty("jqx-tree-item-arrow-expand"))}else{g.addClass(d.toThemeProperty("jqx-tree-item-arrow-collapse"))}});j.hasItems=a(j.element).find("li").length>0;j.arrow=g[0];if(!j.hasItems){g.css("visibility","hidden")}$element.css("float","none")})},_getOffset:function(b){var f=a(window).scrollTop();var h=a(window).scrollLeft();var c=a.jqx.mobile.isSafariMobileBrowser();var g=a(b).offset();var e=g.top;var d=g.left;if(c!=null&&c){return{left:d-h,top:e-f}}else{return a(b).offset()}},_renderHover:function(c,e,b){var d=this;if(!b){a(e.titleElement).unbind("hover");a(e.titleElement).hover(function(){if(!e.disabled&&d.enableHover&&!d.disabled){a(e.titleElement).addClass(d.toThemeProperty("jqx-state-hover"));a(e.titleElement).addClass(d.toThemeProperty("jqx-tree-item-hover"))}},function(){if(!e.disabled&&d.enableHover&&!d.disabled){a(e.titleElement).removeClass(d.toThemeProperty("jqx-state-hover"));a(e.titleElement).removeClass(d.toThemeProperty("jqx-tree-item-hover"))}})}},_render:function(h,b){if(a.browser.msie&&a.browser.version<8){var f=this;a.each(this.items,function(){var m=a(this.element);var o=m.parent();var l=parseInt(this.titleElement.css("margin-left"))+this.titleElement[0].scrollWidth+13;m.css("min-width",l);var n=parseInt(o.css("min-width"));if(isNaN(n)){n=0}var k=m.css("min-width");if(n<parseInt(m.css("min-width"))){o.css("min-width",k)}this.titleElement[0].style.width=null})}var j=1000;var d=[5,5];var f=this;a.data(f.element,"animationHideDelay",f.animationHideDelay);var e=this.isTouchDevice();a.data(document.body,"treeel",this);this.host.css("visibility","visible");this._initialize();if(e&&this.toggleMode=="dblclick"){this.toggleMode="click"}a.each(this.items,function(){var m=this;var l=a(m.element);if(f.enableRoundedCorners){l.addClass(f.toThemeProperty("jqx-rc-all"))}f.removeHandler(a(m.checkBoxElement),"click");f.addHandler(a(m.checkBoxElement),"click",function(o){this.treeItem.checked=!this.treeItem.checked;f.checkItem(this.treeItem.element,this.treeItem.checked);if(f.hasThreeStates){f.checkItems(this.treeItem,this.treeItem)}return false});f.removeHandler(l,"mousedown");f.removeHandler(l,"mousedown");f.removeHandler(l,"mouseenter");f.removeHandler(l,"mouseleave");f.removeHandler(l,"mousedown");f.removeHandler(l,"mouseup");f.removeHandler(l,"selectstart");f._renderHover(l,m,e);var k=a(m.subtreeElement);if(k.length>0){var n=m.isExpanded?"block":"none";k.css({overflow:"hidden",display:n});k.data("timer",{})}f.removeHandler(a(m.titleElement),"dblclick");f.removeHandler(a(m.titleElement),"click");f.addHandler(a(m.titleElement),"selectstart",function(o){return false});if(a.browser.opera){f.removeHandler(a(m.titleElement),"mousedown");f.addHandler(a(m.titleElement),"mousedown",function(o){return false})}if(f.toggleMode!="click"){f.addHandler(a(m.titleElement),"click",function(o){f.selectItem(m.element);if(f.panel!=null){f.panel.jqxPanel({focused:true})}})}f.addHandler(a(m.titleElement),f.toggleMode,function(o){if(k.length>0){clearTimeout(k.data("timer").hide)}if(f.panel!=null){f.panel.jqxPanel({focused:true})}if(k!=null){}f.selectItem(m.element);if(m.isExpanding==undefined){m.isExpanding=false}if(m.isCollapsing==undefined){m.isCollapsing=false}f.panel.jqxPanel({autoUpdate:false});if(k.length>0){if(!m.isExpanded){if(false==m.isExpanding){m.isExpanding=true;f._expandItem(f,m)}}else{if(false==m.isCollapsing){m.isCollapsing=true;f._collapseItem(f,m,true)}}}f.panel.jqxPanel({autoUpdate:true});return false})});if(this.host.jqxPanel){this.host.find("ul:first").wrap('<div style="background-color: transparent; overflow: hidden; width: 100%; height: 100%;" id="panel'+this.element.id+'"></div>');var c=this.host.find("div:first");var g="fixed";if(this.height==null||this.height=="auto"){g="verticalwrap"}if(this.width==null||this.width=="auto"){if(g=="fixed"){g="horizontalwrap"}else{g="wrap"}}c.jqxPanel({theme:this.theme,touchMode:this.touchMode,autoUpdate:true,sizeMode:g});if(a.browser.msie&&a.browser.version<8){this.host.attr("hideFocus",true);this.host.find("div").attr("hideFocus",true);this.host.find("ul").attr("hideFocus",true)}c[0].className="";this.panel=c}this._raiseEvent("3",this)},isTouchDevice:function(){var b=a.jqx.mobile.isTouchDevice();if(this.touchMode==true){b=true}else{if(this.touchMode==false){b=false}}return b},createID:function(){var b=Math.random()+"";b=b.replace(".","");b="99"+b;b=b/1;while(this.items[b]){b=Math.random()+"";b=b.replace(".","");b=b/1}return"treeItem"+b},createTree:function(b){if(b==null){return}var d=this;var f=a(b).find("li");var c=0;this.items=new Array();this.itemMapping=new Array();a(b).addClass(d.toThemeProperty("jqx-tree-dropdown-root"));for(var e=0;e<f.length;e++){this._createItem(f[e])}this._updateItemsNavigation();this._updateCheckStates()},_updateCheckLayout:function(c){var b=this;a.each(this.items,function(){if(this.level==c||c==undefined){b._updateCheckItemLayout(this)}})},_updateCheckItemLayout:function(b){if(this.checkboxes){if(a(b.titleElement).css("display")!="none"){var c=a(b.checkBoxElement);var d=a(b.titleElement).outerHeight()/2-1-parseInt(this.checkSize)/2;c.css("margin-top",d);if(a.browser.msie&&a.browser.version<8){b.titleElement.css("margin-left",parseInt(this.checkSize)+25)}else{c.css("margin-left",16)}}}},_updateCheckStates:function(){var b=this;if(b.hasThreeStates){a.each(this.items,function(){b._updateCheckState(this)})}else{a.each(this.items,function(){if(this.checked==null){b.checkItem(this.element,false)}})}},_updateCheckState:function(e){if(e==null||e==undefined){return}var d=this;var c=0;var f=false;var b=0;var g=a(e.element).find("li");b=g.length;if(e.checked&&b>0){a.each(g,function(h){var k=d.itemMapping[this.id].item;var j=k.element.getAttribute("item-checked");if(j==undefined||j==null||j=="true"||j==true){d.checkItem(k.element,true)}})}a.each(g,function(h){var j=d.itemMapping[this.id].item;if(j.checked!=false){if(j.checked==null){f=true}c++}});if(b>0){if(c==b){this.checkItem(e.element,true)}else{if(c>0){this.checkItem(e.element,null)}else{this.checkItem(e.element,false)}}}},_updateItemsNavigation:function(){var f=this.host.find("ul:first");var e=a(f).find("li");var c=0;for(i=0;i<e.length;i++){var b=e[i];var d=this.itemMapping[b.id].item;if(i>0){d.prevItem=this.itemMapping[e[i-1].id].item}if(i<e.length-1){d.nextItem=this.itemMapping[e[i+1].id].item}}},_applyTheme:function(e,h){var f=this;this.host.removeClass("jqx-tree-vertical-"+e);this.host.removeClass("jqx-tree-"+e);this.host.removeClass("jqx-widget-"+e);this.host.addClass(f.toThemeProperty("jqx-tree"));this.host.addClass(f.toThemeProperty("jqx-tree-vertical"));this.host.addClass(f.toThemeProperty("jqx-widget"));var b=this.host.find("ul:first");a(b).removeClass(f.toThemeProperty("jqx-tree-dropdown-root-"+e));a(b).addClass(f.toThemeProperty("jqx-tree-dropdown-root"));var g=a(b).find("li");for(var d=0;d<g.length;d++){var c=g[d];a(c).children().each(function(){if(this.tagName=="ul"||this.tagName=="UL"){a(this).removeClass(f.toThemeProperty("jqx-tree-dropdown-"+e));a(this).addClass(f.toThemeProperty("jqx-tree-dropdown"));return false}})}a.each(this.items,function(){var l=this;var k=a(l.element);k.removeClass(f.toThemeProperty("jqx-tree-item-li-"+e));k.addClass(f.toThemeProperty("jqx-tree-item-li"));a(l.titleElement).removeClass(f.toThemeProperty("jqx-tree-item-"+e));a(l.titleElement).addClass(f.toThemeProperty("jqx-tree-item"));a(treeItem.titleElement).removeClass("jqx-state-default-"+e);a(treeItem.titleElement).addClass(f.toThemeProperty("jqx-state-default"));var j=a(l.arrow);if(!l.isExpanded){j.addClass(f.toThemeProperty("jqx-tree-item-arrow-collapse"))}else{j.addClass(f.toThemeProperty("jqx-tree-item-arrow-expand"))}if(l.checkBoxElement){a(l.checkBoxElement).jqxCheckBox({theme:h})}if(f.enableRoundedCorners){k.removeClass("jqx-rc-all-"+e);k.addClass(f.toThemeProperty("jqx-rc-all"))}});if(this.host.jqxPanel){this.panel.jqxPanel({theme:h})}},_createItem:function(c){if(c==null||c==undefined){return}var o=c.id;if(!o){o=this.createID()}var z=c;var l=a(c);z.id=o;var t=this.items.length;this.items[t]=new a.jqx._jqxTree.jqxTreeItem();this.treeElements[o]=this.items[t];t=this.items.length;var u=0;var B=this;var e=null;a(z).children().each(function(){if(this.tagName=="ul"||this.tagName=="UL"){B.items[t-1].subtreeElement=this;a(this).addClass(B.toThemeProperty("jqx-tree-dropdown"));return false}});a(z).parents().each(function(){if((this.tagName=="li"||this.tagName=="LI")){u=this.id;e=this;return false}});var s=c.getAttribute("item-expanded");if(s==null||s==undefined||(s!="true"&&s!=true)){s=false}else{s=true}l.removeAttr("item-expanded");var A=c.getAttribute("item-locked");if(A==null||A==undefined||(A!="true"&&A!=true)){A=false}else{A=true}l.removeAttr("item-locked");var p=c.getAttribute("item-selected");if(p==null||p==undefined||(p!="true"&&p!=true)){p=false}else{p=true}l.removeAttr("item-selected");var d=c.getAttribute("item-disabled");if(d==null||d==undefined||(d!="true"&&d!=true)){d=false}else{d=true}l.removeAttr("item-disabled");var h=c.getAttribute("item-checked");if(h==null||h==undefined||(h!="true"&&h!=true)){h=false}else{h=true}var C=c.getAttribute("item-title");if(C==null||C==undefined||(C!="true"&&C!=true)){C=false}l.removeAttr("item-title");var x=c.getAttribute("item-icon");var q=c.getAttribute("item-iconsize");var j=c.getAttribute("item-label");var r=c.getAttribute("item-value");l.removeAttr("item-icon");l.removeAttr("item-iconsize");l.removeAttr("item-label");l.removeAttr("item-value");var w=this.items[t-1];w.id=o;w.value=r;w.icon=x;w.iconsize=q;w.parentId=u;w.disabled=d;w.parentElement=e;w.element=c;w.locked=A;w.selected=p;w.checked=h;w.isExpanded=s;this.itemMapping[t-1]={element:z,item:w};this.itemMapping[z.id]=this.itemMapping[t-1];var g=a(c).find('[item-title="true"]').length>0;var y=a(z).find('[item-title="true"]').parents("li:first")[0]==z;g=false;if(!g||!y){if(a(z.firstChild).length>0){if(w.icon){var q=w.iconsize;if(!q){q=16}var x=a('<img width="'+q+'" height="'+q+'" style="float: left;" class="itemicon" src="'+w.icon+'"/>');a(z).prepend(x);x.css("margin-right","4px")}var b=z.innerHTML.indexOf("<ul");if(b==-1){b=z.innerHTML.indexOf("<UL")}if(b==-1){w.originalTitle=z.innerHTML;a(z).wrapInner('<div style="display: inline-block;"/>');w.titleElement=a(a(z)[0].firstChild)}else{var v=z.innerHTML.substring(0,b);v=a.trim(v);w.originalTitle=v;v=a('<div style="display: inline-block;">'+v+"</div>");var m=a(z).find("ul:first");m.remove();z.innerHTML="";a(z).prepend(v);a(z).append(m);w.titleElement=v}if(a.browser.msie&&a.browser.version<8){a(a(z)[0].firstChild).css("display","inline-block")}}else{w.originalTitle="Item";a(z).append(a("<span>Item</span>"));a(z.firstChild).wrap("<span/>");w.titleElement=a(z)[0].firstChild;if(a.browser.msie&&a.browser.version<8){a(z.firstChild).css("display","inline-block")}}}a(w.titleElement).addClass(this.toThemeProperty("jqx-rc-all"));if(j==null||j==undefined){j=w.titleElement;w.label=a.trim(a(w.titleElement).text())}else{w.label=j}a(z).addClass(this.toThemeProperty("jqx-tree-item-li"));a(w.titleElement).addClass(this.toThemeProperty("jqx-state-default"));a(w.titleElement).addClass(this.toThemeProperty("jqx-tree-item"));w.level=a(c).parents("li").length;if(this.checkboxes){if(this.host.jqxCheckBox){var n=a('<div style="position: absolute; width: 18px; height: 18px;" tabIndex=0 class="chkbox"/>');n.width(parseInt(this.checkSize));n.height(parseInt(this.checkSize));a(z).prepend(n);n.jqxCheckBox({checked:w.checked,boxSize:this.checkSize,animationShowDelay:0,animationHideDelay:0,disabled:d,theme:this.theme});w.titleElement.css("margin-left",parseInt(this.checkSize)+6);w.checkBoxElement=n[0];n[0].treeItem=w;var f=a(w.titleElement).outerHeight()/2-1-parseInt(this.checkSize)/2;n.css("margin-top",f);if(a.browser.msie&&a.browser.version<8){w.titleElement.css("width","1%");w.titleElement.css("margin-left",parseInt(this.checkSize)+25)}else{n.css("margin-left",16)}}else{alert("jqxcheckbox.js is not loaded.")}}else{if(a.browser.msie&&a.browser.version<8){w.titleElement.css("width","1%")}}if(d){this.disableItem(w.element)}if(p){this.selectItem(w.element)}if(a.browser.msie&&a.browser.version<8){a(z).css("margin","0px");a(z).css("padding","0px")}w.hasItems=a(c).find("li").length>0},destroy:function(){this.host.removeClass()},_raiseEvent:function(f,c){if(c==undefined){c={owner:null}}var d=this.events[f];args=c;args.owner=this;var e=new jQuery.Event(d);e.owner=this;e.args=args;var b=this.host.trigger(e);return b},getTop:function(b){var c=b.offsetTop;while((b=b.offsetParent)!=null){if(b.tagName!="HTML"){c+=(b.offsetTop-b.scrollTop);if(document.all){c+=b.clientTop}}}return c},getLeft:function(b){var c=b.offsetLeft;while((b=b.offsetParent)!=null){if(b.tagName!="HTML"){c+=b.offsetLeft;if(document.all){c+=b.clientLeft}}}return c},propertyChangedHandler:function(b,d,g,f){if(this.isInitialized==undefined||this.isInitialized==false){return}if(d=="theme"){this._applyTheme(g,f)}if(d=="width"||d=="height"){b.refresh();b._initialize();b._calculateWidth();if(b.host.jqxPanel){var h="fixed";if(this.height==null||this.height=="auto"){h="verticalwrap"}if(this.width==null||this.width=="auto"){if(h=="fixed"){h="horizontalwrap"}else{h="wrap"}}this.panel.jqxPanel({sizeMode:h})}}if(d=="source"){if(this.source!=null){var c=this.loadItems(this.source);this.element.innerHTML=c;var e=this.host.find("ul:first");if(e.length>0){this.createTree(e[0]);this._render()}}}if(d=="hasThreeStates"){this._render();this._updateCheckStates()}}})})(jQuery);(function(a){a.jqx._jqxTree.jqxTreeItem=function(e,d,b){var c={label:null,id:e,parentId:d,parentElement:null,parentItem:null,disabled:false,selected:false,locked:false,checked:false,level:0,isExpanded:false,hasItems:false,element:null,subtreeElement:null,checkBoxElement:null,titleElement:null,arrow:null,prevItem:null,nextItem:null};return c}})(jQuery);