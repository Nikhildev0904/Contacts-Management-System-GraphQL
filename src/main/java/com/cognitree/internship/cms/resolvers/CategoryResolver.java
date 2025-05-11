package com.cognitree.internship.cms.resolvers;

import com.cognitree.internship.cms.models.Category;
import com.cognitree.internship.cms.models.Contact;
import com.cognitree.internship.cms.models.PagedResponse;
import com.cognitree.internship.cms.services.CategoryService;
import com.cognitree.internship.cms.services.ContactService;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@PreAuthorize("hasRole('USER')")
public class CategoryResolver {

    private static final Logger logger = LoggerFactory.getLogger(CategoryResolver.class);

    private final CategoryService categoryService;
    private final ContactService contactService;

    @Autowired
    public CategoryResolver(CategoryService categoryService, ContactService contactService) {
        this.categoryService = categoryService;
        this.contactService = contactService;
    }

    @QueryMapping
    public PagedResponse<Category> categories(
            @Argument String categoryName,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching categories with categoryName={}, page={}, pageSize={}, sortBy={}, sortOrder={}",
                categoryName, page, pageSize, sortBy, sortOrder);
        PagedResponse<Category> response = categoryService.getAllCategories(
                categoryName, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Fetched {} categories", response.getTotalElements());
        return response;
    }

    @QueryMapping
    public Category category(@Argument String id) {
        logger.info("GraphQL: Fetching category with ID: {}", id);
        return categoryService.getCategoryById(id);
    }

    @QueryMapping
    public PagedResponse<Contact> categoryContacts(
            @Argument String categoryId,
            @Argument String contactName,
            @Argument String phone,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching contacts for category ID: {} with filters: contactName={}, phone={}",
                categoryId, contactName, phone);
        PagedResponse<Contact> contacts = categoryService.getCategoryContacts(
                categoryId, contactName, phone, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Found {} contacts for category {}", contacts.getTotalElements(), categoryId);
        return contacts;
    }

    @SchemaMapping(typeName = "Category", field = "contacts")
    public PagedResponse<Contact> contacts(
            Category category,
            @Argument String contactName,
            @Argument String phone,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching contacts for category ID: {} with filters: contactName={}, phone={}",
                category.getId(), contactName, phone);
        PagedResponse<Contact> contacts = categoryService.getCategoryContacts(
                category.getId(), contactName, phone, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Found {} contacts for category {}", contacts.getTotalElements(), category.getId());
        return contacts;
    }

    @MutationMapping
    public Category createCategory(@Argument("input") Category category) {
        logger.info("GraphQL: Creating new category with name: {}", category.getCategoryName());
        Category createdCategory = categoryService.createCategory(category);
        logger.info("GraphQL: Created category with ID: {}", createdCategory.getId());
        return createdCategory;
    }

    @MutationMapping
    public Category updateCategory(@Argument String id, @Argument("input") Category category) {
        logger.info("GraphQL: Updating category with ID: {}", id);
        Category updatedCategory = categoryService.updateCategory(id, category);
        logger.info("GraphQL: Successfully updated category: {}", id);
        return updatedCategory;
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument String id) {
        logger.info("GraphQL: Deleting category with ID: {}", id);
        categoryService.deleteCategory(id);
        logger.info("GraphQL: Successfully deleted category: {}", id);
        return true;
    }
}