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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.demo.client.shared.CustomerFormModel;
import org.jboss.errai.ui.nav.client.local.Page;
import org.livespark.process.client.local.ProcessInput;
import org.livespark.process.client.local.ProcessOutput;

import com.google.gwt.dom.client.Element;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Page
public class CustomerFormPage implements IsElement {

  @Inject
  private ProcessInput<CustomerFormModel> input;

  @Inject
  private ProcessOutput<CustomerFormModel> output;

  @Inject
  private Div root;

  @Inject
  private CustomerView form;

  @Inject
  private Button submit;

  @PostConstruct
  private void init() {
    root.appendChild(form.getElement());
    root.appendChild(submit);
    form.setValue(input.get());
    submit.addEventListener("click", e -> submit(), false);
    submit.setTextContent("Submit");
  }

  private void submit() {
    output.submit(form.getValue());
  }

  @Override
  public HTMLElement getElement() {
    return root;
  }

  private static native HTMLElement asElement(Element element) /*-{
    return element;
  }-*/;

}