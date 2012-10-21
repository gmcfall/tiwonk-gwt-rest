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
package org.tiwonk.rest.example.person.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PostalAddress extends JavaScriptObject {
  
  protected PostalAddress() {}
  
  public static PostalAddress create() {
    return JavaScriptObject.createObject().cast();
  }
  
  public final native String getStreet() /*-{
    return this.street;
  }-*/;
  
  public final native void setStreet(String street) /*-{
    this.street = street;
  }-*/;

  public final native String getCity() /*-{
    return this.city;
  }-*/;

  public final native void setCity(String city) /*-{
    this.city = city;
  }-*/;
  
  public final native String getState() /*-{
    return this.state;
  }-*/;

  public final native void setState(String state) /*-{
    this.state = state;
  }-*/;
  
  public final native String getZipcode() /*-{
    return this.zipcode;
  }-*/;

  public final native void setZipcode(String zipcode) /*-{
    this.zipcode = zipcode;
  }-*/;
  
}
