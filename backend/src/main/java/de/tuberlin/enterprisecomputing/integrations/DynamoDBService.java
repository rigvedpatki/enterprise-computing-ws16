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
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamoDBService {

	//DynamoDB table name = employee_reimbursements
	private String tableName = "employee_reimbursements";
	//DynamoDB Client
	private AmazonDynamoDBClient dynamoDBClient;
	private DynamoDB dynamoDB;
	Table employeeRequestTable;
	
    public DynamoDBService() {
        //TODO initialise DynamoDB client
    	//Creating a DynamoDB client object
    	dynamoDBClient = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    	//Using EU_WEST_1 region for DynamoDB client
    	dynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
    	// Creating instance of DynamoDB
    	dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public String createRequestEntry(final EmployeeRequest request) {
        //TODO
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
/*    	ArrayList<EmployeeRequest> allRequests = new ArrayList<EmployeeRequest>();
    	
    	ScanResult result= null;
    	
    	do{
    		ScanRequest req = new ScanRequest().withAttributesToGet("requestId","");
    		req.setTableName(tableName);
    		
    		if(result != null){
    			req.setExclusiveStartKey(result.getLastEvaluatedKey());
    		}
    		
    		result = dynamoDBClient.scan(req);
    	}*/
    	
        return new ArrayList<>();
    }

    public EmployeeRequest getRequestById(final String id) {
        //TODO
    	//Selecting the table from DynamoDB
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	// Getting the requestEntry for requestId = id
    	Item requestEntryItem = employeeRequestTable.getItem("requestId", id);
    	// Creating a EmployeeRequest instance to be returned 
    	EmployeeRequest request =new EmployeeRequest(requestEntryItem.getString("requestId"),requestEntryItem.getString("name"),requestEntryItem.getString("timestamp"),requestEntryItem.getString("when"),requestEntryItem.getString("why"),requestEntryItem.getString("where"),requestEntryItem.getInt("amount"));
    	
        return request;
    }

    public void updateRequest(final String id, final EmployeeRequest request) {
        //TODO
    }

    public void setStatus(final String id, final String status) {
        //TODO
    	//Selecting the table from DynamoDB
    	Table employeeRequestTable = dynamoDB.getTable(tableName);
    	//Updating the requestStatus as status for requestId = id 
    	UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem( id, new AttributeUpdate("requestStatus").put(status));
    	
    }

    ;
}
