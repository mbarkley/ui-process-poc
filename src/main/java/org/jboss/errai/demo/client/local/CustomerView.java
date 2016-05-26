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

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.databinding.client.BoundUtil;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.demo.client.shared.Customer;
import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ui.shared.TemplateWidget;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.TakesValue;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class CustomerView implements TakesValue<CustomerFormModel>, IsElement {

  @Inject
  @AutoBound
  private DataBinder<CustomerFormModel> binder;

  @Inject
  @Bound(property = "customer.name")
  private TextInput customerName;

  @PostConstruct
  private void init() {
    Element e = BoundUtil.asElement(customerName);
    new TemplateWidget(e, Arrays.asList(ElementWrapperWidget.getWidget(e))).onAttach();
  }

  public void setReadOnly(boolean readOnly) {
    customerName.setReadOnly(readOnly);
  }

  @Override
  public void setValue(CustomerFormModel model) {
    if (model.getCustomer() == null)
      model.setCustomer(new Customer());
    binder.setModel(model);
  }

  @Override
  public CustomerFormModel getValue() {
    return binder.getModel();
  }

  @Override
  public HTMLElement getElement() {
    return customerName;
  }

}
