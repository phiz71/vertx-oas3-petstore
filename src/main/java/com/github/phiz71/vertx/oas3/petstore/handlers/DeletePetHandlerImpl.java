package com.github.phiz71.vertx.oas3.petstore.handlers;

import com.github.phiz71.vertx.oas3.petstore.MongoClientVerticle;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class DeletePetHandlerImpl implements DeletePetHandler {
  
  @Override
  public void deletePet(RoutingContext routingContext, Long id) {
    JsonObject mongoCommand = new JsonObject().put("key", "pets").put("value", new JsonObject().put("id", id));
    
    routingContext.vertx().eventBus().send(MongoClientVerticle.MONGO_REMOVE_VALUE_ADDRESS, mongoCommand, r -> {
      if (r.succeeded()) {
        routingContext.response().setStatusCode(204).end();
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
  }
  
}