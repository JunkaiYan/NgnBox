package backend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class myServlet
 */
public class S3Servlet extends HttpServlet {
	
	private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
	private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
	private static final long serialVersionUID = 1L;
	private static final String BucketName = "ngn-s3-project";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	DBcontroller db;
	S3controller s3;
    public S3Servlet() {
        super();
        db = new DBcontroller();
        db.setConnnection();
        s3 = new S3controller();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter writer = response.getWriter();
		response.addHeader("Access-Control-Allow-Origin", "*");
		String action = request.getParameter("action");
		String username = request.getParameter("username");;
		String password = null;
		if(action.equals("listfiles")){
			String files = db.getMessage(username);
			if(files == null){
				writer.write("null");
			}else{
				writer.write(files);
			}	
		}else if(action.equals("login")){ 
			password = request.getParameter("password");
			String pwd = db.getPassword(username);
			if(pwd != null && password.equals(pwd)){
				writer.write("success");
			}else{
				writer.write("fail");
			}			
		}else if(action.equals("signup")){
			password = request.getParameter("password");
			if(db.insertUser(username, password)){
				writer.write("success");
			}else{
				writer.write("fail");
			}
		}else if(action.equals("delete")){
			String file = request.getParameter("file");
			String filename = username + "/" + file;
			db.deleteEntry(username, file);
			s3.deleteFile(filename);
			writer.write("success");
		}else if(action.equals("download")){
			String file = request.getParameter("file");
			String filename = username + "/" + file;
			String link = s3.download(filename);
			writer.write(link);
		}
		response.flushBuffer();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter writer = response.getWriter();
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            writer.append("Error: Form must has enctype=multipart/form-data.");
            response.flushBuffer();
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        
		String fileName = "error";
        try {
            // parses the request's content to extract file data
//            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
            	String username = "default";
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields      	
                    if (!item.isFormField()) {
                        fileName = new File(item.getName()).getName();
                        String filePath = username + File.separator + fileName;
                        File storeFile = new File(filePath); 

                        //check whether the user have uploaded this file
                        if(fileName == null || fileName == ""){
                        	writer.write("error");
                        	response.flushBuffer();
                        	return;
                        }
                        
                        if(!db.insertFile(username, fileName, new Timestamp(System.currentTimeMillis()))){
                        	writer.write("Existed");
                        	response.flushBuffer();
                        	return;
                        }
                        item.write(storeFile);
                        s3.upload(filePath, BucketName, filePath);
                        storeFile.delete();

                        System.out.println("Upload has been done successfully");
                    }else{
                    	username = item.getString();
                        File uploadDir = new File(username);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                    }
                }
            }
        } catch (Exception ex) {
        	writer.write("error");
        	response.flushBuffer();
        	return;
        } 
        writer.append(fileName);
        response.flushBuffer();
	}
}
