package com.github.phiz71.vertx.oas3.petstore.securityhandlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public interface ApikeySecurityHandler extends Handler<RoutingContext> {
  
  default void handle(RoutingContext routingContext) {
    // Handle apikey security schema
    List<String> queryParams = routingContext.queryParam("server_token");
    if (queryParams.isEmpty()) {
      routingContext.response().setStatusCode(401).end();
    } else {
      String serverToken = queryParams.get(0);
      authenticate(routingContext, serverToken);
    }
  }
  
  public void authenticate(RoutingContext routingContext, String serverToken);
}