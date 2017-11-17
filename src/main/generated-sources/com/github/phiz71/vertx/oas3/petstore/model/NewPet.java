package com.github.phiz71.vertx.oas3.petstore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPet extends JsonObject {
  private static final String NAME_KEY = "name";
  private static final String TAG_KEY = "tag";
  
  public NewPet() {
  }
  
  public NewPet(JsonObject json) {
    this.put(NAME_KEY, json.getString(NAME_KEY));
    this.put(TAG_KEY, json.getString(TAG_KEY));
  }
  
  public NewPet(String name, String tag) {
    this.put(NAME_KEY, name);
    this.put(TAG_KEY, tag);
  }
  
  @JsonProperty("name")
  public String getName() {
    return this.getString(NAME_KEY);
  }
  
  public NewPet setName(String name) {
    this.put(NAME_KEY, name);
    return this;
  }
  
  @JsonProperty("tag")
  public String getTag() {
    return this.getString(TAG_KEY);
  }
  
  public NewPet setTag(String tag) {
    this.put(TAG_KEY, tag);
    return this;
  }
  
}
