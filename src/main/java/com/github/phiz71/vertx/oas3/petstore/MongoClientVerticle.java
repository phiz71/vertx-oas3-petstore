package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.util.MongoHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

/**
 * A verticle setting and reading a value in Redis.
 */
public class MongoClientVerticle extends AbstractVerticle {
  
  public static final String MONGO_ADD_VALUE_ADDRESS = "mongo.addValue";
  public static final String MONGO_REMOVE_VALUE_ADDRESS = "mongo.removeValue";
  public static final String MONGO_FIND_VALUE_ADDRESS = "mongo.findValue";
  
  public static final String MONGO_COMMAND_ID_KEY = "key";
  public static final String MONGO_COMMAND_ID_VALUE = "value";
  public static final String MONGO_COMMAND_ID_LIMIT = "limit";
  
  @Override
  public void start() throws Exception {
    
    vertx.eventBus().<JsonObject>consumer(MONGO_ADD_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      MongoClient mongoClient = MongoHelper.createMongoClient(vertx);
      mongoClient.save(mongoCommand.getString(MONGO_COMMAND_ID_KEY), mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE), id -> {
        if (id.succeeded())
          jsonObjectMessage.reply(null);
        else
          jsonObjectMessage.fail(500, id.cause().getLocalizedMessage());
        mongoClient.close();
      });
    });
    
    vertx.eventBus().<JsonObject>consumer(MONGO_REMOVE_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      MongoClient mongoClient = MongoHelper.createMongoClient(vertx);
      mongoClient.removeDocument(mongoCommand.getString(MONGO_COMMAND_ID_KEY), mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE), id -> {
        if (id.succeeded())
          jsonObjectMessage.reply(null);
        else
          jsonObjectMessage.fail(500, id.cause().getLocalizedMessage());
        mongoClient.close();
      });
    });
    
    vertx.eventBus().<JsonObject>consumer(MONGO_FIND_VALUE_ADDRESS, jsonObjectMessage -> {
      JsonObject mongoCommand = jsonObjectMessage.body();
      MongoClient mongoClient = MongoHelper.createMongoClient(vertx);
      
      Integer limit = mongoCommand.getInteger(MONGO_COMMAND_ID_LIMIT);
      FindOptions options = new FindOptions();
      if (limit != null) {
        options.setLimit(limit);
      }
      mongoClient.findWithOptions(mongoCommand.getString(MONGO_COMMAND_ID_KEY), mongoCommand.getJsonObject(MONGO_COMMAND_ID_VALUE), options, id -> {
        if (id.succeeded())
          jsonObjectMessage.reply(new JsonArray(id.result()));
        else
          jsonObjectMessage.fail(500, id.cause().getLocalizedMessage());
        mongoClient.close();
      });
    });
  }
}