/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.PluginException;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequestBuilder;
import io.nuun.kernel.core.AbstractPlugin;
import org.apache.commons.configuration.Configuration;
import org.seedstack.jdbc.internal.JdbcRegistry;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.seed.Application;
import org.seedstack.seed.core.internal.application.ApplicationPlugin;
import org.seedstack.seed.transaction.internal.TransactionPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.util.*;

/**
 * This plugin enables JPA support by creating an {@link javax.persistence.EntityManagerFactory} per persistence unit configured.
 *
 * @author adrien.lauer@mpsa.com
 */
public class JpaPlugin extends AbstractPlugin {
    public static final String JPA_PLUGIN_CONFIGURATION_PREFIX = "org.seedstack.jpa";

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaPlugin.class);

    private final EntityManagerFactoryFactory confResolver = new EntityManagerFactoryFactory();
    private final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<String, EntityManagerFactory>();
    private final Map<String, Class<? extends JpaExceptionHandler>> exceptionHandlerClasses = new HashMap<String, Class<? extends JpaExceptionHandler>>();

    @Override
    public String name() {
        return "jpa";
    }

    @Override
    @SuppressWarnings("unchecked")
    public InitState init(InitContext initContext) {
        TransactionPlugin transactionPlugin = initContext.dependency(TransactionPlugin.class);
        JdbcRegistry jdbcRegistry = initContext.dependency(JdbcRegistry.class);
        Application application = initContext.dependency(ApplicationPlugin.class).getApplication();
        Configuration jpaConfiguration = application.getConfiguration().subset(JpaPlugin.JPA_PLUGIN_CONFIGURATION_PREFIX);

        String[] persistenceUnitNames = jpaConfiguration.getStringArray("units");

        if (persistenceUnitNames == null || persistenceUnitNames.length == 0) {
            LOGGER.info("No JPA persistence unit configured, JPA support disabled");
            return InitState.INITIALIZED;
        }

        for (String persistenceUnit : persistenceUnitNames) {
            Configuration persistenceUnitConfiguration = jpaConfiguration.subset("unit." + persistenceUnit);
            Iterator<String> it = persistenceUnitConfiguration.getKeys("property");

            Properties properties = new Properties();
            while (it.hasNext()) {
                String name = it.next();
                properties.put(name.substring(9), persistenceUnitConfiguration.getString(name));
            }

            EntityManagerFactory emf;
            if (persistenceUnitConfiguration.containsKey("datasource")) {
                Collection<Class<?>> scannedClasses = new ArrayList<Class<?>>();
                if (initContext.scannedClassesByAnnotationClass().get(Entity.class) != null) {
                    scannedClasses.addAll(initContext.scannedClassesByAnnotationClass().get(Entity.class));
                }
                if (initContext.scannedClassesByAnnotationClass().get(Embeddable.class) != null) {
                    scannedClasses.addAll(initContext.scannedClassesByAnnotationClass().get(Embeddable.class));
                }

                emf = confResolver.createEntityManagerFactory(persistenceUnit, properties, persistenceUnitConfiguration, application, jdbcRegistry, scannedClasses);
            } else {
                emf = confResolver.createEntityManagerFactory(persistenceUnit, properties);
            }

            entityManagerFactories.put(persistenceUnit, emf);

            String exceptionHandler = persistenceUnitConfiguration.getString("exception-handler");
            if (exceptionHandler != null && !exceptionHandler.isEmpty()) {
                try {
                    exceptionHandlerClasses.put(persistenceUnit, (Class<? extends JpaExceptionHandler>) Class.forName(exceptionHandler));
                } catch (Exception e) {
                    throw new PluginException("Unable to load class " + exceptionHandler, e);
                }
            }
        }

        if (persistenceUnitNames.length == 1) {
            JpaTransactionMetadataResolver.defaultJpaUnit = persistenceUnitNames[0];
        }

        transactionPlugin.registerTransactionHandler(JpaTransactionHandler.class);

        return InitState.INITIALIZED;
    }

    @Override
    public void stop() {
        for (Map.Entry<String, EntityManagerFactory> entityManagerFactory : entityManagerFactories.entrySet()) {
            LOGGER.info("Closing entity manager factory for persistence unit {}", entityManagerFactory.getKey());
            try {
                entityManagerFactory.getValue().close();
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to properly close entity manager factory for persistence unit %s", entityManagerFactory.getKey()), e);
            }
        }
    }

    @Override
    public Collection<Class<?>> requiredPlugins() {
        return Lists.<Class<?>>newArrayList(ApplicationPlugin.class, TransactionPlugin.class, JdbcRegistry.class);
    }

    @Override
    public Object nativeUnitModule() {
        return new JpaModule(entityManagerFactories, exceptionHandlerClasses);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return new ClasspathScanRequestBuilder().annotationType(Entity.class).annotationType(Embeddable.class).build();
    }

}
