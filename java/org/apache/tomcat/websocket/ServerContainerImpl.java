/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.websocket;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.ServerContainer;
import javax.websocket.ServerEndpointConfiguration;

public class ServerContainerImpl extends ClientContainerImpl implements
        ServerContainer {

    // Needs to be a WekaHashMap to prevent memory leaks when a context is
    // stopped
    private static Map<ClassLoader, ServerContainerImpl>
            classLoaderContainerMap = new WeakHashMap<>();
    private static ReadWriteLock classLoaderContainerMapLock =
            new  ReentrantReadWriteLock();

    public static ServerContainerImpl getServerContainer() {
        // TODO SecurityManager
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        ServerContainerImpl result = null;

        Lock readlock = classLoaderContainerMapLock.readLock();
        try {
            readlock.lock();
            result = classLoaderContainerMap.get(tccl);
        } finally {
            readlock.unlock();
        }

        if (result == null) {
            Lock writeLock = classLoaderContainerMapLock.writeLock();
            try {
                writeLock.lock();
                result = classLoaderContainerMap.get(tccl);
                if (result == null) {
                    result = new ServerContainerImpl();
                    classLoaderContainerMap.put(tccl, result);
                }
            } finally {
                writeLock.unlock();
            }
        }
        return result;
    }

    private ServerContainerImpl() {
        // Hide default constructor
    }

    @Override
    public void publishServer(Class<? extends Endpoint> clazz)
            throws DeploymentException {

        try {
            Endpoint ep = clazz.newInstance();
            ServerEndpointConfiguration config =
                    (ServerEndpointConfiguration) ep.getEndpointConfiguration();
            String path = Util.getServletMappingPath(config.getPath());

            // TODO Replace following debug code with something useful.
            System.out.println("Class [" + clazz.getName() +
                    "] deployed to path [" + path + "]");
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO i18n
            throw new DeploymentException(
                    "Failed to instantiate specified Endpoint", e);
        }
    }

    public void publishServer(Class<?> pojo, String path) {
        // TODO Replace following debug code with something useful.
        System.out.println("Class [" + pojo.getName() +
                "] deployed to path [" + path + "]");
    }
}