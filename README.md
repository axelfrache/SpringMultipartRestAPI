# SpringMultipartRestAPI

SpringMultipartRestAPI is a Spring Boot application designed to facilitate the uploading, downloading, and deleting of files through a RESTful API. This project leverages Spring's powerful features to handle multipart file uploads, serving files, and managing file deletion securely and efficiently.

## Features
- **File Upload:** Supports uploading one or multiple files simultaneously.
- **File Download:** Allows downloading files by providing a direct download link.
- **File Deletion:** Enables the deletion of files from the server.
- **List Files:** Lists all uploaded files with their respective download URLs.

## Quick Start

To get started with SpringMultipartRestAPI, ensure you have Java and Maven installed on your system. Follow these steps to run the application:

**Clone the repository:**
```bash
git clone https://github.com/yourusername/SpringMultipartRestAPI.git
cd SpringMultipartRestAPI
```

**Run the application using Maven:**
```bash
mvn spring-boot:run
```

This command will start the Spring Boot application on the default port (8080). 
You can access the application at http://localhost:8080.

## API Endpoints

The application defines the following RESTful endpoints:

- **Upload Files: POST /api/savesync/upload**
  - Accepts multipart/form-data requests with one or more files.
- **List Files: GET /api/savesync/files**
  - Returns a list of all files uploaded to the server, including their download URLs.
- **Download File: GET /api/savesync/files/{fileName}**
  - Downloads the specified file. Replace {fileName} with the actual name of the file you wish to download.
- **Delete File: DELETE /api/savesync/files/{fileName}**
  - Deletes the specified file from the server.

## Usage Example

- **Uploading a file:**

    Use a tool like Postman or a CURL command to upload a file:
    ```bash
    curl -F "file=@path/to/your/file.txt" http://localhost:8080/api/savesync/upload
    ```

- **Downloading a file:**

    Access the provided URL from the list files endpoint or use a tool like wget:
    ```bash
    wget http://localhost:8080/api/savesync/files/yourfile.txt
    ```
  
- **Deleting a file:**

  Use a CURL command to delete a file:
  ```bash
  curl -X DELETE http://localhost:8080/api/savesync/files/yourfile.txt
  ```

## Contributing
Contributions are welcome! Please feel free to submit a pull request or create an issue for any feature requests or bug reports.
