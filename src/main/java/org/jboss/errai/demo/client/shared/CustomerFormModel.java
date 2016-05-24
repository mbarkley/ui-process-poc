/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jboss.errai.demo.client.shared;

import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.livespark.formmodeler.rendering.client.shared.FormModel;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Portable
@Bindable
@Named("CustomerFormModel")
public class CustomerFormModel extends FormModel {

  @Valid
  private Customer customer;

  public CustomerFormModel(Customer model) {
    this.customer = model;
  }

  public CustomerFormModel() {
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  @Override
  public List<Object> getDataModels() {
    return Collections.singletonList(customer);
  }

}
