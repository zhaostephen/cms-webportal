package com.capitalcrux.cms.rest.api;

import com.capitalcrux.cms.JsonViews;
import com.capitalcrux.cms.dao.document.DocumentDao;
import com.capitalcrux.cms.entity.Document;
import com.capitalcrux.cms.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Component
@Path("/documents")
public class DocumentApi
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private ObjectMapper mapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list() throws IOException
    {
        this.logger.info("list()");

        ObjectWriter viewWriter;
        if (this.isAdmin()) {
            viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
        } else {
            viewWriter = this.mapper.writerWithView(JsonViews.User.class);
        }
        List<Document> allEntries = this.documentDao.findAll();

        return viewWriter.writeValueAsString(allEntries);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Document read(@PathParam("id") Long id)
    {
        this.logger.info("read(id)");

        Document document = this.documentDao.find(id);
        if (document == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return document;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Document create(Document document)
    {
        this.logger.info("create(): " + document);

        return this.documentDao.save(document);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Document update(@PathParam("id") Long id, Document document)
    {
        this.logger.info("update(): " + document);

        return this.documentDao.save(document);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public void delete(@PathParam("id") Long id)
    {
        this.logger.info("delete(id)");

        this.documentDao.delete(id);
    }

    private boolean isAdmin()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return false;
        }

        UserDetails userDetails = (UserDetails) principal;

        return userDetails.getAuthorities().contains(Role.ADMIN);
    }
}
