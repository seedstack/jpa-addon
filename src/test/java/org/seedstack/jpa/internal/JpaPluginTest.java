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
import org.assertj.core.api.Assertions;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.seedstack.coffig.Coffig;
import org.seedstack.jdbc.spi.JdbcProvider;
import org.seedstack.jpa.JpaConfig;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.seed.Application;
import org.seedstack.seed.spi.config.ApplicationProvider;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;
import java.util.Properties;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Persistence.class)
public class JpaPluginTest {

    private JpaPlugin underTest;

    @Before
    public void before() {
        underTest = new JpaPlugin();
    }

    @Test
    public void initTest() throws Exception {
        mockPersistenceCreateEntityManagerFactory();
        ApplicationProvider applicationProvider = mockApplicationProvider(mockConfiguration("org.seedstack.jpa.fixtures.sample.Unit3ExceptionHandler"));
        underTest.init(mockInitContext(applicationProvider, mockJdbcRegistry()));

        Map<String, JpaExceptionHandler> exceptionHandlerClasses = Reflection.field("exceptionHandlerClasses")
                .ofType(new TypeRef<Map<String, JpaExceptionHandler>>() {
                }).in(underTest).get();

        Assertions.assertThat(exceptionHandlerClasses).isNotNull();
        Assertions.assertThat(exceptionHandlerClasses).hasSize(1);
    }

    public void mockPersistenceCreateEntityManagerFactory() {
        mockStatic(Persistence.class);
        when(Persistence.createEntityManagerFactory("hsql-in-memory", getProperties())).thenReturn(mock(EntityManagerFactory.class));
    }

    private <T extends Plugin> InitContext mockInitContext(ApplicationProvider applicationProvider, JdbcProvider jdbcProvider) {
        InitContext initContext = mock(InitContext.class);
        when(initContext.dependency(ApplicationProvider.class)).thenReturn(applicationProvider);
        when(initContext.dependency(JdbcProvider.class)).thenReturn(jdbcProvider);
        return initContext;
    }

    public JdbcProvider mockJdbcRegistry() {
        return mock(JdbcProvider.class);
    }

    public ApplicationProvider mockApplicationProvider(JpaConfig configuration) {
        ApplicationProvider applicationProvider = mock(ApplicationProvider.class);
        Application application = mock(Application.class);
        when(applicationProvider.getApplication()).thenReturn(application);
        Coffig coffig = mock(Coffig.class);
        when(coffig.get(JpaConfig.class)).thenReturn(configuration);
        when(application.getConfiguration()).thenReturn(coffig);
        return applicationProvider;
    }

    public JpaConfig mockConfiguration(String itemExceptionHandlerName) throws Exception {
        return new JpaConfig().addUnit(
                "hsql-in-memory",
                new JpaConfig.PersistenceUnitConfig()
                        .setProperties(getProperties())
                        .setExceptionHandler((Class<? extends JpaExceptionHandler>) Class.forName(itemExceptionHandlerName))
        );
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:testdb");
        properties.setProperty("javax.persistence.jdbc.user", "sa");
        properties.setProperty("javax.persistence.jdbc.password", "");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("sql.enforce_strict_size", "true");
        return properties;
    }
}
