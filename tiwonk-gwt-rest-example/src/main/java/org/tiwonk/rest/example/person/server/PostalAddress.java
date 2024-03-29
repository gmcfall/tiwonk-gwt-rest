/*******************************************************************************
 * Copyright 2012 Gregory McFall
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.tiwonk.rest.example.person.server;

import org.codehaus.jackson.annotate.JsonProperty;


public class PostalAddress {
  private String street;
  private String city;
  private String state;
  private String zipcode;
  
  @JsonProperty public String getStreet() {
    return street;
  }
  @JsonProperty public void setStreet(String street) {
    this.street = street;
  }
  @JsonProperty public String getCity() {
    return city;
  }
  @JsonProperty public void setCity(String city) {
    this.city = city;
  }
  @JsonProperty public String getState() {
    return state;
  }
  @JsonProperty public void setState(String state) {
    this.state = state;
  }
  @JsonProperty public String getZipcode() {
    return zipcode;
  }
  @JsonProperty public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }
  
  
  
}
