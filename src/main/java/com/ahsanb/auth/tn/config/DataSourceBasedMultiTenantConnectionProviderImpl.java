package com.ahsanb.auth.tn.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ahsanb.auth.t0.config.DBContextHolder;
import com.ahsanb.auth.t0.dao.T0TenantRepository;
import com.ahsanb.auth.t0.entities.T0Tenant;
import com.ahsanb.auth.util.DataSourceUtil;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 1521485491041689779L;

	private static final Logger logger = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

    private Map<String, DataSource> dataSources = new TreeMap<>();

    @Autowired
    private T0TenantRepository t0TenantRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected DataSource selectAnyDataSource() {
        // This method is called more than once. So check if the data source map
        // is empty. If it is then rescan t0_tenant table for all tenant
        if (dataSources.isEmpty()) {
            List<T0Tenant> t0Tenants = t0TenantRepository.findAll();
            for (T0Tenant t0Tenant : t0Tenants) {
                dataSources.put(t0Tenant.getDbName(), DataSourceUtil.createAndConfigureDataSource(t0Tenant));
            }
        }
        return this.dataSources.values().iterator().next();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        // If the requested tenant id is not present check for it in the t0
        // database 't0_tenant' table
        tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
        if (!this.dataSources.containsKey(tenantIdentifier)) {
            List<T0Tenant> t0Tenants = t0TenantRepository.findAll();
            for (T0Tenant t0Tenant : t0Tenants) {
                dataSources.put(t0Tenant.getDbName(), DataSourceUtil.createAndConfigureDataSource(t0Tenant));
            }
        }
        //check again if tenant exist in map after rescan t0_db, if not, throw UsernameNotFoundException
        if (!this.dataSources.containsKey(tenantIdentifier)) {
            logger.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in t0 db after re-scan");
            throw new UsernameNotFoundException(String.format("Tenant not found after rescan, " + " tenant=%s", tenantIdentifier));
        }
        return this.dataSources.get(tenantIdentifier);
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (tenantIdentifier != DBContextHolder.getCurrentDb()) {
            tenantIdentifier = DBContextHolder.getCurrentDb();
        }
        return tenantIdentifier;
    }
}
