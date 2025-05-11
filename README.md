# Contact Management System - GraphQL API

A multi-tenant contact management system built with Spring Boot, GraphQL, and MongoDB. This application provides isolated data storage for different tenants with a comprehensive GraphQL API for managing contacts and categories.

## Features

- **Multi-Tenant Architecture**: Complete data isolation with database-per-tenant approach
- **GraphQL API**: Type-safe queries and mutations with real-time subscriptions
- **Authentication & Authorization**: Role-based access control with JWT or Basic Authentication
- **Contact Management**: Full CRUD operations for contacts with advanced filtering
- **Category Management**: Organize contacts with custom categories
- **Admin Panel**: Tenant management capabilities for administrators
- **GraphQL Playground**: Interactive API explorer and documentation
- **Security**: Tenant isolation and user authentication

## Architecture

This application follows a multi-tenant architecture where:
- Each tenant has their own isolated MongoDB database
- Tenant selection is done through authenticated user context
- Admin users can manage all tenants
- Regular users access only their tenant's data
- GraphQL provides a single endpoint for all operations

## Technology Stack

- **Backend**: Spring Boot 3.x
- **API**: GraphQL with Spring GraphQL
- **Database**: MongoDB
- **Security**: Spring Security with JWT/Basic Authentication
- **Build Tool**: Maven
- **Java Version**: 17+

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- MongoDB 4.4+ (running on localhost:27017)
- GraphQL Playground or similar GraphQL client (optional)

## Project Structure

```
contacts-management-system/
├── src/main/java/com/cognitree/internship/cms/
│   ├── config/          # Configuration classes (Security, MongoDB, GraphQL)
│   ├── graphql/         # GraphQL resolvers, types, and schemas
│   │   ├── resolvers/   # Query, Mutation, and Subscription resolvers
│   │   ├── types/       # GraphQL type definitions
│   │   └── scalars/     # Custom scalar types
│   ├── models/          # Domain models
│   ├── repositories/    # MongoDB repositories
│   ├── services/        # Business logic
│   └── exceptions/      # Custom exceptions
├── src/main/resources/
│   ├── graphql/         # GraphQL schema files
│   │   └── schema.graphqls
│   └── application.properties
└── pom.xml
```

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/contacts-management-system.git
   cd contacts-management-system
   ```

2. **Ensure MongoDB is running**
   ```bash
   # Default connection: mongodb://localhost:27017
   mongod
   ```

3. **Add GraphQL dependencies to pom.xml**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-graphql</artifactId>
   </dependency>
   <dependency>
       <groupId>com.graphql-java</groupId>
       <artifactId>graphql-java-extended-scalars</artifactId>
       <version>20.0</version>
   </dependency>
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The GraphQL endpoint will be available at `http://localhost:8080/graphql`
GraphQL Playground will be available at `http://localhost:8080/graphiql`

## GraphQL Schema

```graphql
type Query {
  # Tenant Management (Admin only)
  tenants(page: Int, pageSize: Int, sortBy: String, sortOrder: SortOrder): TenantPage!
  tenant(id: ID!): Tenant
  
  # Contact Management
  contacts(page: Int, pageSize: Int, sortBy: String, sortOrder: SortOrder): ContactPage!
  contact(id: ID!): Contact
  searchContacts(filter: ContactSearchInput!): [Contact!]!
  
  # Category Management
  categories(page: Int, pageSize: Int, sortBy: String, sortOrder: SortOrder): CategoryPage!
  category(id: ID!): Category
}

type Mutation {
  # Tenant Management (Admin only)
  createTenant(input: CreateTenantInput!): Tenant!
  updateTenant(id: ID!, input: UpdateTenantInput!): Tenant!
  deleteTenant(id: ID!): Boolean!
  
  # Contact Management
  createContact(input: CreateContactInput!): Contact!
  updateContact(id: ID!, input: UpdateContactInput!): Contact!
  deleteContact(id: ID!): Boolean!
  
  # Category Management
  createCategory(input: CreateCategoryInput!): Category!
  updateCategory(id: ID!, input: UpdateCategoryInput!): Category!
  deleteCategory(id: ID!): Boolean!
}

type Subscription {
  # Real-time updates
  contactCreated: Contact!
  contactUpdated: Contact!
  contactDeleted: ID!
}

# Object Types
type Tenant {
  id: ID!
  name: String!
  description: String
  username: String!
  role: String!
  createdAt: DateTime!
  updatedAt: DateTime!
}

type Contact {
  id: ID!
  name: String!
  email: String
  phone: String
  category: String
  address: String
  createdAt: DateTime!
  updatedAt: DateTime!
}

type Category {
  id: ID!
  name: String!
  description: String
  createdAt: DateTime!
  updatedAt: DateTime!
}

# Input Types
input CreateTenantInput {
  name: String!
  description: String
  username: String!
  password: String!
  role: String!
}

input UpdateTenantInput {
  name: String
  description: String
}

input CreateContactInput {
  name: String!
  email: String
  phone: String
  category: String
  address: String
}

input UpdateContactInput {
  name: String
  email: String
  phone: String
  category: String
  address: String
}

input CreateCategoryInput {
  name: String!
  description: String
}

input UpdateCategoryInput {
  name: String
  description: String
}

input ContactSearchInput {
  name: String
  email: String
  phone: String
  category: String
}

# Enum Types
enum SortOrder {
  ASC
  DESC
}

# Custom Scalars
scalar DateTime

# Pagination Types
type TenantPage {
  content: [Tenant!]!
  pageNumber: Int!
  pageSize: Int!
  totalElements: Long!
  totalPages: Int!
  hasNext: Boolean!
  hasPrevious: Boolean!
}

type ContactPage {
  content: [Contact!]!
  pageNumber: Int!
  pageSize: Int!
  totalElements: Long!
  totalPages: Int!
  hasNext: Boolean!
  hasPrevious: Boolean!
}

type CategoryPage {
  content: [Category!]!
  pageNumber: Int!
  pageSize: Int!
  totalElements: Long!
  totalPages: Int!
  hasNext: Boolean!
  hasPrevious: Boolean!
}
```

