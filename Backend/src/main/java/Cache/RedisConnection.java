package Cache;

import Config.Config;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.json.JSONArray;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class RedisConnection {
     private Properties config = Config.getInstance().getRedisConfig();
     private String port = config.getProperty("port");
     private String host = config.getProperty("host");
     private int expiryDuration = Integer.parseInt(config.getProperty("expiry_hours"));
     private String redisPath =  String.format("redis://%s:%s", host, port);
     private static RedisCommands<String,String> redisCommands;
     private static RedisConnection redis;
     public RedisConnection(){
         RedisClient redisClient = RedisClient.create(redisPath);
         StatefulRedisConnection<String, String> connection = redisClient.connect();
         redisCommands = connection.sync();
         redis = this;
     }

     public static RedisCommands<String,String> getRedisCommands(){
         return redisCommands;
     }

     public String getKey(String key){
           return redisCommands.get(key);
     }

     public String setKey(String key,String value){
         redisCommands.set(key,value);
         Date oldDate = new Date();
         Date newDate = new Date(oldDate.getTime() + TimeUnit.HOURS.toMillis(expiryDuration));
         redisCommands.expireat(key,newDate);
         return value;
     }
     public static RedisConnection getInstance(){
         return redis;
     }

}
