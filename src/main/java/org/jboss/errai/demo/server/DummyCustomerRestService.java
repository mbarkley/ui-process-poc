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


package org.jboss.errai.demo.server;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.demo.client.shared.Customer;
import org.jboss.errai.demo.client.shared.CustomerRestService;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@ApplicationScoped
public class DummyCustomerRestService implements CustomerRestService {

  @Override
  public Customer create(Customer model) {
    return model;
  }

  @Override
  public List<Customer> load() {
    return Collections.emptyList();
  }

  @Override
  public Boolean update(Customer model) {
    return true;
  }

  @Override
  public Boolean delete(Customer model) {
    return true;
  }

}
