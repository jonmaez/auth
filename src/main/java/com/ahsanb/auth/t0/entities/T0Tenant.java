package com.ahsanb.auth.t0.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
	
@Data
@AllArgsConstructor
@Entity
@Table(name = "tbl_tenant_t0")
public class T0Tenant implements Serializable {
	private static final long serialVersionUID = -515164655454338287L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Integer tenantId;

    @NotNull(message = "DB Name cannot be null")
    @NotBlank(message = "DB Name cannot be empty")
    @Size(max = 50)
    @Column(name = "db_name",nullable = false)
    private String dbName;

    @NotNull(message = "DB Name cannot be null")
    @NotBlank(message = "DB Name cannot be empty")
    @Size(max = 100)
    @Column(name = "url",nullable = false)
    private String url;

    @NotNull(message = "DB Username cannot be null")
    @NotBlank(message = "DB Username cannot be empty")
    @Size(max = 50)
    @Column(name = "user_name",nullable = false)
    private String username;
    
    @NotNull(message = "DB Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(max = 100)
    @Column(name = "password",nullable = false)
    private String password;
    
    @NotNull(message = "Driver Class cannot be null")
    @NotBlank(message = "Driver Class cannot be empty")
    @Size(max = 100)
    @Column(name = "driver_class",nullable = false)
    private String driverClass;
    
    @NotNull(message = "Status cannot be null")
    @NotBlank(message = "Status cannot be empty")
    @Size(max = 10)
    @Column(name = "status",nullable = false)
    private String status;

    public T0Tenant() {
    }

    public T0Tenant(String dbName, String url, String username, String password, String driverClass, String status) {
        this.dbName = dbName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
        this.status = status;
    }
}
