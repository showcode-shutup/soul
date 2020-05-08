/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.dromara.soul.client.springmvc.spring;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.dromara.soul.client.springmvc.config.SoulHttpConfig;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * ApplicationStartListener.
 * [bins-comments: 实现了ApplicationListener接口，当ApplicationContext发布ApplicationEvent时，ApplicationListener Bean将自动被触发。这种事件机制都必须需要程序显示的触发。]
 * [WebServerInitializedEvent的含义是"在刷新应用程序上下文和Web服务器已就绪" =》项目启动时执行该类中方法往zk上注册客户自己机器的ip地址和端口号到zk的节点上]
 *
 * @author xiaoyu
 */
@Slf4j
public class ApplicationStartListener implements ApplicationListener<WebServerInitializedEvent> {

    private static final String ROOT = "/soul/register";

    private SoulHttpConfig soulHttpConfig;

    public ApplicationStartListener(final SoulHttpConfig soulHttpConfig) {
        this.soulHttpConfig = soulHttpConfig;
    }

    @Override
    public void onApplicationEvent(@NonNull final WebServerInitializedEvent event) {
        String register = System.getProperty("soul.register", "true");
        if (!Boolean.parseBoolean(register)) {
            return;
        }
        int port = event.getWebServer().getPort();
        final String host = getHost();
        String contextPath = soulHttpConfig.getContextPath();
        String zookeeperUrl = soulHttpConfig.getZookeeperUrl();
        if (contextPath == null || "".equals(contextPath)
                || zookeeperUrl == null || "".equals(zookeeperUrl)) {
            log.error("springMvc register must config context-path and zookeeperUrl.... ");
            return;
        }
        //[bins-comments:客户自己的项目启动的时候，会去zk上注册临时节点]
        try {
            ZkClient zkClient = new ZkClient(soulHttpConfig.getZookeeperUrl(), 5000, 2000);
            boolean exists = zkClient.exists(ROOT);
            if (!exists) {
                // 创建父节点
                zkClient.createPersistent(ROOT, true);
            }
            String serverPath = ROOT + contextPath;
            if (!zkClient.exists(serverPath)) {
                //创建应用服务节点
                zkClient.createPersistent(serverPath, true);
            }
            // 拼接ip和端口
            String data = host + ":" + port;
            zkClient.createEphemeralSequential(serverPath + "/children", data);
            log.info("soul-http-client服务注册成功,context-path:{}, ip:port:{}", contextPath, data);
        } catch (Exception e) {
            log.error("springMvc zookeeper register error:", e);
        }
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }
}
