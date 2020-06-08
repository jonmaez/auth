package com.ahsanb.auth.tenant.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.ahsanb.auth.tenant.entities.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name = "Role")
@Table
public class Role {
	
	public Role() {}
	public Role(RoleType name) {
		this.name = name;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
	private Long id;

	@Column(length = 20)
    @NotNull(message = "Name cannot be null")
	@Enumerated(EnumType.STRING)
	private RoleType name;
}