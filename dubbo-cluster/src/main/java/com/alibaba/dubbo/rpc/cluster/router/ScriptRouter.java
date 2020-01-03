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
package com.alibaba.dubbo.rpc.cluster.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcConstants;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;

/**
 * ScriptRouter
 * 
 * @author william.liangf
 */
public class ScriptRouter implements Router {

    private static final Logger logger = LoggerFactory.getLogger(ScriptRouter.class);
    
    private static final Map<String, ScriptEngine> engines = new ConcurrentHashMap<String, ScriptEngine>();
    
    private final ScriptEngine engine;
    
    private final String rule;
    
    public ScriptRouter(URL url) {
        String type = url.getParameter(RpcConstants.TYPE_KEY);
        String rule = url.getParameterAndDecoded(RpcConstants.RULE_KEY);
        if (type == null || type.length() == 0){
            type = RpcConstants.DEFAULT_SCRIPT_TYPE_KEY;
        }
        if (rule == null || rule.length() == 0){
            throw new IllegalStateException(new IllegalStateException("route rule can not be empty. rule:" + rule));
        }
        ScriptEngine engine = engines.get(type);
        if (engine == null){
            engine = new ScriptEngineManager().getEngineByName(type);
            if (engine == null) {
                throw new IllegalStateException(new IllegalStateException("Unsupported route rule type: " + type + ", rule: " + rule));
            }
            engines.put(type, engine);
        }
        this.engine = engine;
        this.rule = rule;
    }

    @SuppressWarnings("unchecked")
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, Invocation invocation) throws RpcException {
        try {
            List<Invoker<T>> invokersCopy = new ArrayList<Invoker<T>>(invokers);
            Compilable compilable = (Compilable) engine;
            Bindings bindings = engine.createBindings();
            bindings.put("invokers", invokersCopy);
            bindings.put("invocation", invocation);
            bindings.put("context", RpcContext.getContext());
            CompiledScript function = compilable.compile(rule);
            Object obj = function.eval(bindings);
            if (obj instanceof Invoker[]) {
                invokersCopy = Arrays.asList((Invoker<T>[]) obj);
            } else if (obj instanceof Object[]) {
                invokersCopy = new ArrayList<Invoker<T>>();
                for (Object inv : (Object[]) obj) {
                    invokersCopy.add((Invoker<T>)inv);
                }
            } else {
                invokersCopy = (List<Invoker<T>>) obj;
            }
            return invokersCopy;
        } catch (ScriptException e) {
            //fail then ignore rule .invokers.
            logger.error("route error , rule has been ignored .rule :"+ rule + ",invocation:" + invocation + ",url :"+(RpcContext.getContext().getInvoker() == null ? "" : RpcContext.getContext().getInvoker().getUrl()), e);
            return invokers;
        }
    }

}