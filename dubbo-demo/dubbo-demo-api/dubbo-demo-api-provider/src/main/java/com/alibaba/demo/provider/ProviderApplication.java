/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.demo.provider;

import com.alibaba.demo.DemoService;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class ProviderApplication {

  /**
   * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before launch the application
   */
  public static void main(String[] args) throws Exception {
    ServiceConfig<DemoServiceImpl> service = new ServiceConfig();
    service.setApplication(new ApplicationConfig("dubbo-demo-api-provider"));
    service.setRegistry(new RegistryConfig("multicast://224.5.6.7:1234"));
    service.setInterface(DemoService.class);
    service.setRef(new DemoServiceImpl());
    service.export();
    System.in.read();
  }
}
