package log;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 基于RollingFileAppender进行优化
 * 基本特性：
 *      1. 在RollingFileAppender的基础上，改用日期作为文件名后缀而不是序号，方便查找
 *      2. 在rollOver时不会重命名文件
 * 实现方式：
 *      1. 重写rollOver方法，更改其中的命名逻辑
 *      2. FILO队列存储backUp的日志文件名，并在rollOver时序列化到磁盘中
 *      3. 系统重启时会尝试读取序列化的FILO队列，并重新加载，否则无法正确检查日志文件数量
 *
 * @author paranoidq
 * @since 1.0.0
 */
public class NamedRollingFileAppender extends RollingFileAppender {


    /**
     * 序列化existedLogFileNames队列的文件名后缀
     */
    private static final String SERIALIZE_POSTFIX = ".restore";

    /**
     * 存储backUp的文件名
     * existedLogFileNames.capacity == maxBackUp
     */
    private LinkedBlockingDeque<String> existedLogFileNames;

    /**
     The default maximum file size is 10MB.
     */
    protected long maxFileSize = 10*1024*1024;

    /**
     * There is one backup file by default.
     * Updated:
     *      将默认backUp的数量改为10，在实际环境中1几乎没什么用
     */
    protected int maxBackupIndex  = 10;

    private long nextRollover = 0;

    /**
     The default constructor simply calls its {@link
    FileAppender#FileAppender parents constructor}.  */
    public NamedRollingFileAppender() {
        super();
    }

    /**
     Instantiate a RollingFileAppender and open the file designated by
     <code>filename</code>. The opened filename will become the ouput
     destination for this appender.

     <p>If the <code>append</code> parameter is true, the file will be
     appended to. Otherwise, the file desginated by
     <code>filename</code> will be truncated before being opened.
     */
    public NamedRollingFileAppender(Layout layout, String filename, boolean append)
        throws IOException {
        super(layout, filename, append);
    }

