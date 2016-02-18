package com.example.ephow.tabtest.food;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;



/**
 * Created by 1 on 2015/8/23.
 */

public class CmenuFTP {
    //STR效率低,待优化为整数
    public static final String FTP_CONNECT_SUCCESSS = "FTP连接成功";
    public static final String FTP_CONNECT_FAIL = "FTP连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "FTP断开连接";
    public static final String FTP_FILE_NOTEXISTS = "FTP数据不存在";
    public static final String FTP_DOWN_LOADING = "数据更新中";
    public static final String FTP_SINGLE_DOWN_LOADING = "更新中"; //单文件带进度下载
    public static final String FTP_DOWN_SUCCESS = "成功更新";
    public static final String FTP_DIR_NOTEXISTS = "FTP文件夹不存在";
    public static final String FTP_SUCCESS = "数据无需更新";
    public static final String FTP_DOWN_FAIL = "更新失败";

    public static final int DOWN_DIRS = 0x10;
    public static final int DOWN_FILE = 0x20;
    public static final int DOWN_FILES = 0x30;

    // 服务器名
    private String hostName;
    // 端口号
    private int serverPort;
    // 用户名
    private String userName;
    // 密码
    private String password;
    // FTP连接
    private FTPClient ftp;

    //FTP基础路径
    private String baseremotePath;
    //LOCAL基础路径
    private String baselocalPath;

    //文件序号,总数
    private long fileIdx;
    private long fileCnt;

    public CmenuFTP() {
        this.hostName = "192.168.0.111";
        this.serverPort = 21;
        this.userName = "ipad";
        this.password = "ipad";
        this.ftp = new FTPClient();
    }

    public CmenuFTP(CmenuJson cj) {
        this.hostName = cj.getValue("ftp","addr");
        this.serverPort = Integer.parseInt(cj.getValue("ftp","port"));
        this.userName = cj.getValue("ftp","user");
        this.password = cj.getValue("ftp","pass");
        this.ftp = new FTPClient();
    }

    // -------------------------------------------------------打开关闭连接------------------------------------------------

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftp.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftp.connect(hostName, serverPort);
        // 获取响应值
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftp.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftp.login(userName, password);
        // 获取响应值
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftp.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftp.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftp.configure(config);
            // 使用被动模式设为默认
            ftp.enterLocalPassiveMode();
            // 二进制文件支持
            ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    private void closeConnect() throws IOException {
        if (ftp != null) {
            // 退出FTP
            ftp.logout();
            // 断开连接
            ftp.disconnect();
        }
    }

    // -------------------------------------------------------FTP下载方法------------------------------------------------

    /**
     * 下载单个文件，可实现断点下载.
     *
     //* @param serverPath
     *            Ftp目录及文件路径
     * @param localPath
     *            本地目录
     * @paramfileName
     *            下载之后的文件名称
     * @param listener
     *            监听器
     * @throws IOException
     */


    public long DownloadAny(String remotePath, String localPath, int downType, String[] filter, ProgressListener listener)
            throws Exception{
        // 打开FTP服务
        try {
            this.openConnect();
            listener.onProgress(FTP_CONNECT_SUCCESSS);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onProgress(FTP_CONNECT_FAIL);
            return -1;
        }
        //获取FTP基础路径 /user => /user/DIR
        baseremotePath = ftp.printWorkingDirectory();
        //获取本地基础路径
        baselocalPath = localPath;

        //如果远程目录非一级目录,则本地目录只取子目录 /FDIR/SUBDIR => /SUBDIR
        //if ( remotePath.indexOf("/") != remotePath.lastIndexOf("/") )
        //目前没有作路径检测(checkpath),路径参数应为/DIR OR /DIR/DIR2 ,不能为/DIR/ OR /DIR/DIR2/
        //否则会导至localpath = "";
        localPath = remotePath.substring(remotePath.lastIndexOf("/"));

        fileIdx = 0;
        fileCnt = 0;

        switch (downType) {
            case DOWN_DIRS:
                DownloadDirs(remotePath, localPath, filter, listener);
                break;
            case DOWN_FILES:
                DownloadFiles(remotePath, localPath, filter, true, listener);
                break;
            case DOWN_FILE:
        }
        // 下载完成之后关闭连接
        this.closeConnect();
        //listener.onDownLoadProgress(FTP_DISCONNECT_SUCCESS, 0, 0, 0);
        return 1;
    }

