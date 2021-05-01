package Database;

import Cache.RedisConnection;
import Config.Config;
import Models.*;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.*;
import com.arangodb.util.MapBuilder;
import com.arangodb.model.*;
import io.netty.handler.logging.LogLevel;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.json.simple.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

    public class ArangoInstance {

        private Config conf = Config.getInstance();
        private Gson gson;
        private ArangoDB arangoDB;
        private String dbUserName = conf.getArangoUserName();
        private String dbPass = conf.getArangoQueuePass();
        protected RedisConnection redis = RedisConnection.getInstance();
        private String dbName = conf.getArangoDbName();

        public ArangoInstance(int maxConnections){
            gson=new Gson();
            arangoDB = new ArangoDB.Builder().host(conf.getArangoHost(),conf.getArangoPort()).user(dbUserName).maxConnections(maxConnections).build();
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database connected: POST"));
            

        }

        public String insert(String collectionName, Object object){
            JSONObject json = new JSONObject(object.toString());
            DocumentEntity document = arangoDB.db(dbName).collection(collectionName).insertDocument(json.toString());
            return document.getKey();
        }

        public JSONObject delete(String collectionName, Object key){
            DocumentDeleteEntity<Void> data= arangoDB.db(dbName).collection(collectionName).deleteDocument((String) key);
            return new JSONObject(gson.toJson(data));
            
        }

        public JSONObject find(String collectionName, Object key, String modelName){
           String id = collectionName+","+(String)key;
           String value = redis.getKey(id);
           if(value != null){
               System.out.println(value + "VALUE");
               return new JSONObject(value);
           }
            try {
                Class modelClass = modelName == null ? null : Class.forName(String.format("Models.%s", modelName));
                Object modelObject = modelClass == null ? null : modelClass.newInstance();
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
                // 404 here
                return null;
            }
        }



        public void initializeDB() {

            try {

//                JSONParser parser=new JSONParser();
//                JSONObject userSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/userSchema.json"));
//                JSONObject notificationSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/notificationSchema.json"));
//                JSONObject chatSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/chatSchema.json"));
//                JSONObject profileViewSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/profileViewSchema.json"));
//                arangoDB.createDatabase(dbName);
//                CollectionSchema user_schema=new CollectionSchema();
//                user_schema.setMessage((String) userSchema.get("message"));
//                user_schema.setRule(userSchema.get("rule").toString());
//                user_schema.setLevel(CollectionSchema.Level.MODERATE);
//                CollectionSchema chat_schema=new CollectionSchema();
//                chat_schema.setMessage(chatSchema.get("message").toString());
//                chat_schema.setRule(chatSchema.get("rule").toString());
//                chat_schema.setLevel(CollectionSchema.Level.MODERATE);
//                CollectionSchema profileView_schema=new CollectionSchema();
//                profileView_schema.setMessage(profileViewSchema.get("message").toString());
//                profileView_schema.setRule(profileViewSchema.get("rule").toString());
//                profileView_schema.setLevel(CollectionSchema.Level.MODERATE);
//                CollectionSchema notification_schema=new CollectionSchema();
//                notification_schema.setMessage(notificationSchema.get("message").toString());
//                notification_schema.setRule(notificationSchema.get("rule").toString());
//                notification_schema.setLevel(CollectionSchema.Level.MODERATE);

//                arangoDB.db(dbName).createCollection("users",new CollectionCreateOptions().setSchema(user_schema));
//                arangoDB.db(dbName).createCollection("notifications",new CollectionCreateOptions().setSchema(notification_schema));
//                arangoDB.db(dbName).createCollection("chats",new CollectionCreateOptions().setSchema(chat_schema));
//                arangoDB.db(dbName).createCollection("profileViews",new CollectionCreateOptions().setSchema(profileView_schema));
//             arangoDB
                arangoDB.createDatabase(dbName);
                arangoDB.db(dbName).createCollection("users");
                arangoDB.db(dbName).createCollection("notifications");
                arangoDB.db(dbName).createCollection("chats");
                arangoDB.db(dbName).createCollection("profileViews");
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Database created: " + dbName));

                System.out.println("Database created: " + dbName);
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database created: " + dbName));
            } catch (ArangoDBException e) {
                e.printStackTrace();
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Failed to create database: " + dbName));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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


        public DocumentEntity updateUserData(Object userID,Object userData){
            JSONObject storedData = this.find("users", userID, "UserData");
            DocumentEntity response=null;
            System.out.println("UserData Is" + new Gson().toJson(userData));
            if (storedData!=null){
                System.out.println(userData);
               response = arangoDB.db(dbName).collection("users").updateDocument((String) userID, userData.toString(), new DocumentUpdateOptions().returnNew(true));
            }
            else{
//                throw error 404
            }
            return response;
        }

//        public CategoryDBObject getCategory(String id){
//            // System.out.println(arangoDB.db(dbName).collection("categories").getDocument(id,Arango.CategoryDBObject.class));
//            CategoryDBObject category =arangoDB.db(dbName).collection("categories").getDocument(id, CategoryDBObject.class);
//            return category;
//        }
//        public void updateCategory(String id, CategoryDBObject category){
//            if(getCategory(id)!=null) {
//                arangoDB.db(dbName).collection("categories").updateDocument(id, category);
//            }
//
//        }




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

