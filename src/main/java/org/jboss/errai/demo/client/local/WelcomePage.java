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
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.livespark.process.api.ProcessExecutor;
import org.livespark.process.api.ProcessFactory;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Page(role = DefaultPage.class)
public class WelcomePage implements IsElement {

  @Inject
  private Div root;

  @Inject
  private Button start;

  @Inject
  private ProcessExecutor executor;

  @Inject
  private ProcessFactory factory;

  @PostConstruct
  private void init() {
    start.setOnclick(e -> {
      executor.execute(factory.getProcessFlow("Main"));
    });
    start.setTextContent("Start");
    root.appendChild(start);
  }

  @Override
  public HTMLElement getElement() {
    return root;
  }

}
