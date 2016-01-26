package org.openpaas.ieda.web.information.stemcell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class FileUploadRequestEntity implements RequestEntity {

    final File file;
    final String contentType;
    final String messageEndpoint;
    final SimpMessagingTemplate messagingTemplate;
    
    public FileUploadRequestEntity(final File file, final String contentType,final SimpMessagingTemplate messagingTemplate, final String messageEndpoint) {
        super();
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        this.contentType = contentType;
        this.messagingTemplate = messagingTemplate;
        this.messageEndpoint = messageEndpoint;
    }
    
    public long getContentLength() {
        return this.file.length();
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getMessageEndpoint() {
        return this.messageEndpoint;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(final OutputStream out) throws IOException {
    	
        byte[] tmp = new byte[8192];
        int i = 0;
        int accumulatePercent = -1;
        double accumulate = 0;
        double totalSize = this.file.length();
        InputStream instream = new FileInputStream(this.file);
        try {
            while ((i = instream.read(tmp)) >= 0) {
            	accumulate += i;
            	out.write(tmp, 0, i);
            	
            	if ( accumulatePercent != (int)(accumulate/totalSize *100 )){
            		accumulatePercent = (int)(accumulate/totalSize * 100); 
            		DirectorRestHelper.sendTaskOutputWithTag(messagingTemplate, messageEndpoint, "Progress", this.file.getName(), Arrays.asList( String.valueOf(accumulatePercent) ));
            	}
            }        
        } finally {
            instream.close();
        }
    }    
    
}