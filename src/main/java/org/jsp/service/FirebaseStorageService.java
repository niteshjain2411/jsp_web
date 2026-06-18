package org.jsp.service;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FirebaseStorageService {
    private static final String BUCKET_NAME = "jain-sangh-pune-5c864.firebasestorage.app";

    /**
     * Uploads a multipart file to Firebase Storage under a specified folder path.
     * * @param multipartFile The file object received from the controller
     *
     * @param folderPath The target folder inside the bucket (e.g., "resumes/")
     * @throws IOException If file reading or network transmission fails
     */
    public void uploadFile(MultipartFile multipartFile, String fileName, String folderPath) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        // 1. Initialize Storage Options (Uses your application's service account credentials automatically)
        final Storage storage = StorageOptions.getDefaultInstance().getService();//This is required when running on a server or cloud environment
//        final Storage storage = StorageClient.getInstance().bucket(BUCKET_NAME).getStorage();//This line is required when running locally

        // 2. Generate a unique file name to avoid overwriting existing files
        final String originalFileName = multipartFile.getOriginalFilename();
        final String uniqueFileName = folderPath + fileName;

        // 3. Define Blob identity and configuration metadata
        final BlobId blobId = BlobId.of(BUCKET_NAME, uniqueFileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(multipartFile.getContentType())
                // Optional: setting content disposition allows browsers to preview PDFs instead of forcing a download
                .setContentDisposition("inline; filename=\"" + originalFileName + "\"")
                .build();

        // 4. Execute the upload to Firebase
        storage.create(blobInfo, multipartFile.getBytes());
    }

    public boolean deleteFile(String fileName) {
        Storage storage = StorageClient.getInstance().bucket(BUCKET_NAME).getStorage();
        BlobId blobId = BlobId.of(BUCKET_NAME, "resumes/" + fileName);
        return storage.delete(blobId);
    }

    public byte[] downloadFile(String path, String fileName) {
        Storage storage = StorageClient.getInstance().bucket(BUCKET_NAME).getStorage();
        BlobId blobId = BlobId.of(BUCKET_NAME, path + fileName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            throw new RuntimeException("File not found: " + fileName);
        }
        return blob.getContent();
    }
}