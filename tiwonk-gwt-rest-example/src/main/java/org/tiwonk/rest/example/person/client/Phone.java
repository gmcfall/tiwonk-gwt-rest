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

public class Phone extends JavaScriptObject {
  
  protected Phone() {}
  
  public static Phone create() {
    return JavaScriptObject.createObject().cast();
  }
  
  public final native String getPhoneNumber() /*-{
    return this.phoneNumber;
  }-*/;
  
  public final native void setPhoneNumber(String phoneNumber) /*-{
    this.phoneNumber = phoneNumber;
  }-*/;
  
  public final PhoneType getPhoneType() {
    String value = getPhoneTypeName();
    return PhoneType.valueOf(PhoneType.class, value);
  }
  
  public final void setPhoneType(PhoneType type) {
    setPhoneTypeName(type.name());
  }
  
  private final native String getPhoneTypeName() /*-{
    return this.phoneType;
  }-*/;
  
  private final native void setPhoneTypeName(String value) /*-{
    this.phoneType = value;
  }-*/;
  

}
