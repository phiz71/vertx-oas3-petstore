package com.github.phiz71.vertx.oas3.petstore.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameters;

public interface DeletePetHandler extends Handler<RoutingContext> {
  
  default void handle(RoutingContext routingContext) {
    RequestParameters params = routingContext.get("parsedParameters");
    // Handle deletePet
    Long id = params.pathParameter("id").getLong();
    deletePet(routingContext, id);
  }
  
  public abstract void deletePet(RoutingContext routingContext, Long id);
  
}