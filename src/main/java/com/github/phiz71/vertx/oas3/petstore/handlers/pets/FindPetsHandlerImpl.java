package com.github.phiz71.vertx.oas3.petstore.handlers.pets;

import com.github.phiz71.vertx.oas3.petstore.PetStoreVerticle;
import com.github.phiz71.vertx.oas3.petstore.handlers.pets.FindPetsHandler;
import com.github.phiz71.vertx.oas3.petstore.model.Error;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.RequestParameter;

import java.util.List;
import java.util.stream.Collectors;

public class FindPetsHandlerImpl implements FindPetsHandler {
  
  @Override
  public void findPets(RoutingContext routingContext, List<RequestParameter> tags, Integer limit) {
    
    // Handle findPets
    JsonObject command = new JsonObject();
    
    if (limit != null) {
      command.put(PetStoreVerticle.PET_STORE_COMMAND_LIMIT_TO_FIND, limit);
    }
  
    if (tags != null) {
      JsonArray tagsArray = new JsonArray(tags.stream().map(RequestParameter::getString).collect(Collectors.toList()));
      command.put(PetStoreVerticle.PET_STORE_COMMAND_TAG_TO_FIND, tagsArray);
    }
    
    routingContext.vertx().eventBus().<JsonArray>send(PetStoreVerticle.PET_STORE_FIND_VALUE_ADDRESS, command, r -> {
      if (r.succeeded()) {
        JsonArray foundPets = r.result().body();
        if (0 == foundPets.size())
          routingContext.response().setStatusCode(200).setStatusMessage("No pet found").end();
        else
          routingContext.response().setStatusCode(200).end(foundPets.encodePrettily());
      } else {
        routingContext.response().setStatusCode(500).end(new Error(500, r.cause().getMessage()).encodePrettily());
      }
    });
  }
  
}