package com.example.android_email;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sun.mail.util.MailSSLSocketFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AppCompatActivity {
	private Button btnClick;
	private EditText txtToAddress;
	private EditText txtSubject;
	private EditText txtContent;
	private static final String SAVE_INFORMATION = "save_information";
	String username;
	String password;
	String sendhost;

	public void SendMail() throws MessagingException, IOException {
		// 用sharedpreference来获取数值
		SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION, MODE_WORLD_READABLE);
		String content = pre.getString("save", "");
		String[] Information = content.split(";");
		username = Information[0];
		password = Information[1];
		sendhost = pre.getString("send_host", "");
		// 该部分有待完善！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		sf.setTrustAllHosts(true);
		Properties props = new Properties();
		props.put("mail.smtp.host", sendhost);// 存储发送邮件服务器的信息
		props.put("mail.smtp.auth", "true");// 同时通过验证
		props.put("mail.smtp.socketFactory.port", 465);// 原端口25
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", sf);
		// 基本的邮件会话
		Session session = Session.getInstance(props);
		// MyAuthenticator authenticator = null;
		// authenticator = new MyAuthenticator(username, password);
		// Session session = Session.getDefaultInstance(props, authenticator);
		session.setDebug(true);// 设置调试标志
		// 构造信息体
		MimeMessage message = new MimeMessage(session);

		// 发件地址
		Address fromAddress = null;
		// fromAddress = new InternetAddress("sarah_susan@sina.com");
		fromAddress = new InternetAddress(username);
		message.setFrom(fromAddress);

		// 收件地址
		Address toAddress = null;
		toAddress = new InternetAddress(txtToAddress.getText().toString());
		message.addRecipient(Message.RecipientType.TO, toAddress);

		// 解析邮件内容

		message.setSubject(txtSubject.getText().toString());// 设置信件的标题
		message.setText(txtContent.getText().toString());// 设置信件内容
		message.saveChanges(); // implicit with send()//存储有信息

		// send e-mail message

		Transport transport = null;
		transport = session.getTransport("smtp");
		transport.connect(sendhost, username, password);

		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		System.out.println("邮件发送成功！");

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_email);

		txtToAddress = (EditText) findViewById(R.id.txtToAddress);
		txtSubject = (EditText) findViewById(R.id.txtSubject);
		txtContent = (EditText) findViewById(R.id.txtContent);

		txtToAddress.setText("839994776@qq.com");
		txtSubject.setText("Hello world");
		txtContent.setText("你好,这是一封测试邮件,请不要拒收,再次谢谢！");

		btnClick = (Button) findViewById(R.id.btnSEND);
		btnClick.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 发送邮件
				// SendMail();
				new sendEmail().execute();
				// Toast显示
				// Toast toast = Toast.makeText(getApplicationContext(),
				// "邮件发送成功！", Toast.LENGTH_LONG);
				// toast.setGravity(Gravity.CENTER, 0, 0);
				// toast.show();
				// 界面跳转
			}

		});

	}

	class sendEmail extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// 用sharedpreference来获取数值
			SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION, MODE_WORLD_READABLE);
			String content = pre.getString("save", "");
			String[] Information = content.split(";");
			username = Information[0];
			password = Information[1];
			sendhost = pre.getString("send_host", "");
			MailSSLSocketFactory sf = null;
			try {
				sf = new MailSSLSocketFactory();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			sf.setTrustAllHosts(true);
			Properties props = new Properties();
			props.put("mail.smtp.host", sendhost);// 存储发送邮件服务器的信息
			props.put("mail.smtp.auth", "true");// 同时通过验证
			props.put("mail.smtp.socketFactory.port", 465);// 原端口25
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.socketFactory", sf);
			// 基本的邮件会话
			Session session = Session.getInstance(props);
			session.setDebug(true);// 设置调试标志
			// 构造信息体
			MimeMessage message = new MimeMessage(session);
			// 发件地址
			Address fromAddress = null;
			// fromAddress = new InternetAddress("sarah_susan@sina.com");
			try {
				fromAddress = new InternetAddress(username);
				message.setFrom(fromAddress);
				Address toAddress = null; // 收件地址
				toAddress = new InternetAddress(txtToAddress.getText().toString());
				message.addRecipient(Message.RecipientType.TO, toAddress); // 解析邮件内容
				message.setSubject(txtSubject.getText().toString());// 设置信件的标题
				message.setText(txtContent.getText().toString());// 设置信件内容
				message.saveChanges(); // implicit with send()//存储有信息
				Transport transport = null; // send e-mail message
				transport = session.getTransport("smtp");
				transport.connect(sendhost, username, password);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch (Exception e) {
				return "发送失败， " + e.getMessage();
			}
			return "发送成功";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(SendMail.this, result, Toast.LENGTH_SHORT).show();
		}

	}

	Handler handler=new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 100) {
				
			} else if (msg.what == 200 ) {
				
			}
		};
		
	};

}