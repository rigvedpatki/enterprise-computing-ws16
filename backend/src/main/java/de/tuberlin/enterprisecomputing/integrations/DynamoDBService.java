package de.tuberlin.enterprisecomputing.integrations;


import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
//import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

//import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import de.tuberlin.enterprisecomputing.domain.EmployeeRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public String createRequestEntry(final EmployeeRequest request, final boolean fileAttached) {

        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table create request entry: " + employeeRequestTable.toString());
        //Generating Random UUID as requestId
        String uniqueRequestId = UUID.randomUUID().toString();
        request.setRequestId(uniqueRequestId);
        //Generating the current timestamp
        Long currentTimeStamp = new Date().getTime();
        request.setTimestamp(currentTimeStamp.toString());
        //generating a URL to the document
        S3Service s3 = new S3Service();
        if (fileAttached) {
            String docLink = s3.generateURL(request.getRequestId());
            request.setDocument(docLink);
        } else {
            request.setDocument("No document attached");
        }
        //Setting the status of document
        request.setStatus("Unchecked");
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
                    .withString("document", request.getDocument())
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
                temp.setDocument(item.get("document").getS());
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
        System.out.println("Dynamodb table: " + dynamoDB.getTable("employee_reimbursements").toString());
        System.out.println("Table get request by id: " + employeeRequestTable.toString());
        // Getting the requestEntry for requestId = id
        Item requestEntryItem = employeeRequestTable.getItem("requestId", id);
        System.out.println(requestEntryItem.toString());
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
        request.setDocument(requestEntryItem.getString("document"));
        request.setStatus(requestEntryItem.getString("status"));


        return request;
    }

    public void updateRequest(final String id, final EmployeeRequest request) {

        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table updateRequest: " + employeeRequestTable.toString());
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
        //UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem(requestEntryUpdate);
        employeeRequestTable.updateItem(requestEntryUpdate);
    }

    public void setStatus(final String id, final String status) {

        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table setStatus: " + employeeRequestTable.toString());
        //Updating the requestStatus as status for requestId = id
        //UpdateItemOutcome employeeRequestOutcome = employeeRequestTable.updateItem( id, new AttributeUpdate("status").put(status));
        employeeRequestTable.updateItem(id, new AttributeUpdate("status").put(status));

    }

    public void setDocLink(final String id, final String docLink) {
        //Selecting the table from DynamoDB
        Table employeeRequestTable = dynamoDB.getTable(tableName);
        System.out.println("Table set doclink: " + employeeRequestTable.toString());
        //Updating the requestStatus as status for requestId = id
        /*UpdateItemOutcome employeeRequestOutcome =*/
        employeeRequestTable.updateItem(id, new AttributeUpdate("document").put(docLink));
    }
}
