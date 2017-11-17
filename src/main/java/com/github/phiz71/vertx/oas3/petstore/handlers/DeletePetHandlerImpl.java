package com.github.phiz71.vertx.oas3.petstore.handlers;

import com.github.phiz71.vertx.oas3.petstore.PetStoreVerticle;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class DeletePetHandlerImpl implements DeletePetHandler {
  
  @Override
  public void deletePet(RoutingContext routingContext, Long id) {
    JsonObject command = new JsonObject().put(PetStoreVerticle.PET_STORE_COMMAND_REMOVE, id);
    
    routingContext.vertx().eventBus().send(PetStoreVerticle.PET_STORE_REMOVE_VALUE_ADDRESS, command, r -> {
      if (r.succeeded()) {
        routingContext.response().setStatusCode(204).end();
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
  }
  
}