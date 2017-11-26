package com.github.phiz71.vertx.oas3.petstore.handlers.pets;

import com.github.phiz71.vertx.oas3.petstore.model.NewPet;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameters;

public interface AddPetHandler extends Handler<RoutingContext> {
  
  default void handle(RoutingContext routingContext) {
    RequestParameters params = routingContext.get("parsedParameters");
    // Handle addPet
    NewPet newPet = new NewPet(params.body().getJsonObject());
    addPet(routingContext, newPet);
  }
  
  public void addPet(RoutingContext routingContext, NewPet newPet);
  
  
}