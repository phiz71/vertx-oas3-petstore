package com.github.phiz71.vertx.oas3.petstore.util;

import com.github.phiz71.vertx.oas3.petstore.MongoClientVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A verticle setting and reading a value in Redis.
 */
public class TestMongoClientVerticle extends MongoClientVerticle {
  
  public static Map<String, List<JsonObject>> db;
  
  public static void initFakeDb(String key) {
    db = new HashMap<>();
    db.put(key, new ArrayList<>());
  }
  
  @Override
  public void start() throws Exception {
    
    vertx.eventBus().<JsonObject>consumer(MONGO_ADD_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      
      String key = mongoCommand.getString(MONGO_COMMAND_ID_KEY);
      
      JsonObject newObject = mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE);
      if (!"ERROR".equalsIgnoreCase(newObject.getString("name"))) {
        db.get(key).add(newObject);
        jsonObjectMessage.reply(null);
      } else {
        jsonObjectMessage.fail(500, "Error raised");
      }
    });
    
    vertx.eventBus().<JsonObject>consumer(MONGO_REMOVE_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      
      String key = mongoCommand.getString(MONGO_COMMAND_ID_KEY);
      JsonObject objectToDelete = mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE);
      
      if (objectToDelete.getLong("id") != -1) {
        List<JsonObject> newList = db.get(key).stream().filter(pet -> !pet.getLong("id").equals(objectToDelete.getLong("id"))).collect(Collectors.toList());
        if (db.get(key).size() != newList.size()) {
          db.put(key, newList);
          jsonObjectMessage.reply(null);
        } else {
          jsonObjectMessage.fail(500, "pet not found :" + objectToDelete.getLong("id"));
        }
      } else {
        jsonObjectMessage.fail(500, "Error raised");
      }
      
    });
    
    vertx.eventBus().<JsonObject>consumer(MONGO_FIND_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      
      String key = mongoCommand.getString(MONGO_COMMAND_ID_KEY);
      JsonObject objectToFind = mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE);
      JsonArray tags = objectToFind.getJsonArray("$or");
      Integer limit = mongoCommand.getInteger(MONGO_COMMAND_ID_LIMIT);
      
      if ((objectToFind.getLong("id") != null && objectToFind.getLong("id") == -1) || (limit != null && limit == -1)) {
        jsonObjectMessage.fail(500, "Error raised");
      } else {
        Stream<JsonObject> stream = db.get(key).stream();
        List<JsonObject> foundItems = null;
        if (objectToFind.getLong("id") != null)
          stream = stream.filter(pet -> pet.getLong("id").equals(objectToFind.getLong("id")));
        if (tags != null)
          stream = stream.filter(pet -> contains(tags, pet.getString("tag")));
        
        if (limit != null)
          stream = stream.limit(limit);
        
        foundItems = stream.collect(Collectors.toList());
        
        if (foundItems == null) {
          jsonObjectMessage.fail(500, "Internal Error");
        } else {
          jsonObjectMessage.reply(new JsonArray(foundItems));
        }
      }
    });
    
  }
  
  private boolean contains(JsonArray tags, String tag) {
    for (JsonObject tagJo : (List<JsonObject>) tags.getList()) {
      if (tagJo.getString("tag").equalsIgnoreCase(tag)) {
        return true;
      }
    }
    return false;
  }
}