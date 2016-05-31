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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.demo.client.shared.Customer;
import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.livespark.process.api.Command;
import org.livespark.process.api.CrudOperation;
import org.livespark.process.client.local.ProcessInput;
import org.livespark.process.client.local.ProcessOutput;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Page
public class CustomerListView implements IsElement {

  @Inject
  private ProcessInput<List<CustomerFormModel>> input;

  @Inject
  private ProcessOutput<Command<CrudOperation, CustomerFormModel>> create;

  @Inject
  @AutoBound
  private DataBinder<List<CustomerFormModel>> binder;

  @Inject
  @Bound
  private ListComponent<CustomerFormModel, CustomerView> customers;

  @Inject
  private Button createButton;

  @Inject
  private Div root;

  @PostConstruct
  private void init() {
    root.appendChild(customers.getElement());
    root.appendChild(createButton);

    createButton.addEventListener("click", e -> onCreate(), true);
    createButton.setTextContent("Create");
    customers.addComponentCreationHandler(view -> {
      view.setReadOnly(true);
      view.getElement().setOnclick(e -> onUpdate(view.getValue()));
    });

    binder.setModel(input.get());
  }

  private void onUpdate(final CustomerFormModel value) {
    create.submit(new Command<>(CrudOperation.UPDATE, value));
  }

  private void onCreate() {
    create.submit(new Command<>(CrudOperation.CREATE, new CustomerFormModel(new Customer())));
  }

  @Override
  public HTMLElement getElement() {
    return root;
  }

}
