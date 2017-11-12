/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.jpa.internal;

import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.seedstack.jpa.JpaExceptionHandler;
import org.seedstack.jpa.spi.JpaRepositoryFactory;
import org.seedstack.seed.core.internal.transaction.TransactionalProxy;

class JpaModule extends PrivateModule {

    private final Map<String, EntityManagerFactory> entityManagerFactories;
    private final Map<String, Class<? extends JpaExceptionHandler>> jpaExceptionHandlerClasses;
    private final List<Class<? extends JpaRepositoryFactory>> jpaRepositoryFactories;

    JpaModule(Map<String, EntityManagerFactory> entityManagerFactories,
            Map<String, Class<? extends JpaExceptionHandler>> jpaExceptionHandlerClasses,
            List<Class<? extends JpaRepositoryFactory>> jpaRepositoryFactories) {
        this.entityManagerFactories = entityManagerFactories;
        this.jpaExceptionHandlerClasses = jpaExceptionHandlerClasses;
        this.jpaRepositoryFactories = jpaRepositoryFactories;
    }

    @Override
    protected void configure() {
        if (entityManagerFactories != null && !entityManagerFactories.isEmpty()) {
            EntityManagerLink entityManagerLink = new EntityManagerLink();

            bind(EntityManager.class)
                    .toInstance(TransactionalProxy.create(EntityManager.class, entityManagerLink));
            expose(EntityManager.class);

            for (Map.Entry<String, EntityManagerFactory> entry : entityManagerFactories.entrySet()) {
                bindUnit(entry.getKey(), entry.getValue(), entityManagerLink);
            }

            Multibinder<JpaRepositoryFactory> jpaRepositoryFactoryMultibinder = Multibinder
                    .newSetBinder(binder(), JpaRepositoryFactory.class);
            for (Class<? extends JpaRepositoryFactory> jpaRepositoryFactory : jpaRepositoryFactories) {
                jpaRepositoryFactoryMultibinder.addBinding().to(jpaRepositoryFactory);
            }

            expose(new JpaRepositoryFactoriesTypeLiteral());
        }
    }

    private void bindUnit(String name, EntityManagerFactory entityManagerFactory,
            EntityManagerLink entityManagerLink) {
        Class<? extends JpaExceptionHandler> unitExceptionHandlerClass = jpaExceptionHandlerClasses
                .get(name);

        if (unitExceptionHandlerClass != null) {
            bind(JpaExceptionHandler.class).annotatedWith(Names.named(name))
                    .to(unitExceptionHandlerClass);
        } else {
            bind(JpaExceptionHandler.class).annotatedWith(Names.named(name))
                    .toProvider(Providers.of(null));
        }

        JpaTransactionHandler transactionHandler = new JpaTransactionHandler(entityManagerLink,
                entityManagerFactory);
        bind(JpaTransactionHandler.class).annotatedWith(Names.named(name))
                .toInstance(transactionHandler);

        expose(JpaExceptionHandler.class).annotatedWith(Names.named(name));
        expose(JpaTransactionHandler.class).annotatedWith(Names.named(name));
    }

    private static class JpaRepositoryFactoriesTypeLiteral extends
            TypeLiteral<Set<JpaRepositoryFactory>> {

    }
}
