package com.github.phiz71.vertx.oas3.petstore.handlers.pets;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameters;

public interface FindPetByIdHandler extends Handler<RoutingContext> {
  
  default void handle(RoutingContext routingContext) {
    RequestParameters params = routingContext.get("parsedParameters");
    // Handle find pet by id
    Long id = params.pathParameter("id").getLong();
    
    findPetById(routingContext, id);
  }
  
  public abstract void findPetById(RoutingContext routingContext, Long id);
  
  
}