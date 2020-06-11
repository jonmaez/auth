package com.ahsanb.auth.tenant.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import com.ahsanb.auth.master.entities.MasterTenant;
import com.ahsanb.auth.master.services.MasterTenantService;
import com.ahsanb.auth.security.UserTenantInformation;
import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.entities.UserDetailsImpl;
import com.ahsanb.auth.tenant.exceptions.InvalidCredentialsException;
import com.ahsanb.auth.util.JwtTokenUtil;
import com.ahsanb.auth.util.TenantContextHolder;

@Service("authService")
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
    @Autowired
    MasterTenantService masterTenantService;
    
    private Map<String, String> mapValue = new HashMap<>();
    private Map<String, String> userDbMap = new HashMap<>();
	
	@Override
	public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialsException {
        // Set database parameter
        MasterTenant masterTenant = masterTenantService.findByTenantId(loginRequest.getTenantId());
        if(masterTenant == null || !masterTenant.isActive()){
            throw new InvalidCredentialsException("Invalid credentials!");
        }
        
        //Entry Client Wise value dbName store into bean.
        loadCurrentDatabaseInstance(masterTenant.getTenantId(), loginRequest.getUsername());
 
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())); 
		} catch(BadCredentialsException e) {
			throw new InvalidCredentialsException("Invalid credentials!");
		}
       
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
		String token = jwtTokenUtil.generateToken(loginRequest.getTenantId(), authentication);
		
		setMetaDataAfterLogin();
				
		List<String> roles = userDetails.getAuthorities().stream()
														 .map(item -> item.getAuthority())
														 .collect(Collectors.toList());

		return new LoginResponse(token, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
	}

    private void loadCurrentDatabaseInstance(String tenantId, String userName) {
        TenantContextHolder.setTenantId(tenantId);
        mapValue.put(userName, tenantId);
    }

    @Bean(name = "userTenantInfo")
    @ApplicationScope
    public UserTenantInformation setMetaDataAfterLogin() {
        UserTenantInformation tenantInformation = new UserTenantInformation();
        if (mapValue.size() > 0) {
            for (String key : mapValue.keySet()) {
                if (null == userDbMap.get(key)) {
                    //Here Assign putAll due to all time one come.
                    userDbMap.putAll(mapValue);
                } else {
                    userDbMap.put(key, mapValue.get(key));
                }
            }
            mapValue = new HashMap<>();
        }
        tenantInformation.setMap(userDbMap);
        return tenantInformation;
    }
}
