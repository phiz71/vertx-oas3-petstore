package com.github.phiz71.vertx.oas3.petstore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pet extends NewPet {
  private static final String ID_KEY = "id";
  
  public Pet() {
  }
  
  public Pet(NewPet newPet) {
    super(newPet);
  }
  
  public Pet(JsonObject json) {
    super(json);
    this.put(ID_KEY, json.getLong(ID_KEY));
  }
  
  public Pet(Long id, String name, String tag) {
    super(name, tag);
    this.put(ID_KEY, id);
  }
  
  @JsonProperty("id")
  public Long getId() {
    return this.getLong(ID_KEY);
  }
  
  public Pet setId(Long id) {
    this.put(ID_KEY, id);
    return this;
  }
  
  @Override
  public Pet setName(String name) {
    super.setName(name);
    return this;
  }
  
  @Override
  public Pet setTag(String tag) {
    super.setTag(tag);
    return this;
  }
}
