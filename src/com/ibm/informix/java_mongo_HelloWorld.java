package com.ibm.informix;
/**
 * Java Sample Application: Connect to Informix using the Mongo java driver
 **/

//Topics
//1 Inserts
//1.1 Insert a single document into a collection
//1.2 Insert multiple documents into a collection
//2 Queries
//2.1 Find one document in a collection that matches a query condition  
//2.2 Find documents in a collection that match a query condition
//2.3 Find all documents in a collection
//3 Update documents in a collection
//4 Delete documents in a collection
//5 Drop a collection

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


public class java_mongo_HelloWorld {
	
	// To run locally, set MONGOURL and DATABASE_NAME here
	public static String MONGOURL = "";
	public static String DATABASE_NAME = "testdb";
	
	// Service name for if credentials are parsed out of the Bluemix VCAP_SERVICES
	public static String SERVICE_NAME = "timeseriesdatabase";
	public static boolean USE_SSL = false;
	
	public static final String collectionName = "javaTest";
	public static final DataFormat user1 = new DataFormat("test1", 1);
	public static final DataFormat user2 = new DataFormat("test2", 2);
	public static final DataFormat user3 = new DataFormat("test3", 3);
	
	public static final List<String> output = new ArrayList<String>();

	public static void main(final String[] args) {
		
		doEverything();
		
		for (String line : output) {
			System.out.print(line + "\n");
		}
	}
	
