package org.acme;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/docs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocResource
{
  @Inject
  DocService docService;

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
//  @Transactional
//  @TransactionConfiguration(timeout = Integer.MAX_VALUE)
  public Response save(MultipartFormDataInput input) throws Exception
  {
    try
    {
      Map<String, List<InputPart>> formParts = input.getFormDataMap();
//      UploadRequests n = null;
//      if (formParts.get("upload") != null)
//      {
//        n = formParts.get("upload").get(0).getBody(new GenericType<UploadRequests>()
//        {
//        });
//      }

      if (formParts.get("uploadFiles") != null)
      {
        docService.save(
//                n,
                formParts.get("uploadFiles"));
      }
      return Response.noContent().build();
    }
    catch (Exception e)
    {
      throw e;
    }
    finally
    {
      /**
       * Call this method to delete any temporary files created from
       * unmarshalling this multipart message Otherwise they will be deleted on
       * Garbage Collection or JVM exit.
       */
      input.close();
    }
  }
}
