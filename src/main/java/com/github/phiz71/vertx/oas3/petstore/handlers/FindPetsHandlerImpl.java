package com.github.phiz71.vertx.oas3.petstore.handlers;

import com.github.phiz71.vertx.oas3.petstore.MongoClientVerticle;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameter;

import java.util.List;
import java.util.stream.Collectors;

public class FindPetsHandlerImpl implements FindPetsHandler {
  
  @Override
  public void findPets(RoutingContext routingContext, List<RequestParameter> tags, Integer limit) {
    
    JsonObject valueToFind = new JsonObject();
    
    if (tags != null) {
      JsonArray orCondition = new JsonArray(tags.stream().map(tag -> new JsonObject().put("tag", tag.getString())).collect(Collectors.toList()));
      valueToFind.put("$or", orCondition);
    }
    
    // Handle findPets
    JsonObject mongoCommand = new JsonObject().put("key", "pets").put("value", valueToFind);
    
    if (limit != null) {
      mongoCommand.put(MongoClientVerticle.MONGO_COMMAND_ID_LIMIT, limit);
    }
    
    routingContext.vertx().eventBus().<JsonArray>send(MongoClientVerticle.MONGO_FIND_VALUE_ADDRESS, mongoCommand, r -> {
      if (r.succeeded()) {
        JsonArray foundPets = r.result().body();
        if (0 == foundPets.size())
          routingContext.response().setStatusCode(200).setStatusMessage("No pet found").end();
        else
          routingContext.response().setStatusCode(200).end(foundPets.encodePrettily());
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
  }
  
}