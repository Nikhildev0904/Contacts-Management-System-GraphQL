package com.cognitree.internship.cms.resolvers;

import com.cognitree.internship.cms.models.Category;
import com.cognitree.internship.cms.models.Contact;
import com.cognitree.internship.cms.models.PagedResponse;
import com.cognitree.internship.cms.services.CategoryService;
import com.cognitree.internship.cms.services.ContactService;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@PreAuthorize("hasRole('USER')")
public class ContactResolver {

    private static final Logger logger = LoggerFactory.getLogger(ContactResolver.class);

    private final ContactService contactService;
    private final CategoryService categoryService;

    @Autowired
    public ContactResolver(ContactService contactService, CategoryService categoryService) {
        this.contactService = contactService;
        this.categoryService = categoryService;
    }

    @QueryMapping
    public PagedResponse<Contact> contacts(
            @Argument String contactName,
            @Argument String phone,
            @Argument String categoryName,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching contacts with filters: contactName={}, phone={}, categoryName={}, page={}",
                contactName, phone, categoryName, page);
        PagedResponse<Contact> response = contactService.getAllContacts(
                contactName, phone, categoryName, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Fetched {} contacts", response.getTotalElements());
        return response;
    }

    @QueryMapping
    public Contact contact(@Argument String id) {
        logger.info("GraphQL: Fetching contact with ID: {}", id);
        return contactService.getContactById(id);
    }

    @QueryMapping
    public PagedResponse<Category> contactCategories(
            @Argument String contactId,
            @Argument String categoryName,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching categories for contact ID: {} with name filter: {}", contactId, categoryName);
        PagedResponse<Category> categories = contactService.getContactCategories(
                contactId, categoryName, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Found {} categories for contact {}", categories.getTotalElements(), contactId);
        return categories;
    }

    @SchemaMapping(typeName = "Contact", field = "categories")
    public PagedResponse<Category> categories(
            Contact contact,
            @Argument String categoryName,
            @Argument Integer page,
            @Argument Integer pageSize,
            @Argument String sortBy,
            @Argument Sort.Direction sortOrder) {
        logger.info("GraphQL: Fetching categories for contact ID: {} with name filter: {}",
                contact.getId(), categoryName);
        PagedResponse<Category> categories = contactService.getContactCategories(
                contact.getId(), categoryName, page, pageSize, sortBy, sortOrder);
        logger.debug("GraphQL: Found {} categories for contact {}",
                categories.getTotalElements(), contact.getId());
        return categories;
    }

    @MutationMapping
    public Contact createContact(@Argument("input") Contact contact) {
        logger.info("GraphQL: Creating new contact with name: {}", contact.getContactName());
        Contact createdContact = contactService.createContact(contact);
        logger.info("GraphQL: Created contact with ID: {}", createdContact.getId());
        return createdContact;
    }

    @MutationMapping
    public Contact updateContact(@Argument String id, @Argument("input") Contact contact) {
        logger.info("GraphQL: Updating contact with ID: {}", id);
        Contact updatedContact = contactService.updateContact(id, contact);
        logger.info("GraphQL: Successfully updated contact: {}", id);
        return updatedContact;
    }

    @MutationMapping
    public Boolean deleteContact(@Argument String id) {
        logger.info("GraphQL: Deleting contact with ID: {}", id);
        contactService.deleteContact(id);
        logger.info("GraphQL: Successfully deleted contact: {}", id);
        return true;
    }

    @MutationMapping
    public Contact addCategoryToContact(@Argument String contactId, @Argument String categoryId) {
        logger.info("GraphQL: Assigning category {} to contact {}", categoryId, contactId);
        Contact updatedContact = contactService.addCategoryToContact(contactId, categoryId);
        logger.info("GraphQL: Successfully assigned category {} to contact {}", categoryId, contactId);
        return updatedContact;
    }

    @MutationMapping
    public Boolean removeCategoryFromContact(@Argument String contactId, @Argument String categoryId) {
        logger.info("GraphQL: Removing category {} from contact {}", categoryId, contactId);
        contactService.removeCategoryFromContact(contactId, categoryId);
        logger.info("GraphQL: Successfully removed category {} from contact {}", categoryId, contactId);
        return true;
    }
}