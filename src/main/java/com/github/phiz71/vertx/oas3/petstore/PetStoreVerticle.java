package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.Pet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A verticle setting and reading a value in Redis.
 */
public class PetStoreVerticle extends AbstractVerticle {
  public static final String PET_STORE_ADD_VALUE_ADDRESS = "petStore.addPet";
  public static final String PET_STORE_REMOVE_VALUE_ADDRESS = "petStore.removePet";
  public static final String PET_STORE_FIND_BY_ID_VALUE_ADDRESS = "petStore.findPetById";
  public static final String PET_STORE_FIND_VALUE_ADDRESS = "petStore.findPets";
  
  public static final String PET_STORE_COMMAND_ADD = "petToAdd";
  public static final String PET_STORE_COMMAND_REMOVE = "petIdToRemove";
  public static final String PET_STORE_COMMAND_ID_TO_FIND = "petIdToFind";
  public static final String PET_STORE_COMMAND_TAG_TO_FIND = "tagToFind";
  public static final String PET_STORE_COMMAND_LIMIT_TO_FIND = "limitToFind";
  
  static List<Pet> petStoreList = new ArrayList<>();
  
  final Handler<Message<JsonObject>> addHandler = jsonObjectMessage -> {
    Pet petToAdd = new Pet(jsonObjectMessage.body().getJsonObject(PET_STORE_COMMAND_ADD));
    
    if (!"ERROR".equalsIgnoreCase(petToAdd.getName())) {
      petStoreList.add(petToAdd);
      jsonObjectMessage.reply(null);
    } else {
      raiseError(jsonObjectMessage);
    }
  };
  
  final Handler<Message<JsonObject>> removeHandler = jsonObjectMessage -> {
    Long petIdToRemove = jsonObjectMessage.body().getLong(PET_STORE_COMMAND_REMOVE);
    
    if (petIdToRemove != -1) {
      List<Pet> newList = petStoreList.stream().filter(pet -> !pet.getId().equals(petIdToRemove)).collect(Collectors.toList());
      if (petStoreList.size() != newList.size()) {
        petStoreList = newList;
        jsonObjectMessage.reply(null);
      } else {
        jsonObjectMessage.fail(500, "pet not found :" + petIdToRemove);
      }
    } else {
      raiseError(jsonObjectMessage);
    }
    
  };
  
  final Handler<Message<JsonObject>> findByIdHandler = jsonObjectMessage -> {
    Long petIdToFind = jsonObjectMessage.body().getLong(PET_STORE_COMMAND_ID_TO_FIND);
    
    if (petIdToFind != -1) {
      List<Pet> foundPets = petStoreList.stream().filter(pet -> pet.getId().equals(petIdToFind)).collect(Collectors.toList());
      jsonObjectMessage.reply(new JsonArray(foundPets));
    } else {
      raiseError(jsonObjectMessage);
    }
  };
  
  final Handler<Message<JsonObject>> findHandler = jsonObjectMessage -> {
    JsonArray tags = jsonObjectMessage.body().getJsonArray(PET_STORE_COMMAND_TAG_TO_FIND);
    Integer limit = jsonObjectMessage.body().getInteger(PET_STORE_COMMAND_LIMIT_TO_FIND);
    
    if (limit != null && limit == -1) {
      raiseError(jsonObjectMessage);
    } else {
      Stream<Pet> stream = petStoreList.stream();
      List<Pet> foundItems = null;
      if (tags != null)
        stream = stream.filter(pet -> tags.contains(pet.getTag()));
      
      if (limit != null)
        stream = stream.limit(limit);
      
      foundItems = stream.collect(Collectors.toList());
      
      jsonObjectMessage.reply(new JsonArray(foundItems));
      
    }
  };
  
  @Override
  public void start() throws Exception {
    vertx.eventBus().<JsonObject>consumer(PET_STORE_ADD_VALUE_ADDRESS, addHandler);
    vertx.eventBus().<JsonObject>consumer(PET_STORE_REMOVE_VALUE_ADDRESS, removeHandler);
    vertx.eventBus().<JsonObject>consumer(PET_STORE_FIND_BY_ID_VALUE_ADDRESS, findByIdHandler);
    vertx.eventBus().<JsonObject>consumer(PET_STORE_FIND_VALUE_ADDRESS, findHandler);
    
  }
  
  private void raiseError(Message<JsonObject> jsonObjectMessage) {
    jsonObjectMessage.fail(500, "Error raised");
  }

}