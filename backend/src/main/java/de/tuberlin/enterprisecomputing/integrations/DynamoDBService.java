package de.tuberlin.enterprisecomputing.integrations;


import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import de.tuberlin.enterprisecomputing.domain.EmployeeRequest;

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


    public DynamoDBService() {
        //initialise DynamoDB client
        //Creating a DynamoDB client object
        dynamoDBClient = new AmazonDynamoDBClient(new ProfileCredentialsProvider("enterprise-computing-ws16").getCredentials());
        //dynamoDBClient = new AmazonDynamoDBClient(new ProfileCredentialsProvider().getCredentials());
        //Using EU_WEST_1 region for DynamoDB client
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        // Creating instance of DynamoDB
        dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public String createRequestEntry(final EmployeeRequest request) {

        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table create request entry: " + employeeRequestTable.toString());
        try {
            //Building the requestEntryItem
            Item requestEntryItem = new Item()
                    .withPrimaryKey("requestId", request.getRequestId())
                    .withString("name", request.getName())
                    .withString("timestamp", request.getTimestamp())
                    .withString("when", request.getWhen())
                    .withString("why", request.getWhy())
                    .withString("where", request.getWhere())
                    .withInt("amount", request.getAmount())
                    .withString("documentLink", request.getDocumentLink())
                    .withString("documentName", request.getDocumentName())
                    .withString("status", request.getStatus());


            //writing the Item into the table
            //PutItemOutcome requestEntryOutcome = employeeRequestTable.putItem(requestEntryItem);
            employeeRequestTable.putItem(requestEntryItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request.getRequestId();
    }

    public ArrayList<EmployeeRequest> getAllRequests() {
        //TODO
        //Creating an ArrayList of EmployeeRequests to be returned
        ArrayList<EmployeeRequest> allRequests = new ArrayList<EmployeeRequest>();
        //Creating a temporary instance of EmployeeRequest
        EmployeeRequest temp = null;
        //COmplete result after scanning
        ScanResult result = null;
        //A single scan can not return data more than 1 MB, hence using loop

        do {
            ScanRequest req = new ScanRequest().withTableName(tableName);
            //New ScanRequest on the table employee_reimbursements with a limit of 10 entries
            if (result != null) {
                req.setExclusiveStartKey(result.getLastEvaluatedKey());
            }
            //collecting the result of the scan into a List of rows
            result = dynamoDBClient.scan(req);
            //System.out.println(result.toString());
            List<Map<String, AttributeValue>> rows = result.getItems();

            //Adding the scanned result from rows into the ArrayList of EmployeeRequests
            for (Map<String, AttributeValue> item : rows) {
                // System.out.println(item.toString());
                temp = new EmployeeRequest();
                temp.setRequestId(item.get("requestId").getS());
                temp.setName(item.get("name").getS());
                temp.setTimestamp(item.get("timestamp").getS());
                temp.setWhen(item.get("when").getS());
                temp.setWhy(item.get("why").getS());
                temp.setWhere(item.get("where").getS());
                temp.setAmount(Integer.parseInt(item.get("amount").getN()));
                temp.setDocumentLink(item.get("documentLink").getS());
                temp.setDocumentName(item.get("documentName").getS());
                temp.setStatus(item.get("status").getS());
                //System.out.println(temp);
                allRequests.add(temp);
            }

        } while (result.getLastEvaluatedKey() != null);

        //System.out.println(allRequests.toString());

        return allRequests;
    }

    public EmployeeRequest getRequestById(final String id) {

        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        //System.out.println("Dynamodb table: " + dynamoDB.getTable("employee_reimbursements").toString());
        //System.out.println("Table get request by id: " + employeeRequestTable.toString());
        // Getting the requestEntry for requestId = id
        Item requestEntryItem = employeeRequestTable.getItem("requestId", id);
        //System.out.println(requestEntryItem.toString());
        // Creating an EmployeeRequest instance to be returned
        EmployeeRequest request = new EmployeeRequest();
        //Filling up the instance with data
        request.setRequestId(requestEntryItem.getString("requestId"));
        request.setName(requestEntryItem.getString("name"));
        request.setTimestamp(requestEntryItem.getString("timestamp"));
        request.setWhen(requestEntryItem.getString("when"));
        request.setWhere(requestEntryItem.getString("where"));
        request.setWhy(requestEntryItem.getString("why"));
        request.setAmount(requestEntryItem.getInt("amount"));
        request.setDocumentLink(requestEntryItem.getString("documentLink"));
        request.setDocumentName(requestEntryItem.getString("documentName"));
        request.setStatus(requestEntryItem.getString("status"));


        return request;
    }

    public void updateRequest(final String id, final EmployeeRequest request) {

        Table employeeRequestTable = dynamoDB.getTable(tableName);
        //System.out.println("Table updateRequest: " + employeeRequestTable.toString());
        //Creating UpdateItemSpec for the updates to be made
        
        UpdateItemSpec requestEntryUpdate = new UpdateItemSpec()
                .withPrimaryKey("requestId", id)
                .withUpdateExpression("set #time=:val1, #when=:val2, #why=:val3, #where=:val4, #amount=:val5")
                .withNameMap(new NameMap()
                		.with("#time","timestamp")
                		.with("#when","when")
                		.with("#why","why")
                		.with("#where","where")
                		.with("#amount","amount"))
                .withValueMap(new ValueMap()
                        .withString(":val1",request.getTimestamp())
                        .withString(":val2", request.getWhen())
                        .withString(":val3", request.getWhy())
                        .withString(":val4", request.getWhere())
                        .withInt(":val5", request.getAmount()))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        //Updating the employeeRequestTable
        UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem(requestEntryUpdate);
        System.out.println("Outcome: "+ employeeRequestOutcome.getItem().toJSONPretty());
        //employeeRequestTable.updateItem(requestEntryUpdate);
    }

    public void setStatus(final String id, final String status) {

        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table setStatus: " + employeeRequestTable.toString());
        
        //Creating UpdateItemSpec for the updates to be made
        
        UpdateItemSpec requestEntryUpdate = new UpdateItemSpec()
                .withPrimaryKey("requestId", id)
                .withUpdateExpression("set #s=:val1")
                .withNameMap(new NameMap()
                		.with("#s", "status"))
                .withValueMap(new ValueMap()
                        .withString(":val1", status))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        //Updating the requestStatus as status for requestId = id
        UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem(requestEntryUpdate);
        System.out.println("Outcome: "+ employeeRequestOutcome.getItem().toJSONPretty());
    }

/*    public void setDocLink(final String id, final String docLink) {
        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table set doclink: " + employeeRequestTable.toString());
        //Updating the requestStatus as status for requestId = id
        UpdateItemOutcome employeeRequestOutcome =
        employeeRequestTable.updateItem(id, new AttributeUpdate("documentLink").put(docLink));
    }*/
}
