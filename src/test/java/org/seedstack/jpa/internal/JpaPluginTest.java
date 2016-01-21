/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import io.nuun.kernel.api.Plugin;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.apache.commons.configuration.Configuration;
import org.assertj.core.api.Assertions;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.seedstack.jdbc.spi.JdbcRegistry;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.seed.Application;
import org.seedstack.seed.core.internal.application.ApplicationPlugin;
import org.seedstack.seed.transaction.internal.TransactionPlugin;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Persistence.class)
public class JpaPluginTest {

    private JpaPlugin underTest;

    @Before
    public void before() {
        underTest = new JpaPlugin();
    }

    @Test
    public void initTest() {
        mockPersistenceCreateEntityManagerFactory();
        ApplicationPlugin applicationPlugin = mockApplicationPlugin(mockConfiguration("org.seedstack.jpa.sample.Unit3ExceptionHandler"));
        underTest.init(mockInitContext(applicationPlugin, mockTransactionPlugin(), mockJdbcRegistry()));

        Map<String, JpaExceptionHandler> exceptionHandlerClasses = Reflection.field("exceptionHandlerClasses")
                .ofType(new TypeRef<Map<String, JpaExceptionHandler>>() {}).in(underTest).get();

        Assertions.assertThat(exceptionHandlerClasses).isNotNull();
        Assertions.assertThat(exceptionHandlerClasses).hasSize(1);
    }

    public void mockPersistenceCreateEntityManagerFactory() {
        mockStatic(Persistence.class);
        when(Persistence.createEntityManagerFactory("hsql-in-memory", getProperties())).thenReturn(mock(EntityManagerFactory.class));
    }

    private <T extends Plugin> InitContext mockInitContext(ApplicationPlugin applicationPlugin, TransactionPlugin transactionPlugin, JdbcRegistry jdbcRegistry) {
        InitContext initContext = mock(InitContext.class);
        when(initContext.dependency(ApplicationPlugin.class)).thenReturn(applicationPlugin);
        when(initContext.dependency(TransactionPlugin.class)).thenReturn(transactionPlugin);
        when(initContext.dependency(JdbcRegistry.class)).thenReturn(jdbcRegistry);
        return initContext;
    }

    public JdbcRegistry mockJdbcRegistry() {
        return mock(JdbcRegistry.class);
    }

    public ApplicationPlugin mockApplicationPlugin(Configuration configuration) {
        ApplicationPlugin applicationPlugin = mock(ApplicationPlugin.class);
        Application application = mock(Application.class);
        when(applicationPlugin.getApplication()).thenReturn(application);
        when(application.getConfiguration()).thenReturn(configuration);
        return applicationPlugin;
    }

    public Configuration mockConfiguration(String itemExceptionHandlerName) {
        Configuration configuration = mock(Configuration.class);
        Assertions.assertThat(configuration).isNotNull();
        when(configuration.subset(JpaPlugin.JPA_PLUGIN_CONFIGURATION_PREFIX)).thenReturn(configuration);
        when(configuration.getStringArray("units")).thenReturn(new String[]{"hsql-in-memory"});
        when(configuration.subset("unit.hsql-in-memory")).thenReturn(configuration);
        Map<String, String> properties = getProperties();
        when(configuration.getKeys("property")).thenReturn(properties.keySet().iterator());
        for (Entry<String, String> entry : properties.entrySet()) {
            when(configuration.getString(entry.getKey())).thenReturn(entry.getValue());
        }
        when(configuration.getString("exception-handler")).thenReturn(itemExceptionHandlerName);
        return configuration;
    }

    public TransactionPlugin mockTransactionPlugin() {
        return mock(TransactionPlugin.class);
    }

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
        properties.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:testdb");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("sql.enforce_strict_size", "true");
        return properties;
    }
}
