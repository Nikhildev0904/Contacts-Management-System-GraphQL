package com.nikhildev.projects.cms.resolvers;

import com.nikhildev.projects.cms.models.PagedResponse;
import com.nikhildev.projects.cms.models.Tenant;
import com.nikhildev.projects.cms.services.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class TenantResolver {

    private static final Logger logger = LoggerFactory.getLogger(TenantResolver.class);

    private final TenantService tenantService;

    @Autowired
    public TenantResolver(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @QueryMapping
    public PagedResponse<Tenant> tenants(
            @Argument String name,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {

        logger.info("GraphQL: Fetching tenants with name filter: {}, page: {}", name, page);
        PagedResponse<Tenant> response = tenantService.getAllTenants(name, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Fetched {} tenants", response.getTotalElements());
        return response;
    }

    @QueryMapping
    public Tenant tenant(@Argument String id) {
        logger.info("GraphQL: Fetching tenant with ID: {}", id);
        return tenantService.getTenantById(id);
    }

    @MutationMapping
    public Tenant createTenant(@Argument("input") Tenant tenant) {
        logger.info("GraphQL: Creating new tenant with name: {}", tenant.getName());
        Tenant createdTenant = tenantService.createTenant(tenant);
        logger.info("GraphQL: Created tenant with ID: {}", createdTenant.getId());
        return createdTenant;
    }

    @MutationMapping
    public Tenant updateTenant(@Argument String id, @Argument("input") Tenant tenant) {
        logger.info("GraphQL: Updating tenant with ID: {}", id);
        Tenant updatedTenant = tenantService.updateTenant(id, tenant);
        logger.info("GraphQL: Updated tenant with ID: {}", id);
        return updatedTenant;
    }

    @MutationMapping
    public Boolean deleteTenant(@Argument String id) {
        logger.info("GraphQL: Deleting tenant with ID: {}", id);
        tenantService.deleteTenant(id);
        logger.info("GraphQL: Deleted tenant with ID: {}", id);
        return true;
    }
}