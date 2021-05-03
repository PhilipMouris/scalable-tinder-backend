
package Database;

import Cache.RedisConnection;
import Config.Config;
import Models.*;
import Notifications.Firebase;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.*;
import com.arangodb.util.MapBuilder;
import com.arangodb.model.*;
import com.google.gson.JsonElement;
import io.netty.handler.logging.LogLevel;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
    public class ArangoInstance {

        private Config conf = Config.getInstance();
        private Gson gson;
        private ArangoDB arangoDB;
        private String dbUserName = conf.getArangoUserName();
        private String dbPass = conf.getArangoQueuePass();
        private String dbName = conf.getArangoDbName();
        private RedisConnection redis;
        private final Logger LOGGER = Logger.getLogger(ArangoInstance.class.getName()) ;
        public ArangoInstance(int maxConnections) {
            gson = new Gson();
            arangoDB = new ArangoDB.Builder().host(conf.getArangoHost(), conf.getArangoPort()).user(dbUserName).maxConnections(maxConnections).build();
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database connected: POST"));


        }

        public void setRedisConnection(RedisConnection redis){
            this.redis = redis;
        }

        public String getDbName() {
            return dbName;
        }

        public ArangoDB getArangoDB() {
            return arangoDB;
        }

        public void setArangoDB(ArangoDB arangoDB) {
            this.arangoDB = arangoDB;
        }

        public String insert(String collectionName, Object object){
            JSONObject json = new JSONObject(object.toString());
            DocumentEntity document = arangoDB.db(dbName).collection(collectionName).insertDocument(json.toString());
            return document.getKey();
        }

        public JSONObject delete(String collectionName, Object key){
            try {
                DocumentDeleteEntity<Void> data = arangoDB.db(dbName).collection(collectionName).deleteDocument((String) key);
                return new JSONObject(gson.toJson(data));
            }catch(ArangoDBException e){
                return null;
            }
            
        }

        private Object getInstanceObjectFromModelName(String modelName){
            try {
                Class modelClass = modelName == null ? null : Class.forName(String.format("Models.%s", modelName));
                Object modelObject = modelClass == null ? null : modelClass.newInstance();
                return modelObject;
            }catch (Exception e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                return null;
            }
        }

        public JSONObject find(String collectionName, Object key, String modelName){
           String id = collectionName+","+(String)key;
           String value = redis.getKey(id);
           if(value != null){
               return new JSONObject(value);
           }
            try {
                Object modelObject = getInstanceObjectFromModelName(modelName);
                Object object = arangoDB.db(dbName).collection(collectionName).getDocument((String) key, modelObject.getClass());
                JSONObject document = new JSONObject(gson.toJson(object));
                redis.setKey(id,document.toString());
                return document;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public JSONObject update(String collection,Object key,Object updateQuery){
            try {
                JSONObject json = new JSONObject(updateQuery.toString());
                DocumentEntity response = arangoDB.db(dbName).collection("users").updateDocument((String) key, json.toString(), new DocumentUpdateOptions().returnNew(true));
                if (response == null)
                    return null;
                JSONObject dbData = new JSONObject(gson.toJson(response));

                return new JSONObject(dbData.get("newDocument").toString());
            }
            catch(ArangoDBException e) {
                return null;
            }
        }

        private String getFilterQueryString(Object filterParams){
            String filterQueryString = "";
            JSONObject filterJSON = (JSONObject)filterParams;
            System.out.println(filterJSON + "FILTERSS");
            for (String key : filterJSON.keySet()) {
                try {
                    Object keyValue = filterJSON.get(key);
                    String valueOperator = keyValue instanceof String ?  "\"%s\"": "%s" ;
                    filterQueryString += String.format("FILTER doc.%s ==",key) +String.format(valueOperator,keyValue)+ " "+ "\n";
                }catch (Exception e){

                }

            }
            return filterQueryString;
        }

        public JSONArray findAll(String collectionName,Object limit,Object page,String model, Object filterParams) {
            try {
                String query = String.format("FOR doc IN %s ",collectionName);
                query+= getFilterQueryString(filterParams);
                query+= "LIMIT @offset, @count RETURN doc";
                Map<String, Object> bindVars = new HashMap<String,Object> ();
                bindVars.put("count",limit);
                bindVars.put("offset", (int)page * (int)limit);
                return executeQuery(query,bindVars,model,true);


            }
            catch(Exception e){
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                return null;
            }

        }


        public JSONArray executeQuery(String query, Map<String, Object> bindVars,String model,boolean useCache) {
            String id = query + ";" + bindVars.toString();
            if(useCache) {
                String value = redis.getKey(id);
                if (value != null) {
                    return new JSONArray(value);
                }
            }
            List<Object> data = new ArrayList<Object>();
            Object modelObject = getInstanceObjectFromModelName(model);
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                System.out.println(aDocument.toString() + "OKK???");
                //System.out.println(aDocument.getProperties() + "OKKK???");
                String jsonString = gson.toJson(aDocument.getProperties());
                JSONObject jsonObject = new JSONObject(jsonString);
                jsonObject.put("_key",aDocument.getKey());


                Object document = gson.fromJson(jsonObject.toString(),modelObject.getClass());
                data.add(document);
            });
            String stringData = gson.toJson(data);
            if(useCache) {
                redis.setKey(id, stringData);
            }
           return  new JSONArray(stringData);
        }

        public String createNotificaiton(int userID,String type,String title,String body){
            //TODO: FIND user data -> get tokens -> send with fb
            Notification notification = new Notification(userID, type,title,body);
            System.out.println(notification.toString() + "STRING");
            String notificationID = insert("notifications", notification);
            JSONObject userData = find("users", ""+userID, "UserData");
            if(userData!=null) {
                Firebase.sendNotification(userData, notification);
            }
            return notificationID;

        }



        public void initializeDB() {

            try {

//            JSONParser parser = new JSONParser();
//            JSONObject userSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/userSchema.json"));
//            JSONObject notificationSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/notificationSchema.json"));
//            JSONObject chatSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/chatSchema.json"));
//            JSONObject profileViewSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/profileViewSchema.json"));
                arangoDB.createDatabase(dbName);
//            CollectionSchema user_schema = new CollectionSchema();
//            user_schema.setMessage((String) userSchema.get("message"));
//            user_schema.setRule(userSchema.get("rule").toString());
//            user_schema.setLevel(CollectionSchema.Level.MODERATE);
//            CollectionSchema chat_schema = new CollectionSchema();
//            chat_schema.setMessage(chatSchema.get("message").toString());
//            chat_schema.setRule(chatSchema.get("rule").toString());
//            chat_schema.setLevel(CollectionSchema.Level.MODERATE);
//            CollectionSchema profileView_schema = new CollectionSchema();
//            profileView_schema.setMessage(profileViewSchema.get("message").toString());
//            profileView_schema.setRule(profileViewSchema.get("rule").toString());
//            profileView_schema.setLevel(CollectionSchema.Level.MODERATE);
//            CollectionSchema notification_schema = new CollectionSchema();
//            notification_schema.setMessage(notificationSchema.get("message").toString());
//            notification_schema.setRule(notificationSchema.get("rule").toString());
//            notification_schema.setLevel(CollectionSchema.Level.MODERATE);

//                arangoDB.db(dbName).createCollection("users",new CollectionCreateOptions().setSchema(user_schema));
//                arangoDB.db(dbName).createCollection("notifications",new CollectionCreateOptions().setSchema(notification_schema));
//                arangoDB.db(dbName).createCollection("chats",new CollectionCreateOptions().setSchema(chat_schema));
//                arangoDB.db(dbName).createCollection("profileViews",new CollectionCreateOptions().setSchema(profileView_schema));
//
                arangoDB.db(dbName).createCollection("users");
                arangoDB.db(dbName).createCollection("notifications");
                arangoDB.db(dbName).createCollection("chats");
                arangoDB.db(dbName).createCollection("profileViews");
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Database created: " + dbName));

                System.out.println("Database created: " + dbName);
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database created: " + dbName));
            } catch (ArangoDBException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Failed to create database: " + dbName));
            }
        }

        public void dropDB(){

            try{
                arangoDB.db(dbName).drop();
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database dropped: " + dbName));
            } catch (ArangoDBException e) {
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Failed to drop database: " + dbName));
            }
        }








        public void setMaxDBConnections(int maxDBConnections){
            arangoDB = new ArangoDB.Builder().user(dbUserName).password(dbPass).maxConnections(maxDBConnections).build();
        }


        public static void main(String[] args) {
            ArangoInstance arangoInstance  = new ArangoInstance(15);
//            arangoInstance.arangoDB.db("Post").createCollection("notifications");

//              arangoInstance.dropDB();
              arangoInstance.initializeDB();
        }



    }
    