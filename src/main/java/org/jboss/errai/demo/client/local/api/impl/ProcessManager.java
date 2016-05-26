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


package org.jboss.errai.demo.client.local.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.function.Function;
import org.jboss.errai.demo.client.local.CustomerFormPage;
import org.jboss.errai.demo.client.local.CustomerListView;
import org.jboss.errai.demo.client.local.api.ProcessInput;
import org.jboss.errai.demo.client.local.api.ProcessInvoker;
import org.jboss.errai.demo.client.local.api.ProcessOutput;
import org.jboss.errai.demo.client.local.api.impl.ProcessDefinition.Builder;
import org.jboss.errai.demo.client.local.api.impl.ProcessDefinition.Builder.StepBuilder;
import org.jboss.errai.demo.client.local.api.impl.ProcessDefinition.Link;
import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ui.nav.client.local.Navigation;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@ApplicationScoped
public class ProcessManager {

  private final Map<String, ProcessDefinition> processesById = new HashMap<>();
  private final Deque<ProcessState> active = new LinkedList<>();

  @Inject
  private Navigation nav;

  {
    Builder builder = ProcessDefinition.builder("CustomerListView");
    StepBuilder init = builder.initialStep();
    StepBuilder list = builder.step("list")
                              .beanType(CustomerListView.class)
                              .defaultQualifiers();
    StepBuilder create = builder.step("create")
                                .beanType(CustomerFormPage.class)
                                .defaultQualifiers();

    builder.link(init, list);
    builder.link(list, create);
    builder.link(create, list)
           .transformer((Function<CustomerFormModel, List<CustomerFormModel>>) model -> new ArrayList<>(Arrays.asList(model)));

    ProcessDefinition procDef = builder.build();
    processesById.put(procDef.id, procDef);
  }

  @Produces @ApplicationScoped
  public ProcessInvoker createInvoker() {
    return (processId, input) -> {
      final ProcessDefinition procDef = Assert.notNull("No process with given id, " + processId, processesById.get(processId));
      startProcess(procDef, input);
    };
  }

  @SuppressWarnings("unchecked")
  @Produces @ApplicationScoped
  public <T> ProcessInput<T> createInput() {
    return () -> (T) produceInput();
  }

  @Produces @ApplicationScoped
  public <T> ProcessOutput<T> createOutput() {
    return output -> consumeOutput(output);
  }

  private void consumeOutput(Object output) {
    final ProcessState activeState = assertActiveProcessState();
    activeState.currentInput = output;
    transition();
  }

  private void transition() {
    final ProcessState procState = assertActiveProcessState();
    final Link toNext = procState.process.transitionsByStep.get(procState.currentStep).iterator().next();
    procState.currentStep = toNext.to;
    procState.currentTransformer = toNext.transformer;
    procState.currentInput = procState.currentTransformer.map(f -> (Object) f.apply(procState.currentInput))
            .orElseGet(() -> procState.currentInput);

    nav.goTo(procState.currentStep.beanType, ImmutableMultimap.of());
  }

  private ProcessState assertActiveProcessState() {
    if (active.isEmpty()) {
      throw new RuntimeException("No active process.");
    }

    return active.peek();
  }

  private Object produceInput() {
    return active.peek().currentInput;
  }

  private void startProcess(ProcessDefinition process, Object input) {
    ProcessState procState = new ProcessState(process);
    active.push(procState);
    procState.currentInput = input;
    procState.currentStep = process.stepsByName.get("init");
    transition();
  }

}
