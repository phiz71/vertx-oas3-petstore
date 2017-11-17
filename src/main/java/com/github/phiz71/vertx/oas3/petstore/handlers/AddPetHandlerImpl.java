package com.github.phiz71.vertx.oas3.petstore.handlers;

import com.github.phiz71.vertx.oas3.petstore.MongoClientVerticle;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import com.github.phiz71.vertx.oas3.petstore.model.NewPet;
import com.github.phiz71.vertx.oas3.petstore.model.Pet;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AddPetHandlerImpl implements AddPetHandler {
  
  @Override
  public void addPet(RoutingContext routingContext, NewPet newPet) {
    
    long newId = System.currentTimeMillis();
    Pet pet = new Pet(newPet);
    pet.setId(newId);
    JsonObject mongoCommand = new JsonObject().put("key", "pets").put("value", pet);
    
    routingContext.vertx().eventBus().send(MongoClientVerticle.MONGO_ADD_VALUE_ADDRESS, mongoCommand, r -> {
      if (r.succeeded()) {
        routingContext.response().setStatusCode(200).end(newPet.encodePrettily());
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
    
  }
  
}