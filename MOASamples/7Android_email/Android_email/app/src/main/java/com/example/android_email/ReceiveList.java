package com.example.android_email;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class ReceiveList extends AppCompatActivity {

	private static final String SAVE_INFORMATION = "save_information";

	private ListView listview;
	private int number;
//	ArrayList<HashMap<String, String>> list;
	Handler handler;

	String Title;
	String Date;
	String From;
	String Content;
	String username;
	String password;
	String receivehost;
	SimpleAdapter listAdapter;
	ReceiverListAdapter adapter;
	List<Emailbean> list=new ArrayList<Emailbean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

		setContentView(R.layout.listmenu);
		listview = (ListView) findViewById(R.id.my_list);
//		listAdapter = new SimpleAdapter(this , list, R.layout.item, new String[] {"title", "info"}, new int[] {R.id.title, R.id.info});
		adapter=new ReceiverListAdapter(list, ReceiveList.this);
		listview.setAdapter(adapter);
		
		handler=new Handler(){

			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 10:
//					SimpleAdapter listAdapter = new SimpleAdapter(this, list,R.layout.item, new String[] { "title", "info" }, new int[] { R.id.title, R.id.info });
					adapter.notifyDataSetChanged();
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
					MenuList();
					Looper.loop();
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void MenuList() throws MessagingException, IOException {
		// sharedpreference读取数据，用split（）方法，分开字符串。
		SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION, MODE_WORLD_READABLE);
		String content = pre.getString("save", "");
		String[] Information = content.split(";");
		username = Information[0];
		password = Information[1];
		receivehost=pre.getString("receive_host", "");
		Properties props = new Properties();
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		  props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		  props.setProperty("mail.pop3.socketFactory.port", "995");
		  props.setProperty("mail.store.protocol","pop3");
		  props.setProperty("mail.pop3.host", "pop.qq.com");
		  props.setProperty("mail.pop3.port", "995");
		  props.setProperty("mail.pop3.auth.login.disable", "true");
		Session session = Session.getDefaultInstance(props); // 取得pop3协议的邮件服务器
		Store store = session.getStore("pop3");
		//连接邮件服务器 //
		store.connect(receivehost, username, password); // 返回文件夹对象
		Folder folder = store.getFolder("INBOX"); // 设置仅读
		folder.open(Folder.READ_ONLY); // 获取信息
		Message message[] = folder.getMessages();
		for (int i = 0; i < message.length; i++) {//通过for语句将读取到的邮件内容一个一个的在list中显示出来
			ResolveMail receivemail = new ResolveMail((MimeMessage) message[i]);

			Title = receivemail.getSubject();//得到邮件的标题
			Date = receivemail.getSentDate();//得到邮件的发送时间
			Emailbean bean=new Emailbean();
			bean.setTitle(Title);
			bean.setContent(Date);
			list.add(bean);
		}

		handler.sendEmptyMessage(10);
		folder.close(true);//用好之后记得将floder和store进行关闭
		store.close();

		// Item长按事件。得到Item的值，然后传递给MailDetail的值
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				return true;
			}

		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent();
				intent.putExtra("ID", position);
				intent.setClass(ReceiveList.this, MailDetails.class);
				startActivity(intent);
			}
		});
	}
}
