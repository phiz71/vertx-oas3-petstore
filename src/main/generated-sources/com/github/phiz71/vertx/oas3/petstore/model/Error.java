package com.github.phiz71.vertx.oas3.petstore.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error extends JsonObject {
  private static final String CODE_KEY = "code";
  private static final String MESSAGE_KEY = "message";
  
  public Error() {
  }
  
  public Error(JsonObject json) {
    this.put(CODE_KEY, json.getInteger(CODE_KEY));
    this.put(MESSAGE_KEY, json.getString(MESSAGE_KEY));
  }
  
  public Error(Integer code, String message) {
    this.put(CODE_KEY, code);
    this.put(MESSAGE_KEY, message);
  }
  
  @JsonProperty("code")
  public Integer getCode() {
    return this.getInteger(CODE_KEY);
  }
  
  public Error setCode(Integer code) {
    this.put(CODE_KEY, code);
    return this;
  }
  
  @JsonProperty("message")
  public String getMessage() {
    return this.getString(MESSAGE_KEY);
  }
  
  public Error setMessage(String message) {
    this.put(MESSAGE_KEY, message);
    return this;
  }
  
}
