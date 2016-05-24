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


package org.jboss.errai.demo.client.local;

import static java.util.Collections.singletonList;

import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.demo.client.shared.Customer;
import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.livespark.formmodeler.rendering.client.view.FormView;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class CustomerView extends FormView<CustomerFormModel> implements TakesValue<CustomerFormModel> {

  @Inject
  @Bound(property = "customer.name")
  @DataField
  private TextBox customerName;

  @Override
  protected void initForm() {
    validator.registerInput("customerName", customerName);
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    customerName.setReadOnly(readOnly);
  }

  @Override
  protected int getEntitiesCount() {
    return 1;
  }

  @Override
  protected List getEntities() {
    return singletonList(getModel().getCustomer());
  }

  @Override
  protected void initEntities() {
    if (getModel().getCustomer() == null) {
      getModel().setCustomer(new Customer());
    }
  }

  @Override
  public boolean doExtraValidations() {
    return true;
  }

  @Override
  public void beforeDisplay() {
  }

  @Override
  public void setValue(CustomerFormModel value) {
    setModel(value);
  }

  @Override
  public CustomerFormModel getValue() {
    return getModel();
  }

}
