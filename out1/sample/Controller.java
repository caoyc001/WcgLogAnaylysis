package sample;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import com.sun.javafx.scene.EnteredExitedHandler;
import io.impl.OIOLogReader;
import io.impl.OIOLogWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import core.LogAnalysis;
import interceptor.LogAnalysisInterceptor;
import interceptor.impl.RegularInterceptor;
import interceptor.impl.NumInterceptor;
import io.LogReader;
import io.LogWriter;
import io.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javax.swing.*;
/**
 * @author yccao
 *
 */
public class Controller implements Initializable {


    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML private TextArea logArea;
    @FXML private TableView <Log> logTable;
    private  List <File> fileList;
    LogWriter logWriter=new OIOLogWriter();
    LogReader logReader=new OIOLogReader();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateTable();

    }

    // 点击按钮后进行搜索

    public void search(ActionEvent event) throws ParseException {
        if(fileList==null)
        {   Object[] options = { "是", "取消" };
            JOptionPane.showOptionDialog(null, "未选择日志文件", "警告",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return;
        }

        NumInterceptor.numMap=new TreeMap<>();
        String pattern=textField1.getText();
        String date=textField2.getText();
        String seq=textField3.getText();
        if(!seq.equals("")) {
            NumInterceptor numInterceptor = new NumInterceptor();
            LogAnalysis logAnalysis = new LogAnalysis(numInterceptor, logReader, logWriter, fileList, seq);
            logAnalysis.start();
            drawTable();
            return ;
        }
        if(pattern.equals("")&&date.equals(""))
        {
            Object[] options = {"确定"};
            JOptionPane.showOptionDialog(null, "请至少输入一个信息", "提示",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return ;
        }

        if(date.equals("")||seq.equals("")) {
            String searchContent=date.equals("")?pattern:date;
            NumInterceptor numInterceptor = new NumInterceptor();
            LogAnalysis logAnalysis = new LogAnalysis(numInterceptor, logReader, logWriter, fileList, searchContent);
            logAnalysis.start();
            drawTable();


        }
        //System.out.print("sucess");
    }
    //绘制表格内容
    public void drawTable()
    {
        List<Log> logList=new ArrayList<>();
        for(Map.Entry<Date,Integer> entry:NumInterceptor.numMap.entrySet())
        {
            logList.add(new Log(entry.getValue().toString(),entry.getKey()));
        }
        ObservableList<Log> data = FXCollections.observableArrayList(logList);
        logTable.setItems(data);
    }
    //初始化表格
    public void generateTable()
    {
        logTable.setEditable(true);

        TableColumn firstNameCol = new TableColumn("序列号");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("seq"));

        TableColumn lastNameCol = new TableColumn("日期");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<>("date"));

        logTable.getColumns().addAll(firstNameCol, lastNameCol);
        System.out.print("sucess");
    }
    //点击表格后按照序列号搜索日志
    public void clickTable() throws ParseException {
        if(logTable==null) {
            return;
        }
        if(logTable.getSelectionModel().isEmpty())
            return;
        String tLog = logTable.getSelectionModel().getSelectedItems().get(0).getSeq();
        searchSeq(tLog);

    }
    //点击菜单后打开文件
    public  void  openFile(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();

         fileList = fileChooser.showOpenMultipleDialog(new Stage());
         if(fileList!=null) {
             Object[] options = {"是", "取消"};
             JOptionPane.showOptionDialog(null, "文件导入成功,"+"共导入"+fileList.size()+"个文件", "成功",
                     JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                     null, options, options[0]);
         }

    }
    //点击列表中的数字后进行序列号搜索
    /*public void clickList() throws ParseException {
       String num=idList.getSelectionModel().getSelectedItems().toArray()[0].toString().trim();
        searchSeq( num);
    }*/
    //点击帮助后弹出对话框
    public void clickHelp()
    {   String helpMessage="本软件用于对wcg日志进行批量搜索\n首先点击文件批量导入需要搜索的日志文件\n可以使用任意关键字,日期或者日志序列号三种方式\n如果使用序列号则不需要输入其他两项\n";
        Object[] options = {"确定"};
        JOptionPane.showOptionDialog(null, helpMessage, "说明",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
    }
    //根据序列号搜索日志
    public void searchSeq(String num) throws ParseException {
        RegularInterceptor regularInterceptor=new RegularInterceptor();
        LogAnalysis logAnalysis=new LogAnalysis(regularInterceptor,logReader,logWriter,fileList, num);
        logAnalysis.start();
        logArea.setText(RegularInterceptor.sb.toString());
    }
    public static class Log  {

        private final SimpleStringProperty date;
        private final SimpleStringProperty seq;


        private Log(String seq, Date date) {
            this.seq = new SimpleStringProperty(seq);
            SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
            this.date = new SimpleStringProperty(sdf.format(date).toString());

        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String fName) {
            date.set(fName);
        }

        public String getSeq() {
            return seq.get();
        }

        public void setSeq(String fName) {
            seq.set(fName);
        }


    }

}