## Authentication

### Using HTTP Headers

Include the authentication header in all GraphQL requests:

```
Authorization: Basic <base64-encoded-credentials>
```

or with JWT:

```
Authorization: Bearer <jwt-token>
```

### GraphQL Playground Configuration

When using GraphQL Playground, set the HTTP headers:

```json
{
  "Authorization": "Basic YWRtaW46YWRtaW4="
}
```

## GraphQL Operations Examples

### Tenant Management (Admin Only)

#### Create Tenant
```graphql
mutation CreateTenant {
  createTenant(input: {
    name: "Company ABC"
    description: "Primary tenant for Company ABC"
    username: "companyabc"
    password: "password123"
    role: "USER"
  }) {
    id
    name
    username
    role
    createdAt
  }
}
```

#### List All Tenants
```graphql
query ListTenants {
  tenants(page: 0, pageSize: 20, sortBy: "name", sortOrder: ASC) {
    content {
      id
      name
      description
      username
      role
      createdAt
      updatedAt
    }
    pageNumber
    pageSize
    totalElements
    totalPages
    hasNext
    hasPrevious
  }
}
```

#### Get Tenant by ID
```graphql
query GetTenant($id: ID!) {
  tenant(id: $id) {
    id
    name
    description
    username
    role
    createdAt
    updatedAt
  }
}
```

#### Update Tenant
```graphql
mutation UpdateTenant($id: ID!) {
  updateTenant(id: $id, input: {
    name: "Updated Company Name"
    description: "Updated description"
  }) {
    id
    name
    description
    updatedAt
  }
}
```

#### Delete Tenant
```graphql
mutation DeleteTenant($id: ID!) {
  deleteTenant(id: $id)
}
```

### Contact Management

#### Create Contact
```graphql
mutation CreateContact {
  createContact(input: {
    name: "John Doe"
    email: "john.doe@example.com"
    phone: "+1234567890"
    category: "Professional"
    address: "123 Main St, City, Country"
  }) {
    id
    name
    email
    phone
    category
    address
    createdAt
  }
}
```

#### List Contacts
```graphql
query ListContacts {
  contacts(page: 0, pageSize: 20, sortBy: "name", sortOrder: ASC) {
    content {
      id
      name
      email
      phone
      category
      address
      createdAt
      updatedAt
    }
    pageNumber
    pageSize
    totalElements
    totalPages
    hasNext
    hasPrevious
  }
}
```

#### Search Contacts
```graphql
query SearchContacts {
  searchContacts(filter: {
    name: "john"
    category: "Professional"
  }) {
    id
    name
    email
    phone
    category
    address
  }
}
```

#### Update Contact
```graphql
mutation UpdateContact($id: ID!) {
  updateContact(id: $id, input: {
    name: "John Smith"
    email: "john.smith@example.com"
    category: "Personal"
  }) {
    id
    name
    email
    category
    updatedAt
  }
}
```

#### Delete Contact
```graphql
mutation DeleteContact($id: ID!) {
  deleteContact(id: $id)
}
```

### Category Management

#### Create Category
```graphql
mutation CreateCategory {
  createCategory(input: {
    name: "VIP Clients"
    description: "High-value client contacts"
  }) {
    id
    name
    description
    createdAt
  }
}
```

