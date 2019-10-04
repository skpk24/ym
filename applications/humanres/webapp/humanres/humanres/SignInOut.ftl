<script>
  $(document).ready(function() {
	 <#if screenName?has_content>
        <#if screenName.equals("SIGN_IN")>
           document.getElementById('signin').style.display='block'
           <#else>
            document.getElementById('signout').style.display='block'
           </#if>

        </#if>
   });


</script>

<div id="signin" style="display:none">
        <div>
              <div class="mainHeading">
                    <h2>Sign In</h2>
                </div>
        
                <br/>
                 <form method="post" id="SignInForm" action="<@ofbizUrl>storeSignIn</@ofbizUrl>">
                    <table cellspacing="0" cellpadding="5" border="0" class="punchTable">
                         <input type="hidden" id="partyId" value="${userLogin.partyId}" name="partyId">
                        <tbody>
                           
                            <tr>
	                            <td>Date</td>
	                            <td>&nbsp;<span id="currentDate">
	                            <#assign nowdate=Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("yyyy-MM-dd HH:mm")?if_exists/>
	                            <#assign datetime=nowdate.split(" ")>
	                            ${datetime[0]?if_exists}</span><input type="hidden" value="2012-02-21" name="date" class="date"></td>
	                       </tr>
                            <tr>
                             <td> Time</td>
                             <td>&nbsp;<span id="currentTime">${datetime[1]?if_exists}</span>
                             <input type="hidden" value="${datetime[1]?if_exists}" name="time" class="time">&nbsp;&nbsp;&nbsp;&nbsp;<span class="timeFormatHint">HH:MM</span></td>
                            </tr>
                            <tr>
                             <td id="noteLable">Note</td>
                             <td>&nbsp;<textarea cols="50" rows="5" name="note" class="note" id="note"></textarea></td>
                            </tr>
                            <tr><td></td>
                            <td>
                             <input type="submit" value="Sign In"  id="btnPunch" name="button"></td>
                            </tr>
                      </tbody>
                        </table>
                    </form>
   </div>
 </div>
 
 <div id="signout" style="display:none">
        <div>
              <div class="mainHeading">
                    <h2>Sign Out</h2>
                </div>
        
                <br/>
                <form method="post" id="SignOutForm" action="<@ofbizUrl>storeSignOut</@ofbizUrl>">
                    <table cellspacing="0" cellpadding="5" border="0" class="punchTable">
                      <input type="hidden" id="partyId" value="${userLogin.partyId}" name="partyId">
                        <tbody>
                             <tr>
	                            <td>Date</td>
	                            <td>&nbsp;<span id="currentDate">
	                            <#assign nowdate=Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("yyyy-MM-dd HH:mm")?if_exists/>
	                            <#assign datetime=nowdate.split(" ")>
	                            ${datetime[0]?if_exists}</span><input type="hidden" value="2012-02-21" name="date1" class="date"></td>
	                       </tr>
                            <tr>
                             <td> Time</td>
                             <td>&nbsp;<span id="currentTime"> ${datetime[1]?if_exists}</span>
                             <input type="hidden" value="${datetime[1]?if_exists}" name="time" class="time">&nbsp;&nbsp;&nbsp;&nbsp;<span class="timeFormatHint">HH:MM</span></td>
                            </tr>
                            <tr>
                             <td id="noteLable">Note</td>
                             <td>&nbsp;<textarea cols="50" rows="5" name="note1" class="note" id="note1"></textarea></td>
                            </tr>
                            <tr><td></td>
                            <td>
                             <input type="submit" value="Sign Out"  id="btnPunch" name="button"></td>
                            </tr>
                      </tbody>
                        </table>
                    </form>
     </div>
 </div>