/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa.internal;

import org.seedstack.jdbc.spi.JdbcProvider;
import org.seedstack.jpa.JpaConfig;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class EntityManagerFactoryFactory {
    private static final String JPA_UNIT = "jpaUnit";
    private final JdbcProvider jdbcProvider;
    private final Application application;

    EntityManagerFactoryFactory(JdbcProvider jdbcProvider, Application application) {
        this.jdbcProvider = jdbcProvider;
        this.application = application;
    }

    EntityManagerFactory createEntityManagerFactory(String persistenceUnitName,
                                                    JpaConfig.PersistenceUnitConfig persistenceUnitConfig) {
        return Persistence
                .createEntityManagerFactory(persistenceUnitName, persistenceUnitConfig.getProperties());
    }

    EntityManagerFactory createEntityManagerFactory(String persistenceUnitName,
                                                    JpaConfig.PersistenceUnitConfig persistenceUnitConfig, Collection<Class<?>> scannedClasses,
                                                    boolean allClassesInUnit) {
        InternalPersistenceUnitInfo unitInfo = new InternalPersistenceUnitInfo(persistenceUnitName);

        String dataSourceName = persistenceUnitConfig.getDatasource();
        DataSource dataSource = jdbcProvider.getDataSource(dataSourceName);
        if (dataSource == null) {
            throw SeedException.createNew(JpaErrorCode.DATA_SOURCE_NOT_FOUND)
                    .put("unit", unitInfo.getPersistenceUnitName()).put("datasource", dataSourceName);
        }

        Set<String> classNames = Stream
                .concat(scannedClasses.stream(), persistenceUnitConfig.getClasses().stream())
                .filter(scannedClass -> allClassesInUnit || belongToJpaUnit(unitInfo, scannedClass))
                .map(Class::getName)
                .collect(Collectors.toSet());
        unitInfo.setManagedClassNames(new ArrayList<>(classNames));

        if (persistenceUnitConfig.hasMappingFiles()) {
            unitInfo.setMappingFileNames(persistenceUnitConfig.getMappingFiles());
        } else {
            unitInfo.setMappingFileNames(Collections.emptyList());
        }

        if (unitInfo.getManagedClassNames().isEmpty() && unitInfo.getMappingFileNames().isEmpty()) {
            throw SeedException.createNew(JpaErrorCode.NO_PERSISTED_CLASSES_IN_UNIT)
                    .put("unit", unitInfo.getPersistenceUnitName());
        }

        unitInfo.setProperties(persistenceUnitConfig.getProperties());
        unitInfo.setValidationMode(persistenceUnitConfig.getValidationMode());
        unitInfo.setSharedCacheMode(persistenceUnitConfig.getSharedCacheMode());
        unitInfo.setPersistenceUnitTransactionType(persistenceUnitConfig.getTransactionType());

        switch (unitInfo.getTransactionType()) {
            case RESOURCE_LOCAL:
                unitInfo.setNonJtaDataSource(dataSource);
                break;
            case JTA:
                unitInfo.setJtaDataSource(dataSource);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown transaction type " + unitInfo.getTransactionType());
        }

        return createEntityManagerFactory(unitInfo);
    }

    private boolean belongToJpaUnit(InternalPersistenceUnitInfo unitInfo, Class<?> scannedClass) {
        String[] jpaUnits = application.getConfiguration(scannedClass).getArray(JPA_UNIT);
        if (jpaUnits != null) {
            for (String jpaUnit : jpaUnits) {
                if (unitInfo.getPersistenceUnitName().equals(jpaUnit)) {
                    return true;
                }
            }
        }
        return false;
    }

    private EntityManagerFactory createEntityManagerFactory(InternalPersistenceUnitInfo info) {
        EntityManagerFactory fac = null;
        List<PersistenceProvider> persistenceProviders = PersistenceProviderResolverHolder
                .getPersistenceProviderResolver().getPersistenceProviders();

        for (PersistenceProvider persistenceProvider : persistenceProviders) {
            info.setPersistenceProviderClassName(persistenceProvider.getClass().getName());
            fac = persistenceProvider.createContainerEntityManagerFactory(info, null);
            if (fac != null) {
                break;
            }
        }

        if (fac == null) {
            throw new PersistenceException(
                    "No Persistence provider for persistence unit " + info.getPersistenceUnitName());
        }

        return fac;
    }
}
