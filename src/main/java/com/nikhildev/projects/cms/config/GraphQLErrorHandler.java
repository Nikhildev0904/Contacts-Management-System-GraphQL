package com.nikhildev.projects.cms.config;

import com.nikhildev.projects.cms.exceptions.ResourceAlreadyExistsException;
import com.nikhildev.projects.cms.exceptions.ResourceNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


@Component
public class GraphQLErrorHandler implements DataFetcherExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLErrorHandler.class);

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        GraphQLError error;
        if (exception instanceof ResourceNotFoundException) {
            logger.error("Resource not found: {}", exception.getMessage());
            error = GraphqlErrorBuilder.newError()
                    .message("Resource not found: " + exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        } else if (exception instanceof ResourceAlreadyExistsException) {
            logger.error("Resource already exists: {}", exception.getMessage());
            error = GraphqlErrorBuilder.newError()
                    .message("Resource already exists: " + exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        } else if (exception instanceof AccessDeniedException) {
            logger.error("Access denied: {}", exception.getMessage());
            error = GraphqlErrorBuilder.newError()
                    .message("Access denied: You don't have permission to perform this operation")
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        } else if (exception instanceof SecurityException) {
            logger.error("Security exception: {}", exception.getMessage());
            error = GraphqlErrorBuilder.newError()
                    .message("Authentication required")
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        } else {
            logger.error("Unexpected error in GraphQL request", exception);
            error = GraphqlErrorBuilder.newError()
                    .message("Internal server error: " + exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();
        }
        return Mono.just(Collections.singletonList(error));
    }
}