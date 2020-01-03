/*
 * Copyright 1999-2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.remoting.transport.support.handler;

import com.alibaba.dubbo.common.Extension;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.ChannelHandlerWrapper;

/**
 * connect disconnect 保证顺序.
 *
 * @author chao.liuc
 */
@Extension(ConnectionOrderedChannelHandlerWrapper.NAME)
public class ConnectionOrderedChannelHandlerWrapper implements ChannelHandlerWrapper {

  public static final String NAME = "connection";

  public ChannelHandler wrap(ChannelHandler handler, URL url) {
    return new ConnectionOrderedChannelHandler(handler, url);
  }

}