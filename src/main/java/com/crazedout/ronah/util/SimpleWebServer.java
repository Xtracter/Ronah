package com.crazedout.ronah.util;

import com.crazedout.ronah.Ronah;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.service.AutoRegisterService;

import java.io.File;
import java.util.logging.Logger;

/**
 * Class to support implementation of Web Server functionality.
 * Service should inherit this class and use its doGET() or doPOST() functions.
 */
@SuppressWarnings("unused")
public abstract class SimpleWebServer extends AutoRegisterService {

    private final static Logger logger = Logger.getLogger(SimpleWebServer.class.getName());
    private String basePath;

    /**
     * Creates a SimpleWebServer with basePath to file www area.
     * Base path is System.getProperty("user.dir")
     */
    public SimpleWebServer(){
        super();
        this.basePath = System.getProperty("user.dir");
    }

    public void setBasePath(String path){
        this.basePath = path;
    }

    protected String[] defaultPage = {"index.html","index.htm","index.php"};

    /**
     * Handle a GET HTTP request.
     * Context path is set to Context Path + / + GET(path="{path}")
     * @param request HttpRequest request
     */
    public File getFile(HttpRequest request){

        String contextPath = request.getPath();
        if("true".equals(System.getProperty("ronah.debug"))){
            logger.info("Path:" + contextPath);
        }
        if(!isFile(contextPath) &&
                (contextPath.isEmpty() || contextPath.charAt(contextPath.length()-1)!='/')) contextPath+="/";

        String file = "";
        int i = contextPath.lastIndexOf("/");
        if(i!=-1){
            file = contextPath.substring(i);
            if('/'==file.charAt(0)) file = file.substring(1);
        }
        contextPath = contextPath.substring(0,contextPath.length()-file.length());

        String systemFile = getRequestedFile(basePath + contextPath, file);
        if(systemFile==null) return null;
        return new File(systemFile);
    }

    private boolean isFile(String contextPath){
        try {
            String[] tok = contextPath.split("/");
            return tok[tok.length - 1].contains(".");
        }catch(ArrayIndexOutOfBoundsException ex){
            return false;
        }
    }

    private String getRequestedFile(String filePath, String file){
        File f;
        System.out.println(filePath + " " + file );

        if(file.isEmpty()){
            for (String s : defaultPage) {
                f = new File((filePath + File.separatorChar + s).replace('/', File.separatorChar));
                if("true".equals(System.getProperty("ronah.debug"))) {
                    logger.info(f.getAbsolutePath());
                }
                if (f.exists()) {
                    if("true".equals(System.getProperty("ronah.debug"))) {
                        logger.info(f.getAbsolutePath());
                    }
                    return f.getAbsolutePath();
                }
            }
        }else{
            f  = new File((filePath + file).replace('/',File.separatorChar));
            System.out.println("2:" + f.getAbsolutePath());
            if(f.exists()) return f.getAbsolutePath();
        }
        return null;
    }
}
