import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;




userLoginId = userLogin.userLoginId ;
partyId = delegator.findByPrimaryKey("UserLogin",[userLoginId:userLoginId]);
String home = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/jquery/dashboards/"+partyId.partyId+".js";


File fileToCreate = new File(home);
if(!fileToCreate.exists()) {
    fileToCreate.createNewFile();
} 

events = [:];
//events.put(20120928,"<strong>Balakrishna Prabhakar's Birthday</strong>");
//events.put(20121002,"<strong>Gandhiji's Birthday</strong>");
//events.put(20120320,"<strong>Geetha's Birthday</strong>");
//events.put(20121125,"<strong>Bharath's Birthday</strong>");


                               
Writer output;
output = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(fileToCreate, true), "UTF-8"));

eventList = delegator.findList("WorkEffort",EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId)
                                ,null,null,null,false);
for(int i=0;i<eventList.size();i++){

GenericValue gv = eventList.get(i);

// estimatedStartDate
// estimatedCompletionDate
// workEffortName

estimatedStartDate = (String)gv.estimatedStartDate;

if(estimatedStartDate){
estimatedStartDate=estimatedStartDate.split(" ")[0];
estimatedStartDate = estimatedStartDate.replaceAll("-", "")
workEffortName = gv.workEffortName;
events.put(estimatedStartDate,"<strong>"+workEffortName+"</strong>");
}
} 

for ( e in events ) {
    
    output.append("DefineEvent(" + 
    							"\""+e.key+ "\""
    							+ ", " 
    							+ "\""+ e.value + "\""
    							+"," 
    							+"\""+ "\""+","
    							+ "\""+ "\""+","
    							+ " " + 0 + " " + "," + " "+ 0 +" "+ ");\n");
}

output.close();

                                
                              

BufferedReader reader = new BufferedReader(new FileReader(fileToCreate));
    Set<String> lines = new HashSet<String>(100000); // maybe should be bigger
    String line;
    while ((line = reader.readLine()) != null) {
        lines.add(line);
    }
    reader.close();
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileToCreate));
    for (String unique : lines) {
        writer.write(unique);
        writer.newLine();
    }
    writer.close();

context.ele = partyId.partyId+".js"
