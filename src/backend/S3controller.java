package backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

public class S3controller {
	AmazonS3 s3Client;
	private static final String bucketName = "ngn-s3-project";
	public S3controller() {
		AWSCredentials credentials;
		try {
			credentials = new PropertiesCredentials(
			         S3controller.class.getResourceAsStream("../AwsCredentials.properties"));
			s3Client = new AmazonS3Client(credentials);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void deleteFile(String keyName) {
		try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	public List<String> getUserFiles (String bucketName, String username) {
		List<String> fileNames = new ArrayList<>();
    	ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
    	.withBucketName(bucketName)
    	.withPrefix(username);
    	ObjectListing objectListing;
    	
    	do {
    		objectListing = s3Client.listObjects(listObjectsRequest);
    		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
    			String[] path = objectSummary.getKey().split("/");
    			fileNames.add(path[path.length - 1]);
    		}
    		listObjectsRequest.setMarker(objectListing.getNextMarker());
    	} while 
    		(objectListing.isTruncated());
    	
    	return fileNames;       	
	}
	
	public String download(String filename) {
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filename);
		return String.valueOf(s3Client.generatePresignedUrl(request));
	}
	
	public void upload (String filePath, String existingBucketName, String keyName) {
			
		List<PartETag> partETags = new ArrayList<PartETag>();
		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(		                                                 	existingBucketName, keyName);
		InitiateMultipartUploadResult initResponse = 
		                              s3Client.initiateMultipartUpload(initRequest);
		File file = new File(filePath);

		long contentLength = file.length();
		long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.

		
		try {
		    // Step 2: Upload parts.
		    long filePosition = 0;
		    for (int i = 1; filePosition < contentLength; i++) {
		        // Last part can be less than 5 MB. Adjust part size.
		    	partSize = Math.min(partSize, (contentLength - filePosition));
		    	
		        // Create request to upload a part.
		        UploadPartRequest uploadRequest = new UploadPartRequest()
		            .withBucketName(existingBucketName).withKey(keyName)
		            .withUploadId(initResponse.getUploadId()).withPartNumber(i)
		            .withFileOffset(filePosition)
		            .withFile(file)
		            .withPartSize(partSize);

		        // Upload part and add response to our list.
		        partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

		        filePosition += partSize;
		    }

		    // Step 3: Complete.
		    CompleteMultipartUploadRequest compRequest = new 
		                CompleteMultipartUploadRequest(existingBucketName, 
		                                               keyName, 
		                                               initResponse.getUploadId(), 
		                                               partETags);

		    s3Client.completeMultipartUpload(compRequest);
		} catch (Exception e) {
		    s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
		              existingBucketName, keyName, initResponse.getUploadId()));
		}
	}
}
