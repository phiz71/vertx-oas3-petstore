package com.github.phiz71.vertx.oas3.petstore.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameter;
import io.vertx.ext.web.api.RequestParameters;

import java.util.List;

public interface FindPetsHandler extends Handler<RoutingContext> {
  
  default void handle(RoutingContext routingContext) {
    RequestParameters params = routingContext.get("parsedParameters");
    List<RequestParameter> tags = null;
    Integer limit = null;
    
    if (params.queryParametersNames().contains("tags"))
      tags = params.queryParameter("tags").getArray();
    if (params.queryParametersNames().contains("limit"))
      limit = params.queryParameter("limit").getInteger();
    
    
    findPets(routingContext, tags, limit);
  }
  
  public void findPets(RoutingContext routingContext, List<RequestParameter> tags, Integer limit);
}