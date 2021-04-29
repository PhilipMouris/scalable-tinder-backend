package Database;

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

        private String dbName = conf.getArangoDbName();

        public ArangoInstance(int maxConnections){
            gson=new Gson();
            arangoDB = new ArangoDB.Builder().host(conf.getArangoHost(),conf.getArangoPort()).user(dbUserName).maxConnections(maxConnections).build();
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database connected: POST"));
            

        }


        public void initializeDB(){

            try{
                
                JSONParser parser=new JSONParser();
                JSONObject userSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/userSchema.json"));
                JSONObject notificationSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/notificationSchema.json"));
                JSONObject chatSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/chatSchema.json"));
                JSONObject profileViewSchema = (JSONObject) parser.parse(new FileReader("/home/vm/Desktop/scalable-tinder/db/NoSQL/profileViewSchema.json"));
                arangoDB.createDatabase(dbName);
                CollectionSchema user_schema=new CollectionSchema();
                user_schema.setMessage((String) userSchema.get("message"));
                user_schema.setRule(userSchema.get("rule").toString());
                user_schema.setLevel(CollectionSchema.Level.MODERATE);
                CollectionSchema chat_schema=new CollectionSchema();
                chat_schema.setMessage(chatSchema.get("message").toString());
                chat_schema.setRule(chatSchema.get("rule").toString());
                chat_schema.setLevel(CollectionSchema.Level.MODERATE);
                CollectionSchema profileView_schema=new CollectionSchema();
                profileView_schema.setMessage(profileViewSchema.get("message").toString());
                profileView_schema.setRule(profileViewSchema.get("rule").toString());
                profileView_schema.setLevel(CollectionSchema.Level.MODERATE);
                CollectionSchema notification_schema=new CollectionSchema();
                notification_schema.setMessage(notificationSchema.get("message").toString());
                notification_schema.setRule(notificationSchema.get("rule").toString());
                notification_schema.setLevel(CollectionSchema.Level.MODERATE);

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
            } catch (ArangoDBException | FileNotFoundException e) {
                e.printStackTrace();
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR,"Failed to create database: " + dbName));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
        public String insertNewUser(UserData userData){
            DocumentEntity e = arangoDB.db(dbName).collection("users").insertDocument(userData);
            return e.getKey();
        }
        public UserData getUserData(String userID){
            UserData userData=arangoDB.db(dbName).collection("users").getDocument(userID, UserData.class);
            return userData;
        }
        public DocumentEntity userAddBio(String userID, String bio) {
            UserData userData=getUserData(userID);
            DocumentEntity response=null;
            if (userData!=null){
                userData.setBio(bio);
                response= arangoDB.db(dbName).collection("users").updateDocument(userID, userData,new DocumentUpdateOptions().returnNew(true));
            }
            else{
//                throw error 404
            }
            return response;
        }
        public DocumentEntity updateUserData(String userID,UserData userData){
            UserData userDataToFind=getUserData(userID);
            DocumentEntity response=null;
            System.out.println("UserData Is"+new Gson().toJson(userData));
            if (userDataToFind!=null){
                System.out.println(userData);
               response= arangoDB.db(dbName).collection("users").updateDocument(userID, gson.toJson(userData),new DocumentUpdateOptions().returnNew(true));
            }
            else{
//                throw error 404
            }
            return response;
        }
        public DocumentDeleteEntity deleteUserData(String userID){
            DocumentDeleteEntity<Void> userData=arangoDB.db(dbName).collection("users").deleteDocument(userID);
            return userData;
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

