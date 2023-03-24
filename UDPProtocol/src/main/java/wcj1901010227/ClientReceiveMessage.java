/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author VACation
 */
public class ClientReceiveMessage extends Thread{
    private DatagramSocket clientSocket;//会话套接字
    private ClientUI parentUI;//父类
    private byte[] data=new byte[8096];//8kb
    private DefaultListModel listModel=new DefaultListModel();
    SqlHelper sqlHelper;
    
    //构造函数
    public ClientReceiveMessage(DatagramSocket socket,ClientUI parentUI)
    {
        clientSocket=socket;
        this.parentUI=parentUI;
        this.sqlHelper=new SqlHelper();
    }
    

    @Override
    public void run() {
        while (true) 
        {            
            try {
                DatagramPacket packet=new DatagramPacket(data, data.length);//创建接受报文
                
                clientSocket.receive(packet);
                Message message=(Message)Translate.ByteToObject(data);//还原消息对象
                //来自外部人员的
                String userAccount=message.getUserAccount();
                String userNickName=message.getUserNickName();
                if(message.isAdmin())
                {
                    //如果是管理员的话
                    userNickName="[管理员]"+userNickName;
                }
                //根据消息类型分类处理
                if(message.getType().equalsIgnoreCase("M_LOGIN"))//有新用户登录
                {
                    //更新消息窗口
                    
                    parentUI.txtArea.append(userNickName+"( "+userAccount+" )"+" 进入了聊天室！\n");
                    listModel.add(listModel.getSize(), userNickName+"("+userAccount+")");
                    parentUI.userList.setModel(listModel);
                    if(parentUI.adminUI!=null)
                    {
                        if(parentUI.adminUI.signupUI!=null)
                        {
                            parentUI.adminUI.signupUI.ChangeOnline(1);
                        }
                    }
                }
                else if (message.getType().equalsIgnoreCase("M_ACK"))//服务器返回的确认消息 
                {
                    //登录成功，把自己添加到用户列表
                    listModel.add(listModel.getSize(), userNickName);
                    parentUI.userList.setModel(listModel);      
                    if(message.isIsSigning())
                    {
                        parentUI.txtArea.append("老师发起了一次签到...\n");
                        parentUI.stuSignUpUI = new StuSignUpUI(Integer.parseInt(message.getText()), parentUI);
                        parentUI.stuSignUpUI.setVisible(true);
                        parentUI.stuSignUpUI.startCount();
                    }
                }
                
                else if(message.getType().equalsIgnoreCase("M_MSG"))//普通会话消息
                {
                    //更新消息窗口
                    parentUI.txtArea.append(userNickName+"( "+userAccount+" )"+" 说:"+message.getText()+"\n");
                    
                }
                else if (message.getType().equalsIgnoreCase("M_QUIT"))//其他用户下线的消息
                {
                    //更新消息窗口
                    parentUI.txtArea.append(userNickName+"( "+userAccount+" )"+" 离开了聊天室...\n");
                    //下线用户从列表删除
                    listModel.remove(listModel.indexOf(userNickName+"("+userAccount+")"));
                    parentUI.userList.setModel(listModel);
                    if(message.isIsSigning())
                    {
                        if (parentUI.adminUI != null) {
                            if (parentUI.adminUI.signupUI != null) {
                                //如果在考勤的时候退出，就不算签到
                                parentUI.adminUI.signupUI.ChangeOnline(-1);
                                parentUI.adminUI.signupUI.ChangeSign(-1, message);

                            }
                        }
                    }
                    
                    
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE"))
                {
                    //对方发起了私聊
                    if(parentUI.privateChatUI==null)
                    {
                        
                        parentUI.privateChatUI=new PersonalChatUI(clientSocket,parentUI,parentUI.message.getToAddr(),
                                parentUI.message.getToPort(),userAccount,parentUI.message.getUserAccount(),sqlHelper.GetUserNickName(userAccount));
                        parentUI.privateChatUI.setTitle("您正在和 "+userAccount+"进行对话\n");
                        parentUI.privateChatUI.setVisible(true);
                        parentUI.privateChatUI.txtArea.append(userNickName+"向您发起了私聊请求，现在可以对话\n");
                        Message backMessage=new Message();
                        backMessage.setType("M_PRIVATE_CONFIRM");
                        backMessage.setUserAccount(message.getTargetAccount());
                        backMessage.setUserNickName(message.getTargetNickName());
                        backMessage.setTargetAccount(userAccount);
                        backMessage.setTargetNickName(userNickName);
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket confirmPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        clientSocket.send(confirmPacket);
                    }
                    else
                    {
                        //已经不是第一次私聊了
                        parentUI.privateChatUI.txtArea.append(userNickName+"( "+userAccount+" )"+" 悄悄对你说:"+message.getText()+"\n");
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE_CONFIRM"))
                {
                    parentUI.privateChatUI.txtArea.append(userNickName+"已经收到了你的私聊请求，可以发消息了\n");
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE_QUIT"))
                {
                    parentUI.privateChatUI.txtArea.append(userNickName+"已经结束此次通话，请退出聊天\n");
                    parentUI.privateChatUI=null;
                    
                }
                else if(message.getType().equalsIgnoreCase("M_Silence"))
                {
                    parentUI.txtArea.append("老师已经开启了全员禁言...\n");
                    parentUI.btnSend.setEnabled(false);
                }
                else if(message.getType().equalsIgnoreCase("M_Speak"))
                {
                    parentUI.txtArea.append("老师已经关闭了全员禁言...\n");
                    parentUI.btnSend.setEnabled(true);
                }
                else if(message.getType().equalsIgnoreCase("M_START"))
                {
                    parentUI.txtArea.append("老师发起了一次签到...\n");
                    parentUI.stuSignUpUI=new StuSignUpUI(Integer.parseInt(message.getText()), parentUI);
                    parentUI.stuSignUpUI.setVisible(true);
                    parentUI.stuSignUpUI.startCount();
                }
                else if(message.getType().equalsIgnoreCase("M_OK"))
                {
                    if(parentUI.adminUI.signupUI!=null)
                    {
                        parentUI.adminUI.signupUI.ChangeSign(1,message);
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_END"))
                {
                    parentUI.txtArea.append("本次签到已结束...\n");
                    if(parentUI.stuSignUpUI!=null)
                    {
                        parentUI.stuSignUpUI.dispose();
                        parentUI.stuSignUpUI=null;
                    }
                    JOptionPane.showMessageDialog(null, "老师已经结束此次签到");
                    
                }


                                        
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"客户端接收线程出错",JOptionPane.ERROR_MESSAGE);
                
            }
            
        }
    }
    
    
}