    //初步测试通过,待调试
    private boolean DownloadDirs(String remotePath, String localPath, String[] filter, ProgressListener listener)
            throws Exception {
        //获取当前目录
        String currentPath = ftp.printWorkingDirectory();
        //切换到指定相对目录
        if ( !ftp.changeWorkingDirectory(baseremotePath + remotePath) ) {
            listener.onProgress(FTP_DIR_NOTEXISTS + " " + remotePath);
            return false;
        }
        //创建本地文件夹
        File mkFile = new File( baselocalPath + localPath );
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }
        //获取文件
        FTPFile[] files = ftp.listFiles();
        // 判断FTP是否有文件或文件夹存在
        if (files != null && files.length > 0) {
            //文件数量初始为0
            long fCnt = 0;
            //文件夹名
            String dName;
            //文件夹嵌套下载
            for (int i = 0; i < files.length; i++) {
                dName = files[i].getName();
                //是文件夹则调递归检测下层目录
                if (files[i].isDirectory() && !dName.equalsIgnoreCase(".") && !dName.equalsIgnoreCase(".."))
                    DownloadDirs(remotePath + "/" + dName, localPath + "/" + dName, filter, listener);
                else if (files[i].isFile()) fCnt++; //是文件则文件总数增加
            }
            //如果文件夹内有文件
            if ( fCnt > 0 )  {
                //fCnt计数目前还没有其它用处
                fileCnt = fCnt;
                DownloadFiles(remotePath, localPath, filter, true, listener);
            }
        }
        //无文件文件夹存在
        //else listener.onDownLoadProgress(FTP_FILE_NOTEXISTS);
        //返回上一层目录
        return ftp.changeWorkingDirectory( currentPath );
    }

    private long DownloadFiles(String remotePath, String localPath, String[] filter, boolean Schedule, ProgressListener listener)
            throws Exception {
        //对比当前工作路径与指定路径,如不同
        if (!ftp.printWorkingDirectory().substring(baseremotePath.length()).equals(remotePath)) {
            //切换到指定相对目录
            if ( !ftp.changeWorkingDirectory(baseremotePath + remotePath) ) {
                //如无文件夹就报错
                listener.onProgress(FTP_DIR_NOTEXISTS + " " + remotePath);
                return -1;
            }
        }
        //获取文件
        FTPFile[] files = ftp.listFiles();
        if ( files != null && files.length > 0 ) {
            if (fileCnt <= 0) {
                // 判断服务器文件是否存在
                if (files.length == 0) {
                    listener.onProgress(FTP_FILE_NOTEXISTS);
                    return -1;
                }
                //创建本地文件夹
                File mkFile = new File(baselocalPath + localPath);
                if (!mkFile.exists()) {
                    mkFile.mkdirs();
                }
            }
            //文件序号初始化
            this.fileIdx = 0;
            //获取文件总数
            this.fileCnt = files.length;
            for (int i = 0 ; i < files.length; i++)
                //是文件
                if (files[i].isFile()) {
                    //获取文件名
                    String remoteName = files[i].getName();
                    //检查远程文件类型,只下载png和sqlite文件
                    String fileType = remoteName.substring(remoteName.lastIndexOf("."));
                    boolean ok = false;
                    //循环检测文件后缀,符合条件就下载
                    for (int f = 0; f < filter.length; f++) {
                        if ( filter[f].equals(fileType) ) {
                            ok = true;
                            break;
                        }
                    }
                    //如果要过滤的文件类型多的话可以改为String[] Filter用循环过滤文件类型
                    if ( ok ) {  //fileType.equals(".png") || fileType.equals(".sqlite") ) {
                        //合成本地路径和文件名
                        String localName = baselocalPath + localPath + "/" + remoteName;
                        //合成远程路径和文件名
                        remoteName = baseremotePath + remotePath + "/" + remoteName;
                        //获取远程文件的长度
                        long serverSize = files[i].getSize();
                        //获取远程文件修改时间
                        long serverFileTimes = files[i].getTimestamp().getTime().getTime();
                        //本地文件长度初始化为0
                        long localSize = 0;
                        //打开文件
                        File localFile = new File(localName);
                        // 判断本地文件存在
                        if (localFile.exists()) {
                            // 如果本地文件存在
                            // 获取本地文件的长度
                            localSize = localFile.length();
                            //获取本地文件的最后修改时间
                            long lt = localFile.lastModified();
                            /////////////////////////////////////////////
                            //是否有问题待观察
                            // 判断下载的文件是否能断点下载,本地文件SIZE小于远程文件就继续下载
                            if ( localSize >= serverSize ) {
                                //如果本地文件比远程文件新则跳过该文件,文件总数则减一
                                if ( lt > serverFileTimes ) { fileCnt--; continue; }
                                //否则就删掉原文件重新下载
                                localFile.delete();
                                localSize = 0;
                            } else{
                                //远程文件比本地文件新时,同样删掉原文件重新下载
                                if ( lt < serverFileTimes ) {
                                    localFile.delete();
                                    localSize = 0;
                                }
                            }
                            ///////////////////////////////////////////
                        }
                        fileIdx++; //文件数量递增
                        //开始下载文件
                        DownloadSingleFile(remoteName,serverSize,localName,localSize,Schedule,listener);
                        //设置文件日期为FTP上的文件日期
                        localFile.setLastModified(serverFileTimes);
                    }else fileCnt--;   //不是要下载的文件类型总数减一
                }else fileCnt--;   //不是文件,是文件夹什么的,总数减一
        } else return 0;    //文件夹下无文件返回0
        if ( fileCnt != 0 && fileIdx == fileCnt )
            listener.onProgress(FTP_DOWN_SUCCESS + remotePath + "下的:" + fileIdx + "/" + fileCnt + "个文件");
        //文件夹内的文件已最新
        else listener.onProgress(localPath + "文件夹内" + FTP_SUCCESS);
        return fileIdx;
    }



    private boolean DownloadSingleFile(String remoteName, long remoteSize,
                                      String localFile, long localSize,
                                      boolean Schedule, ProgressListener listener)
            throws Exception {
        // 开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        //选择下载方式,显示当前文件下载进度或不带当前文件下载进度
        if ( Schedule ) {
            // 进度
            long step = remoteSize / 100;
            long process = 0;
            long currentSize = 0;
            //设置FTP要下载的文件的断点
            //测试之后远程文件和本地文件的修改时间几乎不可能相同,所以目前也没法断点下载
            ftp.setRestartOffset(localSize);
            //准备输入流
            InputStream input = ftp.retrieveFileStream(remoteName);
            //设置缓冲区
            byte[] b = new byte[1024];
            int length;
            while ((length = input.read(b)) != -1) {
                //out.write(b, (int)localSize, length);
                out.write(b, 0, length);
                currentSize = currentSize + length;
                if (currentSize / step != process) {
                    process = currentSize / step;
                    //每%5的监听一次进度
                    if (process % 5 == 0)
                        listener.onProgress(FTP_SINGLE_DOWN_LOADING + ": 第" + fileIdx + "/" + fileCnt + "个文件" + ",当前文件下载:" + process + "%");
                }
            }
            input.close();
            // 此方法是来确保流处理完毕,必须在retrieveFileStream之后调用，如果没有此方法，可能会造成现程序死掉
            if ( ! ftp.completePendingCommand() ) listener.onProgress(FTP_DOWN_FAIL);
        }else {
            //监听文件下载进度
            listener.onProgress(FTP_DOWN_LOADING + ": 第" + fileIdx + "/" + fileCnt + "个文件");
            //下载文件
            ftp.retrieveFile(remoteName, out);

        }
        out.flush();
        out.close();
        return true;
    }


    // ---------------------------------------------------下载监听---------------------------------------------

    /*
     * 下载进度监听
     */
    public interface ProgressListener {
        public void onProgress(String currentStep);
    }


    // ---------------------------------------删除本地文件夹与文件夹内所有文件--------------------------------------------

    //已测试,暂未增加监听功能
    public void DeleteFiles(File file ,ProgressListener listener) {
        //如果文件存在
        if ( file.exists() ) {
            //如果是文件就删了
            if ( file.isFile() ) file.delete();
            else if ( file.isDirectory() ) {
                //获取文件夹内所有文件
                File[] childFile = file.listFiles();
                if ( childFile != null && childFile.length > 0 )
                    //递归删除子文件或文件夹
                    for (File f : childFile) DeleteFiles(f ,listener);
                file.delete();
            }
        }
    }

    //同步文件及文件夹
    public void SyncAllFiles(File file ,boolean vsi, ProgressListener listener) {

    }


}
