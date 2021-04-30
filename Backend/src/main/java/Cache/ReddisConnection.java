package Cache;

import Config.Config;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import java.util.Properties;

public class ReddisConnection {
     private Properties config = Config.getInstance().getRedisConfig();
     private String port = config.getProperty("port");
     private String host = config.getProperty("host");
     private String redisPath =  String.format("redis://%s:%s", host, port);
     private static RedisCommands<String,String> redisCommands;
     public ReddisConnection(){
         RedisClient redisClient = RedisClient.create(redisPath);
         StatefulRedisConnection<String, String> connection = redisClient.connect();
         redisCommands = connection.sync();
     }

     public static RedisCommands<String,String> getRedisCommands(){
         return redisCommands;
     }

}
