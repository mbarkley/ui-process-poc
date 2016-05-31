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
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.livespark.process.api.Command;
import org.livespark.process.api.CrudOperation;
import org.livespark.process.api.ProcessFactory;
import org.livespark.process.api.Step;
import org.livespark.process.api.Unit;
import org.livespark.process.client.local.CDIStepFactory;

@EntryPoint
public class Config {

  @Inject
  private CDIStepFactory stepFactory;

  @Inject
  private ProcessFactory procFactory;

  @Inject
  private SimpleDataSource dataSource;

  @Inject
  private TransitionTo<CustomerListView> listTransition;

  @Inject
  private TransitionTo<CustomerFormPage> formTransition;

  @PostConstruct
  public void setup() {
    createAndRegisterSteps();
    createProcesses();
  }

  private void createProcesses() {
    final Step<List<CustomerFormModel>, Command<CrudOperation, CustomerFormModel>> list = procFactory.getStep("CustomerList");
    final Step<CustomerFormModel, CustomerFormModel> form = procFactory.getStep("CustomerForm");
    final Step<Unit, List<CustomerFormModel>> loader = procFactory.getStep("CustomerListLoader");
    final Step<CustomerFormModel, Unit> saver = procFactory.getStep("CustomerSaver");
    final Step<CustomerFormModel, Unit> updater = procFactory.getStep("CustomerUpdater");

    procFactory.registerProcess("Main",
      procFactory
      .buildProcessFrom(loader)
      .andThen(list)
      .transition(command -> {
        switch (command.commandType) {
        case CREATE:
          return procFactory
                  .<CustomerFormModel, CustomerFormModel>buildProcessFrom(form)
                  .<Unit>andThen(saver)
                  .<Unit>butFirst(ignore -> command.value)
                  .<Unit>andThen(() -> procFactory.getProcessFlow("Main"));
        case UPDATE:
          return procFactory
                  .<CustomerFormModel, CustomerFormModel>buildProcessFrom(form)
                  .<Unit>andThen(updater)
                  .<Unit>butFirst(ignore -> command.value)
                  .<Unit>andThen(() -> procFactory.getProcessFlow("Main"));
        default:
          throw new RuntimeException();
        }
      }));
  }

  private void createAndRegisterSteps() {
    final Step<List<CustomerFormModel>, Command<CrudOperation, CustomerFormModel>> listStep = stepFactory.createCdiStep(() -> {
      listTransition.go();
    }, () -> {}, "CustomerList", false);

    final Step<CustomerFormModel, CustomerFormModel> formStep = stepFactory.createCdiStep(() -> {
      formTransition.go();
    }, () -> {}, "CustomerForm", false);

    procFactory.registerStep(listStep.getName(), listStep);
    procFactory.registerStep(formStep.getName(), formStep);
    procFactory.registerStep("CustomerListLoader", new Step<Unit, List<CustomerFormModel>>() {

      @Override
      public boolean hasUnitInput() {
        return true;
      }

      @Override
      public void execute(final Unit input, final Consumer<List<CustomerFormModel>> callback) {
        callback.accept(dataSource.getCustomers());
      }

      @Override
      public String getName() {
        return "CustomerListLoader";
      }
    });
    procFactory.registerStep("CustomerSaver", new Step<CustomerFormModel, Unit>() {

      @Override
      public boolean hasUnitInput() {
        return false;
      }

      @Override
      public void execute(final CustomerFormModel input, final Consumer<Unit> callback) {
        dataSource.acceptNewCustomer(input);
        callback.accept(Unit.INSTANCE);
      }

      @Override
      public String getName() {
        return "CustomerSaver";
      }
    });
    procFactory.registerStep("CustomerUpdater", new Step<CustomerFormModel, Unit>() {

      @Override
      public boolean hasUnitInput() {
        return false;
      }

      @Override
      public void execute(final CustomerFormModel input, final Consumer<Unit> callback) {
        final List<CustomerFormModel> customers = dataSource.getCustomers();
        int i;
        for (i = 0; i < customers.size(); i++) {
          if (customers.get(i).getCustomer().getId().equals(input.getCustomer().getId())) {
            customers.set(i, input);
            break;
          }
        }
        if (i == customers.size()) {
          throw new RuntimeException("Customer could not be updated. No customer with id " + input.getCustomer().getId());
        }
        callback.accept(Unit.INSTANCE);
      }

      @Override
      public String getName() {
        return "CustomerUpdater";
      }
    });
  }

}
