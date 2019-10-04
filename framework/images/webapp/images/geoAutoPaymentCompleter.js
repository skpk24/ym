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
window.onload = function() {
	 getAssociatedStateList('countryGeoId', 'stateProvinceGeoId');
	 //changeState1();
}

//Generic function for fetching country's associated state list.
function getAssociatedStateList(countryId, stateId) {
    var optionList = [];
    
    var var1 = document.getElementById("countryGeoId").value;
    var var2 = '';
    var url = "getAssociatedStateList?countryGeoId="+var1+"&stateProvinceGeoId1="+var2+"";
        jQuery.ajax({url:url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
        jQuery('#statesdisplay').html(data);
		changeState1(data.length);
	  }
    }); 
}
function changeState(){
	
	document.getElementById("hiddenState").value = document.getElementById("stateProvinceGeoId").value;
}

function changeState1(length){
	if(length == "231"){
	document.getElementById("hiddenState").value = "";
	}else{
		document.getElementById("stateProvinceGeoId").value = document.getElementById("hiddenState").value;
	}
}