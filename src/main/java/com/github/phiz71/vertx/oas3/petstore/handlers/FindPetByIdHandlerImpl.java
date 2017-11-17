package com.github.phiz71.vertx.oas3.petstore.handlers;

import com.github.phiz71.vertx.oas3.petstore.MongoClientVerticle;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class FindPetByIdHandlerImpl implements FindPetByIdHandler {
  
  @Override
  public void findPetById(RoutingContext routingContext, Long id) {
    JsonObject mongoCommand = new JsonObject().put("key", "pets").put("value", new JsonObject().put("id", id));
    
    routingContext.vertx().eventBus().<JsonArray>send(MongoClientVerticle.MONGO_FIND_VALUE_ADDRESS, mongoCommand, r -> {
      if (r.succeeded()) {
        JsonArray foundPets = r.result().body();
        if (0 == foundPets.size())
          routingContext.response().setStatusCode(404).end(new Error(404, "No pet found with the id : " + id).encodePrettily());
        else if (1 == foundPets.size())
          routingContext.response().setStatusCode(200).end(foundPets.getJsonObject(0).encodePrettily());
        else
          routingContext.response().setStatusCode(500).end(new Error(500, "More than one item with this id : " + id).encodePrettily());
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
  }
  
}