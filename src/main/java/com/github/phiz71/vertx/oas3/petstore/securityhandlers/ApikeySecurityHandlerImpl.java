package com.github.phiz71.vertx.oas3.petstore.securityhandlers;

import io.vertx.ext.web.RoutingContext;

public class ApikeySecurityHandlerImpl implements ApikeySecurityHandler {
  
  @Override
  public void authenticate(RoutingContext routingContext, String serverToken) {
    if (!"0123-4567-89AB-CDEF".equals(serverToken))
      routingContext.response().setStatusCode(401).end();
    else
      routingContext.next();
  }
  
}