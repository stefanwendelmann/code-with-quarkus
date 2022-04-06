package org.acme;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public class FormData
{
  @RestForm("uploadFiles")
  public List<FileUpload> uploadFiles;

  public List<FileUpload> getUploadFiles()
  {
    return uploadFiles;
  }

  public void setUploadFiles(List<FileUpload> uploadFiles)
  {
    this.uploadFiles = uploadFiles;
  }
}
