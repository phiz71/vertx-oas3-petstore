package com.github.phiz71.vertx.oas3.petstore.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoHelper {
  
  private MongoHelper() {
  }
  
  public static MongoClient createMongoClient(Vertx vertx) {
    JsonObject config = vertx.getOrCreateContext().config();
    
    String uri = config.getString("mongo_uri");
    if (uri == null) {
      uri = "mongodb://localhost:27017";
    }
    String db = config.getString("mongo_db");
    if (db == null) {
      db = "test";
    }
    
    JsonObject mongoconfig = new JsonObject()
      .put("connection_string", uri)
      .put("db_name", db);
    
    // Create the redis client
    return MongoClient.createShared(vertx, mongoconfig);
  }
  
}
