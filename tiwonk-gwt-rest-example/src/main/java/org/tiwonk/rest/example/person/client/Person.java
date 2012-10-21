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
import com.google.gwt.core.client.JsArray;

public class Person extends JavaScriptObject  {

  protected Person() {}
  
  public static Person create() {
    return JavaScriptObject.createObject().cast();
  }
  
  public final native String getUri() /*-{
    return this["@id"];
  }-*/;

  public final native void setUri(String uri) /*-{
    this["@id"] = uri;
  }-*/;

  public final native String getEmailAddress() /*-{
    return this.emailAddress;
  }-*/;

  public final native void setEmailAddress(String emailAddress) /*-{
    this.emailAddress = emailAddress;
  }-*/;

  public final native PostalAddress getPostalAddress() /*-{
    return this.postalAddress;
  }-*/;

  public final native void setPostalAddress(PostalAddress postalAddress) /*-{
    this.postalAddress = postalAddress;
  }-*/;
  
  public final native JsArray<Phone> getPhone() /*-{
    return this.phone;
  }-*/;
  
  public final native void addPhone(Phone phone) /*-{
    if (!this.phone) {
      this.phone = new Array();
    }
    this.phone[this.phone.length] = phone;
  }-*/;
    
}
