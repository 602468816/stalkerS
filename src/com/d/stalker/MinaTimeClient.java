package com.d.stalker;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.alibaba.fastjson.JSON;
import com.d.stalker.bean.Message;


public class MinaTimeClient {
	public static void main(String[] args) {
		// �����ͻ���������. 
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast( "logger", new LoggingFilter() ); 
		connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" )))); //���ñ�������� 
		connector.setConnectTimeoutMillis(1000*60);
		connector.setHandler(new IoHandlerAdapter(){
			public void messageSent(IoSession session, Object message) throws Exception {
				System.out.println("messageSent:"+message);
				//session.close(true);
			}
			public void messageReceived(IoSession session, Object message) throws Exception { 
				System.out.println("client�յ���Ϣ��"+message);//��ʾ���յ�����Ϣ 
			}
		});//�����¼������� 
		ConnectFuture cf = connector.connect(new InetSocketAddress("127.0.0.1", 9123));//�������� 
		cf.awaitUninterruptibly();//�ȴ����Ӵ������ 

		Message  message=new Message();
		message.setFormUser("user2");
		message.setMessage("��¼");
		message.setCmd("login");//�״ε�¼
		cf.getSession().write(JSON.toJSONString(message));
		
		//cf.getSession().write("hello");//������Ϣ 
		 
		cf.getSession().getCloseFuture().awaitUninterruptibly();//�ȴ����ӶϿ� 
		connector.dispose(); 
	}
}
