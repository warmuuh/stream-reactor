/*
 * Copyright 2017-2024 Lenses.io Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lenses.streamreactor.common.config.base.intf;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.ConfigException;

/**
 * Picks out the settings required from KCQL
 */
public abstract class Converter<S, T> {

  public List<T> convertAll(List<S> source) throws ConfigException {
    return source.stream().map(this::convert).collect(Collectors.toList());
  }

  protected abstract T convert(S source) throws ConfigException;
}
