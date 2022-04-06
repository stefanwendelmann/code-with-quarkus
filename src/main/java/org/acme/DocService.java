package org.acme;

import io.quarkus.runtime.util.ExceptionUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Singleton
public class DocService
{
  private static final Logger LOG = Logger.getLogger(DocService.class);
  final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");

  @ConfigProperty(name = "document.path")
  String docPath;

  public void save(List<FileUpload> uploadFiles)
  {
    if (uploadFiles != null && !uploadFiles.isEmpty())
    {
      for (FileUpload f :
              uploadFiles)
      {
        String filePath = docPath + fileNameDateFormat.format(new Date()) + "_" + f.fileName().replaceAll("[^a-zA-Z0-9\\._]+", "_");
        try
        {
          Files.copy(f.filePath(), new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e)
        {
          LOG.error(ExceptionUtil.rootCauseFirstStackTrace(e));
        }
      }
    }
  }

//  /**
//   * Speichert Dateien aus Input Parts zu Kategorien.
//   *
//   * @param uploadRequest
//   * @param inputParts
//   * @return true if ok
//   */
////  @Transactional
//  public boolean save(
////          UploadRequests uploadRequests,
//          List<InputPart> inputParts) throws Exception
//  {
//    if (inputParts != null)
//    {
//      for (InputPart inputPart : inputParts)
//      {
//        MultivaluedMap<String, String> header = inputPart.getHeaders();
//
//        String fileName = getFileName(header);
//        String contentType = getContentType(header);
//
//// Overwrite Filename from Request Body
////        UploadRequest ur = null;
////        if (uploadRequests != null)
////        {
////          ur = uploadRequests.getUploadRequestForOriginalName(fileName);
////        }
////        if (ur != null && ur.getNewName() != null && !ur.getNewName().isEmpty())
////        {
////          fileName = ur.getNewName();
////        }
//
//        // convert the uploaded file to inputstream
//        try (InputStream inputStream = inputPart.getBody(InputStream.class, null))
//        {
//
//          // constructs upload file path
//          String filePath = docPath + fileNameDateFormat.format(new Date()) + "_" + fileName.replaceAll("[^a-zA-Z0-9\\._]+", "_");
//
//          Long fileSize = writeFile(inputStream, new File(filePath));
//
//          // Persist to DB
////          Dokumente ad = new Dokumente();
////          ad.setContenttype(contentType);
////          ad.setSizebyte(fileSize);
////          ad.setDateiname(fileName);
////          ad.setSystempath(filePath);
////          ad.setLastchangeuser(jwt.getSubject());
////          ad.setLastchangedate(new Date());
////          save(ad);
////          updateEtag(ad);
//        } catch (Exception e)
//        {
//          LOG.error(ExceptionUtil.rootCauseFirstStackTrace(e));
//        }
//      }
//    }
//    return true;
//  }

  /**
   * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
   * name="file"; filename="filename.extension"] }
   */
  // get uploaded filename, is there a easy way in RESTEasy?
  public String getFileName(MultivaluedMap<String, String> header)
  {

    String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
    String filename = "unknown";
    try
    {
      for (String cdp : contentDisposition)
      {
        if (cdp.trim().startsWith("filename="))
        {
          // RFC5987 ergänzung
          String s = cdp.trim().substring(9);

          if (s.startsWith("\""))
          {
            s = s.substring(1);
          }
          if (s.endsWith("\""))
          {
            s = s.substring(0, s.length() - 1);
          }
          if (s.startsWith("UTF-8''"))
          {
            String charset = s.split("''")[0];
            s = s.split("''")[1];

            filename = URLDecoder.decode(s, charset);
          } else
          {
            String[] name = cdp.split("=");
            filename = name[1].trim().replaceAll("\"", "");
          }
        } else
        {
          // RFC5987
          if (cdp.trim().startsWith("filename*="))
          {
            String charset = cdp.trim().substring(10).split("''")[0];
            String s = cdp.trim().substring(10).split("''")[1];
            if (s.endsWith("\""))
            {
              s = s.substring(0, s.length() - 1);
            }
            filename = URLDecoder.decode(s, charset);
          }
        }
      }
    } catch (Exception e)
    {
      LOG.error(e);
    }

    return filename;
  }

  /**
   * Get Content Type from Header header sample { Content-Type=[image/png],
   * Content-Disposition=[form-data; name="file"; filename="filename.extension"]
   * }
   */
  private String getContentType(MultivaluedMap<String, String> header)
  {
    String contentType = header.getFirst("Content-Type");
    if (contentType != null && !contentType.isEmpty())
    {
      return contentType;
    } else
    {
      return "unknown";
    }
  }

  /**
   * Write the File to the Destination
   *
   * @param content
   * @param filename
   * @return Size of the File
   * @throws IOException
   */
  private Long writeFile(InputStream is, File f) throws IOException
  {
    if (!f.exists())
    {
      Files.copy(is, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
      return f.length();
    } else
    {
      return null;
    }
  }

}
