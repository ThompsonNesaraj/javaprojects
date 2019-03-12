package com.periscope.rest;

import java.net.HttpURLConnection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.periscope.service.ServiceException;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

/**
 * A convience method that wraps the WebApplicationException
 * Inputs are Response.Status code (400 - Bad Request, 404 - Not Found, 204 - No Content, 401 - Unauthorized)
 * @author mpasko
 *
 */
public class RestApplicationException extends WebApplicationException {
	private static final long serialVersionUID = 1738879526288628366L;

	public RestApplicationException(ServiceException e) {
		super(new ResponseBuilderImpl().status(e.getWebHttpStatus()).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build());
	}
	
	public RestApplicationException(Exception e) {
		super(new ResponseBuilderImpl().status(HttpURLConnection.HTTP_INTERNAL_ERROR).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build());
	}
	
	public RestApplicationException(Response.Status status, String message) {
		super(new ResponseBuilderImpl().status(status).type(MediaType.TEXT_PLAIN).entity(message).build());
	}
	
	public RestApplicationException(Response.Status status, String message, Throwable e) {
		super(e,new ResponseBuilderImpl().status(status).type(MediaType.TEXT_PLAIN).entity(message).build());
	}
	
	public RestApplicationException(Response.Status status, Throwable e) {
		super(e,new ResponseBuilderImpl().status(status).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build());
	}
}
