package com.kinny.util;

import org.csource.fastdfs.*;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author qgy
 * @create 2019/5/16 - 19:44
 */
public class FastDfsUtil {

    private TrackerClient trackerClient;

    private TrackerServer trackerServer;

    private StorageClient storageClient;

    private StorageServer storageServer;

    static {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("properties/application");

        try {
            ClientGlobal.init(resourceBundle.getString("fastDfs.confUrl"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String  upload(byte[] bytes, String ext) {

        FastDfsUtil fastDfsUtil = new FastDfsUtil();

        try {
            String[] strings = fastDfsUtil.storageClient.upload_file(bytes, ext, null);
            return strings[0] + "/" + strings[1];
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     *  finish 初始化 外界无法访问
     */
    private FastDfsUtil() {

        this.trackerClient = new TrackerClient();
        try {
            this.trackerServer= this.trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.storageClient = new StorageClient(this.trackerServer, this.storageServer);

    }




}