    /**
     Instantiate a FileAppender and open the file designated by
     <code>filename</code>. The opened filename will become the output
     destination for this appender.

     <p>The file will be appended to.  */
    public NamedRollingFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
    }

    /**
     Returns the value of the <b>MaxBackupIndex</b> option.
     */
    public
    int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    /**
     Get the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     @since 1.1
     */
    public
    long getMaximumFileSize() {
        return maxFileSize;
    }

    /**
     Implements the usual roll over behaviour.

     <p>If <code>MaxBackupIndex</code> is positive, then files
     {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     are renamed to {<code>File.2</code>, ...,
     <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     renamed <code>File.1</code> and closed. A new <code>File</code> is
     created to receive further log output.

     <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     <code>File</code> is truncated with no backup files created.

     */
    public // synchronization not necessary since doAppend is alreasy synched
    void rollOver() {
        // 如果没有在log4j中配置maxBackupIndex，那么set函数不会被调用，因此要在这里检查existedLogFileNames队列是否成功初始化
        restoreBackUpsIfNull();
        LogLog.debug("当前队列剩余空间: " + existedLogFileNames.remainingCapacity());
       // System.out.println("当前队列剩余空间: " + existedLogFileNames.remainingCapacity());

        File file;
        if (qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            LogLog.debug("rolling over count=" + size);
            //   if operation fails, do not roll again until
            //      maxFileSize more bytes are written
            nextRollover = size + maxFileSize;
        }
        LogLog.debug("maxBackupIndex="+maxBackupIndex);

        boolean deleteSuccess = true;
        // If maxBackups <= 0, then there is no file renaming to be done.
        if(maxBackupIndex > 0 && existedLogFileNames.size() >= maxBackupIndex) {
            // Delete the oldest file, to keep Windows happy.
            file = new File(existedLogFileNames.pollFirst());
            if (file.exists()) {
                deleteSuccess = file.delete();
            }
        }

        boolean renameSuccess = true;
        if (deleteSuccess) {
            // 将当前日志文件backUp重命名
            File target = new File(renameAsBackup());
            this.closeFile();
            file = new File(fileName);
            LogLog.debug("Renaming file " + file + " to " + target);
            renameSuccess = file.renameTo(target);

            if (renameSuccess) {
                // 将重命名后的文件名存入FILO队列
                try {
                    renameSuccess = existedLogFileNames.offerLast(target.getCanonicalPath());
                } catch (IOException e) {
                    renameSuccess = false;
                }
                if (renameSuccess) {
                    // 将FILO队列序列化到磁盘，以便系统重启时读取
                    try {
                        File f = new File(fileName + SERIALIZE_POSTFIX);
                        f.createNewFile();
                        SerializationUtils.serialize(existedLogFileNames, new FileOutputStream(f, false));
                    } catch (IOException e) {
                        LogLog.debug("Error serialize existed file queue", e);
                        renameSuccess = false;
                    }
                }
            }

            //
            //   if file rename failed, reopen file with append = true
            //
            if (!renameSuccess) {
                try {
                    this.setFile(fileName, true, bufferedIO, bufferSize);
                }
                catch(IOException e) {
                    if (e instanceof InterruptedIOException) {
                        Thread.currentThread().interrupt();
                    }
                    LogLog.error("setFile("+fileName+", true) call failed.", e);
                }
            }
        }

        //
        //   if deletes were successful, then
        //
        if (renameSuccess) {
            try {
                // This will also close the file. This is OK since multiple
                // close operations are safe.
                this.setFile(fileName, false, bufferedIO, bufferSize);
                nextRollover = 0;
            }
            catch(IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("setFile("+fileName+", false) call failed.", e);
            }
        }

       // printQueue();
    }


    /**
     * 将当前的日志文件重命名为backUp文件
     * 以当前时间作为后缀，方便查找
     *
     * @return
     */
    private String renameAsBackup() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String dateString = format.format(new Date(System.currentTimeMillis()));
        return fileName + "." + dateString;
    }

    /**
     * 用于调试
     * 打印队列
     */
    private void printQueue() {
        Iterator<String> iterator = existedLogFileNames.iterator();
        while (iterator.hasNext()) {
            String tmp = iterator.next();
            System.out.print(tmp + " -> ");
        }
        System.out.println();
    }


    /**
     * 当前正在写的文件
     * @param fileName
     * @param append
     * @param bufferedIO
     * @param bufferSize
     * @throws IOException
     */
    public
    synchronized
    void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
        throws IOException {
        super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
        if(append) {
            File f = new File(fileName);
            ((CountingQuietWriter) qw).setCount(f.length());
        }
    }


    /**
     Set the maximum number of backup files to keep around.

     <p>The <b>MaxBackupIndex</b> option determines how many backup
     files are kept before the oldest is erased. This option takes
     a positive integer value. If set to zero, then there will be no
     backup files and the log file will be truncated when it reaches
     <code>MaxFileSize</code>.
     */
    public
    void setMaxBackupIndex(int maxBackups) {
        this.maxBackupIndex = maxBackups;
    }

    private void restoreBackUpsIfNull() {
        if (existedLogFileNames == null) {
            existedLogFileNames = new LinkedBlockingDeque<String>(getMaxBackupIndex());
            try {
                LinkedBlockingDeque<String> restore = (LinkedBlockingDeque<String>) SerializationUtils.deserialize(
                    new FileInputStream(fileName + SERIALIZE_POSTFIX));
                existedLogFileNames.addAll(restore);
            } catch (FileNotFoundException e) {
                LogLog.warn("FILO store file not found");
            }
        }
    }

    /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>This method is equivalent to {@link #setMaxFileSize} except
     that it is required for differentiating the setter taking a
     <code>long</code> argument from the setter taking a
     <code>String</code> argument by the JavaBeans {@link
    java.beans.Introspector Introspector}.

     @see #setMaxFileSize(String)
     */
    public
    void setMaximumFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }


    /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>In configuration files, the <b>MaxFileSize</b> option takes an
     long integer in the range 0 - 2^63. You can specify the value
     with the suffixes "KB", "MB" or "GB" so that the integer is
     interpreted being expressed respectively in kilobytes, megabytes
     or gigabytes. For example, the value "10KB" will be interpreted
     as 10240.
     */
    public
    void setMaxFileSize(String value) {
        maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
    }

    protected
    void setQWForFiles(Writer writer) {
        this.qw = new CountingQuietWriter(writer, errorHandler);
    }

    /**
     This method differentiates RollingFileAppender from its super
     class.

     @since 0.9.0
     */
    protected
    void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if(fileName != null && qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            if (size >= maxFileSize && size >= nextRollover) {
                long startTime = System.currentTimeMillis();
                rollOver();
                long endTime = System.currentTimeMillis();
                System.out.println((endTime - startTime));
            }
        }
    }

}
