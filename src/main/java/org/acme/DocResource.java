package org.acme;

import org.jboss.resteasy.reactive.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/docs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocResource
{
  @Inject
  DocService docService;

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response save(@MultipartForm FormData formData) throws Exception
  {
    docService.save(formData.getUploadFiles());
    return Response.noContent().build();
  }
}
