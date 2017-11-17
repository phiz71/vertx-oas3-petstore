package com.github.phiz71.vertx.oas3.petstore.securityhandlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class ApikeySecurityHandler implements Handler<RoutingContext> {
  
  public void handle(RoutingContext routingContext) {
    // Handle apikey security schema
    List<String> queryParams = routingContext.queryParam("server_token");
    if (queryParams.isEmpty() || !"0123-4567-89AB-CDEF".equals(queryParams.get(0)))
      routingContext.response().setStatusCode(401).end();
    else
      routingContext.next();
  }
  
}