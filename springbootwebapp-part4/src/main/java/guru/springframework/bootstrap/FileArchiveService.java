package guru.springframework.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import guru.springframework.domain.CustomerImage;


@Service
public class FileArchiveService {
	
	
	@Autowired
	private AmazonS3Client s3Client;

	private static final String S3_BUCKET_NAME = "xdsb-test1-aws-demo";


	/**
	 * Save image to S3 and return CustomerImage containing key and public URL
	 * 
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	public CustomerImage saveFileToS3(MultipartFile multipartFile) throws Exception {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJHHKYSPED4DR6MKA", "eMWqcX5bvr3U9OP9jWber5vYT4wpLvPHYG5jC8hY");
		s3Client = new AmazonS3Client(credentials);
//		s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		try{
			File fileToUpload = convertFromMultiPart(multipartFile);
			
			System.out.println(s3Client.getRegion());
//			for (Bucket bucket : s3Client.listBuckets()) {
//				System.out.println(" - " + bucket.getName());
//			}
			
			String key = Instant.now().getEpochSecond() + "_" + fileToUpload.getName();
			
			System.out.println("key is: "+ key + " path is: "+ fileToUpload.getAbsolutePath());
			System.out.println("111111111111111");
			/* save file */
			
			PutObjectRequest obj = new PutObjectRequest(S3_BUCKET_NAME, key, fileToUpload);
			System.out.println("555555555555555555555555555555555555555555");
			System.out.println(obj.getKey());
			
			s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, key, fileToUpload));

			System.out.println("22222222222222222");
			/* get signed URL (valid for one year) */
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(S3_BUCKET_NAME, key);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(DateTime.now().plusYears(1).toDate());
			System.out.println("33333333333333333333333333");
			URL signedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest); 
			
			return new CustomerImage(key, signedUrl.toString());
		}
		catch(Exception ex){			
			throw new Exception("An error occurred saving file to S3", ex);
		}		
	}

	/**
	 * Delete image from S3 using specified key
	 * 
	 * @param customerImage
	 */
	public void deleteImageFromS3(CustomerImage customerImage){
		s3Client.deleteObject(new DeleteObjectRequest(S3_BUCKET_NAME, customerImage.getKey()));	
	}

	/**
	 * Convert MultiPartFile to ordinary File
	 * 
	 * @param multipartFile
	 * @return
	 * @throws IOException
	 */
	private File convertFromMultiPart(MultipartFile multipartFile) throws IOException {

		File file = new File(multipartFile.getOriginalFilename());
		file.createNewFile(); 
		FileOutputStream fos = new FileOutputStream(file); 
		fos.write(multipartFile.getBytes());
		fos.close(); 

		return file;
	}
}
