package com.ahsanb.auth.util;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahsanb.auth.t0.entities.T0Tenant;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);

    public static DataSource createAndConfigureDataSource(T0Tenant t0Tenant) {
        HikariDataSource ds = new HikariDataSource();
        ds.setUsername(t0Tenant.getUsername());
        ds.setPassword(t0Tenant.getPassword());
        ds.setJdbcUrl(t0Tenant.getUrl());
        ds.setDriverClassName(t0Tenant.getDriverClass());
        // HikariCP settings - could come from the master_tenant table but
        // hardcoded here for brevity
        // Maximum waiting time for a connection from the pool
        ds.setConnectionTimeout(20000);
        // Minimum number of idle connections in the pool
        ds.setMinimumIdle(3);
        // Maximum number of actual connection in the pool
        ds.setMaximumPoolSize(500);
        // Maximum time that a connection is allowed to sit idle in the pool
        ds.setIdleTimeout(300000);
        ds.setConnectionTimeout(20000);
        // Setting up a pool name for each tenant datasource
        String tenantConnectionPoolName = t0Tenant.getDbName() + "-connection-pool";
        ds.setPoolName(tenantConnectionPoolName);
        logger.info("Configured datasource:" + t0Tenant.getDbName() + ". Connection pool name:" + tenantConnectionPoolName);
        return ds;
    }
}