#### List Categories
```graphql
query ListCategories {
  categories(page: 0, pageSize: 20, sortBy: "name", sortOrder: ASC) {
    content {
      id
      name
      description
      createdAt
      updatedAt
    }
    pageNumber
    pageSize
    totalElements
  }
}
```

### Real-time Subscriptions

#### Subscribe to Contact Creation
```graphql
subscription OnContactCreated {
  contactCreated {
    id
    name
    email
    phone
    category
    createdAt
  }
}
```

#### Subscribe to Contact Updates
```graphql
subscription OnContactUpdated {
  contactUpdated {
    id
    name
    email
    phone
    category
    updatedAt
  }
}
```

## GraphQL Resolver Implementation

### Query Resolver Example
```java
@Component
public class QueryResolver implements GraphQLQueryResolver {
    
    @Autowired
    private ContactService contactService;
    
    @PreAuthorize("isAuthenticated()")
    public Page<Contact> contacts(int page, int pageSize, String sortBy, SortOrder sortOrder) {
        // Implementation
    }
    
    @PreAuthorize("isAuthenticated()")
    public Contact contact(String id) {
        return contactService.getContactById(id);
    }
}
```

### Mutation Resolver Example
```java
@Component
public class MutationResolver implements GraphQLMutationResolver {
    
    @Autowired
    private ContactService contactService;
    
    @PreAuthorize("isAuthenticated()")
    public Contact createContact(CreateContactInput input) {
        // Implementation
    }
}
```

### Subscription Resolver Example
```java
@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {
    
    @Autowired
    private ContactEventPublisher eventPublisher;
    
    public Publisher<Contact> contactCreated() {
        return eventPublisher.getContactCreatedPublisher();
    }
}
```

## Error Handling

GraphQL errors are returned in a standard format:

```json
{
  "errors": [
    {
      "message": "Contact not found",
      "path": ["contact"],
      "extensions": {
        "classification": "NOT_FOUND",
        "code": "RESOURCE_NOT_FOUND"
      }
    }
  ]
}
```

## Configuration

### GraphQL Configuration
```properties
# GraphQL settings
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
spring.graphql.path=/graphql
spring.graphql.websocket.path=/graphql-ws

# Security
spring.graphql.cors.allowed-origins=*
spring.graphql.cors.allowed-methods=*
spring.graphql.cors.allowed-headers=*
```

### Multi-tenant Configuration
```properties
# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017

# Multi-Tenancy Configuration
multitenancy.default-tenant=default
```

## Testing GraphQL APIs

### Using GraphQL Playground
1. Navigate to `http://localhost:8080/graphiql`
2. Set authentication headers
3. Write and execute queries/mutations
4. View schema documentation

### Using cURL
```bash
# Query example
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{"query":"query { contacts { content { id name email } } }"}' \
  http://localhost:8080/graphql

# Mutation example
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{"query":"mutation { createContact(input: {name: \"John Doe\", email: \"john@example.com\"}) { id name } }"}' \
  http://localhost:8080/graphql
```

### Using Apollo Client (JavaScript)
```javascript
import { ApolloClient, InMemoryCache, createHttpLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';

const httpLink = createHttpLink({
  uri: 'http://localhost:8080/graphql',
});

const authLink = setContext((_, { headers }) => {
  return {
    headers: {
      ...headers,
      authorization: 'Basic YWRtaW46YWRtaW4=',
    }
  }
});

const client = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache()
});
```

## Security Features

- Authentication required for all GraphQL operations
- Role-based access control (ADMIN/USER)
- Tenant isolation through authenticated context
- Query depth limiting to prevent malicious queries
- Rate limiting for GraphQL operations
- Field-level security with directives

## Performance Optimization

- DataLoader pattern for N+1 query problem
- Query complexity analysis
- Pagination for large datasets
- Caching strategies
- Database indexing

## Monitoring and Metrics

```properties
# Enable GraphQL metrics
management.metrics.graphql.autotime.enabled=true
management.metrics.export.prometheus.enabled=true
```

## Troubleshooting

### Common Issues

1. **Authentication Errors**
   ```json
   {
     "errors": [
       {
         "message": "Unauthorized",
         "extensions": {
           "classification": "UNAUTHORIZED"
         }
       }
     ]
   }
   ```
    - Verify Authorization header is set correctly
    - Check user credentials

2. **Query Depth Exceeded**
    - Simplify nested queries
    - Use pagination for large datasets

3. **Subscription Connection Issues**
    - Check WebSocket configuration
    - Verify WebSocket endpoint URL

## Future Enhancements

- Federation for microservices
- Advanced caching with Redis
- GraphQL schema stitching
- Custom directives for validation
- File upload support
- Query batching
- Persisted queries

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact:
- Email: support@example.com
- GitHub: https://github.com/yourusername/contacts-management-system
