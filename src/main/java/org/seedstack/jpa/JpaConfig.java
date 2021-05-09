/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.seedstack.coffig.Config;

@Config("jpa")
public class JpaConfig {
    private Map<String, PersistenceUnitConfig> units = new HashMap<>();
    private String defaultUnit;
    private boolean allClassesInUnit = true;

    public Map<String, PersistenceUnitConfig> getUnits() {
        return Collections.unmodifiableMap(units);
    }

    public JpaConfig addUnit(String name, PersistenceUnitConfig config) {
        units.put(name, config);
        return this;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public JpaConfig setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
        return this;
    }

    public boolean isAllClassesInUnit() {
        return allClassesInUnit;
    }

    public JpaConfig setAllClassesInUnit(boolean allClassesInUnit) {
        this.allClassesInUnit = allClassesInUnit;
        return this;
    }

    public static class PersistenceUnitConfig {

        private String datasource;
        private Class<? extends PersistenceProvider> provider;
        private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType
                .RESOURCE_LOCAL;
        private List<String> mappingFiles = new ArrayList<>();
        private List<Class<?>> classes = new ArrayList<>();
        private SharedCacheMode sharedCacheMode = SharedCacheMode.UNSPECIFIED;
        private ValidationMode validationMode = ValidationMode.AUTO;
        private Properties properties = new Properties();
        private Class<? extends JpaExceptionHandler> exceptionHandler;

        public String getDatasource() {
            return datasource;
        }

        public PersistenceUnitConfig setDatasource(String datasource) {
            this.datasource = datasource;
            return this;
        }

        public boolean isUsingDatasource() {
            return datasource != null && !datasource.isEmpty();
        }

        public Class<? extends PersistenceProvider> getProvider() {
            return provider;
        }

        public PersistenceUnitConfig setProvider(Class<? extends PersistenceProvider> provider) {
            this.provider = provider;
            return this;
        }

        public PersistenceUnitTransactionType getTransactionType() {
            return transactionType;
        }

        public PersistenceUnitConfig setTransactionType(
                PersistenceUnitTransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public List<String> getMappingFiles() {
            return Collections.unmodifiableList(mappingFiles);
        }

        public PersistenceUnitConfig setMappingFiles(List<String> mappingFiles) {
            this.mappingFiles = new ArrayList<>(mappingFiles);
            return this;
        }

        public boolean hasMappingFiles() {
            return !mappingFiles.isEmpty();
        }

        public PersistenceUnitConfig addMappingFile(String mappingFile) {
            this.mappingFiles.add(mappingFile);
            return this;
        }

        public List<Class<?>> getClasses() {
            return Collections.unmodifiableList(classes);
        }

        public PersistenceUnitConfig setClasses(List<Class<?>> classes) {
            this.classes = new ArrayList<>(classes);
            return this;
        }

        public boolean hasClasses() {
            return !classes.isEmpty();
        }

        public PersistenceUnitConfig addClass(Class<?> someClass) {
            this.classes.add(someClass);
            return this;
        }

        public SharedCacheMode getSharedCacheMode() {
            return sharedCacheMode;
        }

        public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
            this.sharedCacheMode = sharedCacheMode;
        }

        public ValidationMode getValidationMode() {
            return validationMode;
        }

        public void setValidationMode(ValidationMode validationMode) {
            this.validationMode = validationMode;
        }

        public Properties getProperties() {
            return properties;
        }

        public PersistenceUnitConfig setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public PersistenceUnitConfig setProperty(String key, String value) {
            this.properties.setProperty(key, value);
            return this;
        }

        public Class<? extends JpaExceptionHandler> getExceptionHandler() {
            return exceptionHandler;
        }

        public PersistenceUnitConfig setExceptionHandler(
                Class<? extends JpaExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public boolean hasExceptionHandler() {
            return exceptionHandler != null;
        }
    }
}
