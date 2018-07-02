package com.example.android_email;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;
import javax.activation.DataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;

public class MailDetails extends AppCompatActivity {
    private static final String SAVE_INFORMATION = "save_information";
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private WebView view;
    private ReceiveList ml;
    String receivehost;
    Handler handler;
    private String string;
    private String subject;
    private String from;
    private String date;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);
        view = (WebView) findViewById(R.id.view);
        WebSettings settings = view.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true); //支持javascript
        settings.setSupportZoom(true); // 设置可以支持缩放
        settings.setBuiltInZoomControls(true); // 设置出现缩放工具
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true); //扩大比例的缩放
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //自适应屏幕
        settings.setDefaultTextEncodingName("GBK");// 设置默认为utf-8

        handler=new Handler(){

            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 10:
                        text1.setText(subject);
                        if (from.contains("<") || from.contains(">") || from.contains("=") || from.contains("?")) {
                            text2.setText("发件人: <"+from.split("<")[1]);
                        }
                        text3.setText("时间: "+date);
                        text4.setVisibility(View.GONE);
                        view.loadData(string, "text/html; charset=UTF-8", null);// 这种写法可以正确解码
                        break;
                    default:
                        break;
                }
            }

        };

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Looper.prepare();
                    receive();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void receive() throws Exception {
        // sharedpreference读取数据，用split（）方法，分开字符串。
        SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION, MODE_WORLD_READABLE);
        String content = pre.getString("save", "");
        String[] Information = content.split(";");
        String username = Information[0];
        String password = Information[1];
        receivehost = pre.getString("receive_host", "");
        Intent intent = getIntent();// 得到上一个文件传入的ID号
        Bundle bundle = intent.getExtras();
        int num = bundle.getInt("ID");// 将得到的ID号传递给变量num

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        // 取得pop3协议的邮件服务器
        Store store = session.getStore("pop3");

        // 连接pop.qq.com邮件服务器
        store.connect(receivehost, username, password);
        // 返回文件夹对象
        Folder folder = store.getFolder("INBOX");
        // 设置仅读
        folder.open(Folder.READ_ONLY);

        // 获取信息
        Message message = folder.getMessage(num + 1);
        subject = message.getSubject();
        from = message.getFrom()[0].toString();

        date = message.getSentDate().toString();
        getMailContent(message);
        // string=bodytext.toString();
        handler.sendEmptyMessage(10);
        folder.close(true);
        store.close();
    }

    /**
     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
     */
    @SuppressLint("DefaultLocale")
    public void getMailContent(Part part) throws Exception {

        String contenttype = part.getContentType();
        int nameindex = contenttype.indexOf("name");

        boolean conname = false;
        if (nameindex != -1)
            conname = true;
        System.out.println("CONTENTTYPE: " + contenttype);
        if ((part.isMimeType("text/plain") && !conname) || (part.isMimeType("text/html") && !conname)) {
            InputStream in = part.getInputStream();
            String type = part.getContentType();
            String charset = "gbk";

            if (!TextUtils.isEmpty(type)) {
                if (type.toLowerCase().contains("utf-8")) {
                    charset = "utf-8";
                } else if (type.toLowerCase().contains("gbk")) {
                    charset = "gbk";
                } else if (type.toLowerCase().contains("unicode")) {
                    charset = "unicode";
                } else if (type.toLowerCase().contains("gb2312")) {
                    charset = "gb2312";
                } else if (type.toLowerCase().contains("utf-16be")) {
                    charset = "utf-16be";
                } else {
                    charset = "utf-8";
                }
            }

            InputStreamReader isr = new InputStreamReader(in, charset);
            char[] buf = new char[1024];
            int count = 0;
            StringBuffer sb = new StringBuffer();
            while ((count = isr.read(buf)) != -1) {
                sb.append(new String(buf, 0, count));
            }
            // bodytext.append(sb.toString());
            string = sb.toString();
            handler.sendEmptyMessage(10);
        } else if (part.isMimeType("multipart/*")) {
            DataSource source = new ByteArrayDataSource(part.getInputStream(), "multipart/*");
            Multipart multipart = new MimeMultipart(source);

            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailContent((Part) part.getContent());
        } else {
            getMailContent((Part) part.getContent());
        }
    }

}
