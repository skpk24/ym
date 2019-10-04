<div class="screenlet-title-bar">
	    	<h3>
	    		Send Message
		    </h3>
		</div>
<br>
<br>
<table class="basic-table hover-bar">
 <form method="post" action="<@ofbizUrl>sendMessage</@ofbizUrl>">
  <tr>
     <td>From Facility</td>
     <td>
	   <select  name="fromfacility">
	     <option value="10000">FNP Ashok Vihar</option>
	     <option value="MyRetailStore">FNP Sarita Vihar</option>
	     <option value="WebStoreWarehouse">FNP VASANT VIHAR</option>
	   </select>
     </td>
  </tr>
   <input type="hidden"  name="nextAction" value="PULL"/>
   <input type="hidden"  name="incommingAction" value="PUSH"/>
   <input type="hidden"  name="status" value="Pending"/>
 <tr>
     <td>To Facility</td>
     <td>
	   <select  name="tofacility">
	     <option value="MyRetailStore">FNP Sarita Vihar</option>
	     <option value="10000">FNP Ashok Vihar</option>
	     <option value="WebStoreWarehouse">FNP VASANT VIHAR</option>
	   </select>
     </td>
 </tr>
	<tr>
	    <td>Comment</td>
	      <td>
		    <textarea name="comment" rows="5" cols="100"></textarea>
	      </td>
	 </tr>
	 
	 <tr>
	      <td colspan="2" align="center">
		    <input type="submit" value="SEND" class="buttontext"/>
	      </td>
	 </tr>
	 
	 
</form>
</table>