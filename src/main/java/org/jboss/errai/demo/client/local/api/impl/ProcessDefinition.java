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

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.function.Function;
import org.jboss.errai.common.client.function.Optional;
import org.jboss.errai.ioc.client.QualifierUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class ProcessDefinition {

  static class Step {
    final String name;
    final Class<?> beanType;
    final Set<Annotation> qualifiers;

    Step(final String name, final Class<?> beanType, final Set<Annotation> qualifiers) {
      this.name = name;
      this.beanType = beanType;
      this.qualifiers = qualifiers;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof Step && ((Step) obj).name.equals(name);
    }
  }

  static class Link {
    final Step to, from;
    final Optional<Function<? super Object, ?>> transformer;
    Link(final Step from, final Step to, Optional<Function<? super Object, ?>> transformer) {
      this.from = from;
      this.to = to;
      this.transformer = transformer;
    }
  }

  static class Builder {

    class StepBuilder {
      private final String name;
      private Class<?> beanType;
      private Set<Annotation> qualifiers;
      StepBuilder(final String name) {
        this.name = name;
      }

      StepBuilder beanType(Class<?> type) {
        beanType = type;
        return this;
      }

      StepBuilder defaultQualifiers() {
        return qualifiers(QualifierUtil.DEFAULT_ANNOTATION);
      }

      StepBuilder qualifiers(Annotation... qualifiers) {
        this.qualifiers = new HashSet<>(asList(qualifiers));
        return this;
      }
    }

    class LinkBuilder {
      private final StepBuilder from, to;
      private Function<? super Object, ?> transformer;
      LinkBuilder(StepBuilder from, StepBuilder to) {
        this.from = from;
        this.to = to;
      }
      @SuppressWarnings("unchecked")
      LinkBuilder transformer(Function<?, ?> transformer) {
        this.transformer = (Function<? super Object, ?>) transformer;
        return this;
      }
    }

    public Builder(final String id) {
      this.id = id;
    }

    private final String id;
    private final Collection<StepBuilder> steps = new ArrayList<>();
    private final Collection<LinkBuilder> links = new ArrayList<>();

    StepBuilder step(String name) {
      StepBuilder builder = new StepBuilder(name);
      steps.add(builder);
      return builder;
    }

    LinkBuilder link(StepBuilder from, StepBuilder to) {
      LinkBuilder builder = new LinkBuilder(from, to);
      links.add(builder);
      return builder;
    }

    StepBuilder initialStep() {
      return step("init");
    }

    ProcessDefinition build() {
      final Map<String, Step> stepsByName = new HashMap<>();
      final Multimap<Step, Link> linksByStep = ArrayListMultimap.create();
      for (final StepBuilder stepBuilder : steps) {
        stepsByName.put(stepBuilder.name, new Step(stepBuilder.name, stepBuilder.beanType, stepBuilder.qualifiers));
      }
      for (final LinkBuilder linkBuilder : links) {
        final Step from = stepsByName.get(linkBuilder.from.name),
                   to = stepsByName.get(linkBuilder.to.name);
        Link link = new Link(from, to, Optional.ofNullable(linkBuilder.transformer));
        linksByStep.put(from, link);
      }

      return new ProcessDefinition(id, stepsByName, linksByStep);
    }

  }
  static Builder builder(String id) {
    return new Builder(id);
  }

  ProcessDefinition(String id, Map<String, Step> stepsByName, Multimap<Step, Link> transitionsByStep) {
    this.id = id;
    this.stepsByName = stepsByName;
    this.transitionsByStep = transitionsByStep;
  }

  String id;
  Multimap<Step, Link> transitionsByStep;
  Map<String, Step> stepsByName;
}