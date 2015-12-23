package de.tuberlin.enterprisecomputing.integrations;

import de.tuberlin.enterprisecomputing.domain.EmployeeRequest;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDBService {

	//DynamoDB table name = employee_reimbursements
	private String tableName = "employee_reimbursements";
	//DynamoDB Client
	private AmazonDynamoDBClient dynamoDBClient;
	private DynamoDB dynamoDB;
	Table employeeRequestTable;
	
    public DynamoDBService() {
        //initialise DynamoDB client
    	//Creating a DynamoDB client object
    	dynamoDBClient = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    	//Using EU_WEST_1 region for DynamoDB client
    	dynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
    	// Creating instance of DynamoDB
    	dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public String createRequestEntry(final EmployeeRequest request) {
        
    	//Selecting the table from DynamoDB
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	try
    	{
    		//Building the requestEntryItem
    		Item requestEntryItem = new Item()
    				.withPrimaryKey("requestId", request.getRequestId())
    				.withString("name", request.getName())
    				.withString("timestamp", request.getTimestamp())
    				.withString("when", request.getWhen())
    				.withString("why", request.getWhy())
    				.withString("where", request.getWhere())
    				.withInt("amount", request.getAmount());
    		
    		//writing the Item into the table
    		PutItemOutcome requestEntryOutcome = employeeRequestTable.putItem(requestEntryItem);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
        return request.getRequestId();
    }

    public ArrayList<EmployeeRequest> getAllRequests() {
        //TODO
    	//Creating an ArrayList of EmployeeRequests to be returned
    	ArrayList<EmployeeRequest> allRequests = new ArrayList<EmployeeRequest>();
    	//Creating a temporary instance of EmployeeRequest
    	EmployeeRequest temp = new EmployeeRequest();
    	
    	Map<String, AttributeValue> lastKeyEvaluated = null;
    	//A single scan can not return data more than 1 MB, hence using loop
    	do{
    		//New ScanRequest on the table employee_reimbursements with a limit of 10 entries
    		ScanRequest req = new ScanRequest()
    				.withTableName(tableName)
    				.withLimit(10)
    				.withExclusiveStartKey(lastKeyEvaluated);
    		//collecting the result of the scan
    		ScanResult result = dynamoDBClient.scan(req);
    		//Adding the scanned result into the ArrayList of EmployeeRequests
    	    for (Map<String, AttributeValue> item : result.getItems()){
    	    	
    	    	temp.setRequestId(item.get("requestId").toString());
    	    	temp.setName(item.get("name").toString());
    	    	temp.setTimestamp(item.get("timestamp").toString());
    	    	temp.setWhen(item.get("when").toString());
    	    	temp.setWhy(item.get("why").toString());
    	    	temp.setWhere(item.get("where").toString());
    	    	temp.setAmount(Integer.parseInt(item.get("amount").toString()));
    	    	
    	    	allRequests.add(temp);
    	    	//Clearing the temporary employeeRequest
    	    	temp = null;
    	    }
    	
    	}while(lastKeyEvaluated != null);
    		
        return allRequests;
    }

    public EmployeeRequest getRequestById(final String id) {
        
    	//Selecting the table from DynamoDB
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	// Getting the requestEntry for requestId = id
    	Item requestEntryItem = employeeRequestTable.getItem("requestId", id);
    	// Creating an EmployeeRequest instance to be returned 
    	EmployeeRequest request =new EmployeeRequest(requestEntryItem.getString("requestId"),requestEntryItem.getString("name"),requestEntryItem.getString("timestamp"),requestEntryItem.getString("when"),requestEntryItem.getString("why"),requestEntryItem.getString("where"),requestEntryItem.getInt("amount"));
    	
        return request;
    }

    public void updateRequest(final String id, final EmployeeRequest request) {
        
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	//Creating UpdateItemSpec for the updates to be made
    	UpdateItemSpec requestEntryUpdate = new UpdateItemSpec()
    			.withPrimaryKey("requestID", id)
    			.withValueMap(new ValueMap()
    					.withString("name", request.getName())
    					.withString("timestamp", request.getTimestamp())
    					.withString("when", request.getWhen())
    					.withString("why", request.getWhy())
    					.withString("where", request.getWhere())
    					.withInt("amount", request.getAmount())
    					);
    	//Updating the employeeRequestTable
    	UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem(requestEntryUpdate);
    }

    public void setStatus(final String id, final String status) {
        
    	//Selecting the table from DynamoDB
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	//Updating the requestStatus as status for requestId = id 
    	UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem( id, new AttributeUpdate("requestStatus").put(status));
    	
    }
    
/*    private EmployeeRequest addToArrayList(Map<String, AttributeValue> item)
    {
    	
    	return null;
    }*/

    
}