	public static void doEverything() {
		output.clear();
		
		MongoClient client = null;
		try {
			// parse connection information
			parseVcap();
		
			// Here the Mongo client opens the connection to the server at the URL that you provide
			MongoClientURI uri = new MongoClientURI(MONGOURL);
			client = new MongoClient(uri);
			
			// This is the collection that you will be working with throughout the program.
			DB db = client.getDB(DATABASE_NAME);
			DBCollection collection = db.getCollection(collectionName);
			output.add("Connected to: " + MONGOURL);
			
			output.add("\nTopics");
			
			//1 Inserts
			output.add("\n1 Inserts");
			//1.1 Insert a single document into a collection
			output.add("1.1 Insert a single document into a collection");
			
			BasicDBObject insertSample = new BasicDBObject();
			insertSample.put("name", user1.name);
			insertSample.put("value", user1.value);
			collection.insert(insertSample);
			
			output.add("\tInserting document: " + insertSample);
			
			//1.2 Insert multiple documents into a collection
			output.add("1.2 Insert multiple documents into a collection");
			
			List<DBObject> multiDocs = new ArrayList<>();
			BasicDBObject insertSamples1 = new BasicDBObject();
			insertSamples1.put("name", user1.name);
			insertSamples1.put("value", user1.value);
			BasicDBObject insertSamples2 = new BasicDBObject();
			insertSamples2.put("name", user2.name);
			insertSamples2.put("value", user2.value);
			BasicDBObject insertSamples3 = new BasicDBObject();
			insertSamples3.put("name", user3.name);
			insertSamples3.put("value", user3.value);
			multiDocs.add(insertSamples1);
			multiDocs.add(insertSamples2);
			multiDocs.add(insertSamples3);
			collection.insert(multiDocs);
			
			output.add("\tInserting document: -> " + insertSamples1);
			output.add("\tInserting document: -> " + insertSamples2);
			output.add("\tInserting document: -> " + insertSamples3);
			
			output.add("\n2 Queries");
	
			//2.1 Find one document in a collection that matches a query condition
			output.add("2.1 Find one document in a collection that matches a query condition");
			
			DBObject doc3 = new BasicDBObject();
			BasicDBObject searchOneQuery = new BasicDBObject();
			searchOneQuery.put("name", user1.name);
			doc3 = collection.findOne(searchOneQuery);
			
			output.add("\tFinding one with name " + user1.name);
			output.add("\tFound: " + doc3);
			
			//2.2 Find documents in a collection that match a query condition
			output.add("2.2 Find documents in a collection that match a query condition");
			List<DBObject> docs = new ArrayList<DBObject>();
			BasicDBObject searchAllQuery = new BasicDBObject();
			searchAllQuery.put("name", user1.name);
			DBCursor findCursor = collection.find(searchAllQuery);
			while (findCursor.hasNext()) {
				docs.add(findCursor.next());
			}
			findCursor.close();
			
			output.add("\tFinding all with name: " + user1.name);
			for (DBObject doc : docs)
				output.add("\tFound -> " + doc);
			
			
			//2.3 Find all documents in a collection
			output.add("2.3 Find all documents in a collection");
			
			List<DBObject> allDocs = new ArrayList<DBObject>();
			DBCursor allCursor = collection.find();
			while (allCursor.hasNext()) {
				allDocs.add(allCursor.next());
			}
			allCursor.close();
			
			output.add("\tDisplaying all documents: ");
			for (DBObject doc : allDocs){
				output.add("\t" + doc);
			}
			
			//3 Update documents in a collection
			output.add("\n3 Update documents in a collection");
			
			BasicDBObject doc2 = new BasicDBObject();
			int newValue = 5;
			doc2.put("value", newValue);
			BasicDBObject update = new BasicDBObject();
			// Using '$set' allows you to keep the rest of the document the same and only update the attribute desired
			update.put("$set", doc2);
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", user2.name);
			collection.update(searchQuery, update);
			
			output.add("\tUpdating: " + searchQuery + " with value: " + newValue);
			
			//4 Remove documents in a collection
			output.add("\n4 Remove documents in a collection");
			
			BasicDBObject removeQuery = new BasicDBObject();
			String removeName = "test2";
			removeQuery.put("name", removeName);
			collection.remove(removeQuery);
			
			output.add("\tRemoving documents with the name: " + removeName);
			output.add("\tDocuments removed");
			
			//5 Drop a collection
			output.add("\n5 Drop a collection");
			
			collection.drop();
			
			output.add("\tDropping collection " + collectionName);
			output.add("\tCollection dropped");
			
			// close connection
			output.add("\nDone");
			
		} catch (Exception e) {
			output.add("ERROR: " + e);
			e.printStackTrace();
			System.out.println("-------------------------------------\n");
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	public static void parseVcap() throws Exception {
		if (MONGOURL != null && !MONGOURL.equals("")) {
			// If MONGOURL is already set, use it as is
			return;
		}
 
		// Otherwise parse URL and credentials from VCAP_SERVICES
		String serviceName = System.getenv("SERVICE_NAME");
		if(serviceName == null || serviceName.length() == 0) {
			serviceName = SERVICE_NAME;
		}
		String vcapServices = System.getenv("VCAP_SERVICES");
		if (vcapServices == null) {
			throw new Exception("VCAP_SERVICES not found in the environment"); 
		}
		StringReader stringReader = new StringReader(vcapServices);
		JsonReader jsonReader = Json.createReader(stringReader);
		JsonObject vcap = jsonReader.readObject();
		if (vcap.getJsonArray(serviceName) == null) {
			throw new Exception("Service " + serviceName + " not found in VCAP_SERVICES");
		}
		
		JsonObject credentials = vcap.getJsonArray(serviceName).getJsonObject(0).getJsonObject("credentials");
		
		DATABASE_NAME = credentials.getString("db");
		if (USE_SSL) {
			MONGOURL = credentials.getString("mongodb_url_ssl");
		} else {
			MONGOURL = credentials.getString("mongodb_url");
		}
		
		System.out.println("URL -> " + MONGOURL);
		System.out.println("DB -> " + DATABASE_NAME);
	}

}

class DataFormat {
	public final String name;
	public final int value;
	
	public DataFormat (String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String toString(){
		return "name: " + this.name + " value: " + this.value + " ";
	}
}
