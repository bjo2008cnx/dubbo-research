/**
 * Project: dubbo-registry
 *
 * File Created at 2011-10-13 $Id$
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited. All rights reserved.
 *
 * This software is the confidential and proprietary information of Alibaba Company. ("Confidential Information").  You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * Alibaba.com.
 */
package com.alibaba.dubbo.registry.support;

import java.io.IOException;
import java.net.ServerSocket;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.ExtensionLoader;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.registry.RegistryService;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcConstants;
import com.alibaba.dubbo.rpc.proxy.ProxyFactory;

/**
 * SimpleRegistryExporter
 *
 * @author william.liangf
 */
public class SimpleRegistryExporter {

  private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

  private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

  public synchronized static Exporter<RegistryService> exportIfAbsent(int port) {
    try {
      new ServerSocket(port).close();
      return export(port);
    } catch (IOException e) {
      return null;
    }
  }

  public static Exporter<RegistryService> export(int port) {
    RegistryService registryService = new SimpleRegistryService();
    return protocol.export(proxyFactory.getInvoker(registryService, RegistryService.class,
        new URL("dubbo", NetUtils.getLocalHost(), port, RegistryService.class.getName())
            .setPath(RegistryService.class.getName())
            .addParameter(Constants.INTERFACE_KEY, RegistryService.class.getName())
            .addParameter(RpcConstants.CLUSTER_STICKY_KEY, "true")
            .addParameter(RpcConstants.CALLBACK_INSTANCES_LIMIT_KEY, "1000")
            .addParameter("ondisconnect", "disconnect")
            .addParameter("subscribe.1.callback", "true")
            .addParameter("unsubscribe.1.callback", "false")));
  }

}
