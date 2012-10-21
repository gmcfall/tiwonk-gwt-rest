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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class PersonEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {
    
    savePerson();
  }
  
  private void savePerson() {
    display("Saving person resource...");
    
    Person person = Person.create();
    person.setEmailAddress("jsmith@example.com");
    PostalAddress address = PostalAddress.create();
    address.setStreet("101 Main Street");
    address.setCity("Springfield");
    address.setState("KS");
    address.setZipcode("66084");
    person.setPostalAddress(address);
    
    Phone homePhone = Phone.create();
    homePhone.setPhoneType(PhoneType.HOME);
    homePhone.setPhoneNumber("555-123-4567");
    person.addPhone(homePhone);
    
    Phone mobile = Phone.create();
    mobile.setPhoneType(PhoneType.MOBILE);
    mobile.setPhoneNumber("555-987-6543");
    person.addPhone(mobile);
    
    

    String collectionURL = GWT.getHostPageBaseURL() + "resources/persons/";
    PersonClientResource resource = GWT.create(PersonClientResource.class);
    resource.setResourceURL(collectionURL);
    
    resource.create(person, new AsyncCallback<Person>() {
      
      @Override
      public void onSuccess(Person person) {
        String location = person.getUri();
        display("Person URI = " + location);
        loadPerson(location);
      }
      
      @Override
      public void onFailure(Throwable caught) {
        display("Failed to save person resource.");
      }
    });
    
  }
  
  private void loadPerson(String uri) {
    display("Loading person resource...");
    
    PersonClientResource resource = GWT.create(PersonClientResource.class);
    resource.setResourceURL(uri);
    
    resource.retrieve(new AsyncCallback<Person>() {

      @Override
      public void onFailure(Throwable caught) {
        display("Failed to load person resource.");
      }

      @Override
      public void onSuccess(Person person) {
        display("email: " + person.getEmailAddress());
        display("street: " + person.getPostalAddress().getStreet());
        display("city: " + person.getPostalAddress().getCity());
        display("state: " + person.getPostalAddress().getState());
        display("zipcode: " + person.getPostalAddress().getZipcode());
        JsArray<Phone> array = person.getPhone();
        if (array != null) {
          for (int i=0; i<array.length(); i++) {
            Phone phone = array.get(i);
            display(phone.getPhoneType().name() + " phone: " + phone.getPhoneNumber());
          }
        }
      }
    });
  }

  private void display(String text) {
    RootPanel.get().add(new Label(text));
  }

}
