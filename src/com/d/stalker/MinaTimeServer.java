package com.d.stalker;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.alibaba.fastjson.JSON;
import com.d.stalker.bean.Message;


public class MinaTimeServer {
	private static final int PORT = 9123;//��������˿� 
	public static void main( String[] args ) throws IOException{ 
		IoAcceptor acceptor = new NioSocketAcceptor(); 
		acceptor.getFilterChain().addLast( "logger", new LoggingFilter() ); 
		acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));//ָ����������� 
		 
		 
		acceptor.setHandler(new IoHandlerAdapter(){
			public void sessionCreated(IoSession session) {
				// ��ʾ�ͻ��˵�ip�Ͷ˿�
				Collection<IoSession> sessions = session.getService().getManagedSessions().values();
				
				System.out.println("IP��"+session.getRemoteAddress().toString()+",sessionId:"+session.getId());
				System.out.println("�û����ߣ�"+"��������=��"+sessions);
			}

			@Override
			public void messageReceived( IoSession session, Object message) throws Exception {
				String str = message.toString();
				System.out.println("server�յ���Ϣ��"+str);
				if (str.trim().equalsIgnoreCase("quit")) {
					session.close(true);// �����Ự
					return;
				}else{
				Message mes=JSON.parseObject(message.toString(), Message.class);
				if("login".equals(mes.getCmd())){
					session.setAttribute("userName", mes.getFormUser());
					return;
				}
				
				Collection<IoSession> sessions = session.getService().getManagedSessions().values();
				System.out.println("����"+session.getAttribute("userName")+"����Ϣ��"+sessions.size());	
				
				String userName=mes.getToUser();
				
					
					if("all".equals(mes.getToUser())){
						for (IoSession sess : sessions) {
					       sess.write(mes.getMessage());
						}
					}else{
						 for (IoSession sess : sessions) {
							 if(userName.equals(sess.getAttribute("userName"))){
						       sess.write(mes.getMessage());
						       break;
							 }
							}
							 
						 }
				session.write("back"); 
				}
				
			}
		});//ָ��ҵ���߼������� 
		acceptor.setDefaultLocalAddress( new InetSocketAddress(PORT) );//���ö˿ں� 
		acceptor.bind();//�������� 
	}
}
