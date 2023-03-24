/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import java.util.Timer;

/**
 *
 * @author VACation
 */
public class ServerReceiveMessage extends Thread{
    private DatagramSocket serverSocket;//服务器套接字
    private DatagramPacket packet;//报文
    private List<User> userList=new ArrayList<>();
    private byte[] data=new byte[8096];
    private ServerUI parentUI;
    private SqlHelper sqlHelper;
    boolean signup;
    Timer timer;
    int continueTime;
    
    public ServerReceiveMessage(DatagramSocket socket,ServerUI parentUI)
    {
        serverSocket=socket;
        this.parentUI=parentUI;
        sqlHelper=new SqlHelper();
        signup=false;
    }

    @Override
    public void run() {
        while (true)
        {            
            try {
                packet=new DatagramPacket(data, data.length);
                serverSocket.receive(packet);//接受客户机数据
                //收到的数据转为消息对象
                Message message=(Message)Translate.ByteToObject(packet.getData());
                String account=message.getUserAccount();
                String nickName=message.getUserNickName();
                
                if(message.getType().equalsIgnoreCase("M_LOGIN"))//是登录的消息
                {
                    String passWord=message.getUserPassword();
                    Message backMessage=new Message();
                    backMessage.setIsAdmin(message.isAdmin());
                    //登录失败的情况
                    if(sqlHelper.LoginCheck(account, passWord)==false)
                    {
                        backMessage.setType("M_FAILURE");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        //返回给用户的报文
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                    }
                    //用户被封禁的情况
                    else if(sqlHelper.GetUserState(account)==0)
                    {
                        backMessage.setType("M_BANNED");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                    }
                    //用户已经登录的情况
                    else if(sqlHelper.GetUserState(account)==2)
                    {
                        backMessage.setType("M_DUPLICATE");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                        
                    }
                    //登录成功的情况
                    else
                    {
                        sqlHelper.SetUserState(account, SqlHelper.logState.LOG_IN);
                        backMessage.setType("M_SUCCESS");
                        backMessage.setIsAdmin(sqlHelper.isAdmin(account));
                        byte[] buff=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buff, buff.length,packet.getAddress(),packet.getPort());
                        
                        serverSocket.send(backPacket);
                        
//                        User user=new User(account, message.getUserPassword(), message.getUserNickName(),message.getUserSex());
//                        user.setPacket(packet);
                        //User user=sqlHelper.GenerateUser(account, message.getUserPassword(), message.getUserNickName(), message.getUserSex(),packet);
                        if(message.isAdmin())
                        {
                            nickName="[管理员]"+nickName;
                        }
                        
                        User user=sqlHelper.GenerateUser(account, message.getUserPassword(), nickName+"("+account+")", message.getUserSex(),packet);
                        
                        
                        userList.add(user);
                        //更新服务器聊天室大厅
                        if(user.IsAdmin())
                        {
                            parentUI.txtArea.append(nickName+"( "+account+" )"+" 登录了！\n");
                        }
                        else
                        {
                            parentUI.txtArea.append(nickName+"( "+account+" )"+" 登录了！\n");
                        }
                        
                        //向所有其他在线用户发送M_LOGIN消息，向新用户发送整个用户列表
                        for (int i = 0; i < userList.size(); i++) {
                            //向其他用户发送M_LOGIN消息
                            if(!account.equalsIgnoreCase(userList.get(i).getAccount()))
                            {
                                DatagramPacket oldPacket=userList.get(i).getPacket();
                                DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                                serverSocket.send(newPacket);
                            }
                            //向当前用户回送M_ACK消息，将第i个用户添加到新登录的客户机的用户列表中，可以做私聊
                            Message ackMessage=new Message();
                            ackMessage.setUserAccount(userList.get(i).getAccount());
                            ackMessage.setUserNickName(userList.get(i).getNickName());
                            ackMessage.setType("M_ACK");
                            if(i==userList.size()-1&&userList.get(i).IsAdmin()==false)
                            {
                                ackMessage.setIsSigning(signup);
                            }
                            
                            ackMessage.setText(String.valueOf(continueTime));
                            byte[] buffer=Translate.ObjectToByte(ackMessage);
                            DatagramPacket newPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                            serverSocket.send(newPacket);   
                        }
 
                    }
                }
                //是普通消息的情况
                else if(message.getType().equalsIgnoreCase("M_MSG"))
                {
                    
                    parentUI.txtArea.append(nickName+"( "+account+" )"+" 说: "+message.getText()+"\n");
                    for (int i = 0; i < userList.size(); i++) {
                        DatagramPacket oldPacket=userList.get(i).getPacket();
                        DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                        serverSocket.send(newPacket);
                    }
                }
                //如果是M_QUIT消息
                else if(message.getType().equalsIgnoreCase("M_QUIT"))
                {
                    parentUI.txtArea.append(nickName+"( "+account+" )"+" 下线！\n");
                    sqlHelper.SetUserState(account, SqlHelper.logState.LOG_OUT);
                    for (int i = 0; i < userList.size(); i++) {
                        //获取索引
                        if(userList.get(i).getAccount().equals(account))
                        {
                            userList.remove(i);
                            break;
                        }
                        
                    }
                    //向其他用户转发下线消息
                    for (int i = 0; i < userList.size(); i++) {
                        DatagramPacket oldPacket=userList.get(i).getPacket();
                        Message backMessage=message;
                        backMessage.setIsSigning(signup);
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket newPacket=new DatagramPacket(buffer, buffer.length,oldPacket.getAddress(),oldPacket.getPort());
                        serverSocket.send(newPacket);
                        
                    }
                }
                //找回密码请求
                else if(message.getType().equalsIgnoreCase("M_PASSWORD"))
                {
                    Message backMessage=new Message();
                    String passwordString=sqlHelper.GetPasswordByAccount(account);
                    //如果找到了
                    if(passwordString!=null)
                    {
                        backMessage.setType("M_SUCCESS");
                        backMessage.setUserPassword(passwordString);
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                    }
                    else
                    {
                        backMessage.setType("M_FAILURE");
                        byte[]buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                    }
                }
                //注册请求
                else if(message.getType().equalsIgnoreCase("M_REG"))
                {
                    //account已经定义了
                    
                    String sex=message.getUserSex();
                    String passWord=message.getUserPassword();
                    String userClass=message.getUserClass();
                    int affectedRow=sqlHelper.UserRegister(account, passWord, nickName, sex,userClass);
                    Message backMessage=new Message();
                    if(affectedRow==1)
                    {
                        //注册成功的情况
                        backMessage.setType("M_SUCCESS");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                        
                    }
                    else
                    {
                        //注册失败的情况
                        backMessage.setType("M_FAILURE");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                        
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE"))
                {
                    //私聊的消息,获取消息要发送给的对象
                    String targetAccount=message.getTargetAccount();
                    User targetUser=null;
                    for(int i=0;i<userList.size();i++)
                    {
                        if(userList.get(i).getAccount().equals(targetAccount))
                        {
                            targetUser=userList.get(i);
                            break;
                        }
                    }
                    if(targetUser!=null)
                    {
                        Message newMessage=message;
                        
                        String nickNameString=sqlHelper.GetUserNickName(account);
//                        if(sqlHelper.isAdmin(account))
//                        {
//                            nickNameString="[管理员]"+nickNameString;
//                        }
                        newMessage.setUserNickName(nickNameString);
                        byte[] buffer=Translate.ObjectToByte(newMessage);
                        DatagramPacket targetPacket=targetUser.getPacket();
                        DatagramPacket newPacket=new DatagramPacket(buffer, buffer.length,targetPacket.getAddress(),targetPacket.getPort());
                        serverSocket.send(newPacket);
                        
                        
                        
                        
                        
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "目标用户为空");
                    }
                    
                    
                    
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE_CONFIRM"))
                {
                    //对方已经确认可以进行私聊
                    String targetAccount=message.getTargetAccount();
                    User targetUser=null;
                    for(int i=0;i<userList.size();i++)
                    {
                        if(userList.get(i).getAccount().equals(targetAccount))
                        {
                            targetUser=userList.get(i);
                            break;
                        }
                    }
                    if(targetUser!=null)
                    {
                        DatagramPacket targetPacket=targetUser.getPacket();
                        Message backMessage=message;
                        backMessage.setUserNickName(message.getTargetNickName());
                        DatagramPacket newPacket=new DatagramPacket(data, data.length,targetPacket.getAddress(),targetPacket.getPort());
                        serverSocket.send(newPacket);
                        
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "目标用户为空");
                    }
                    
                }
                else if(message.getType().equalsIgnoreCase("M_PRIVATE_QUIT"))
                {
                    String targetAccount=message.getTargetAccount();
                    User targetUser=null;
                    for(int i=0;i<userList.size();i++)
                    {
                        if(userList.get(i).getAccount().equals(targetAccount))
                        {
                            targetUser=userList.get(i);
                            break;
                        }
                    }
                    if(targetUser!=null)
                    {
                        DatagramPacket targetPacket=targetUser.getPacket();
                        Message newMessage=message;
                        String nickNameString=sqlHelper.GetUserNickName(account);
                        if(sqlHelper.isAdmin(account))
                        {
                            nickNameString="[管理员]"+nickNameString;
                        }
                        newMessage.setUserNickName(nickNameString);
                        byte[] buffer=Translate.ObjectToByte(newMessage);
                        DatagramPacket newPacket=new DatagramPacket(buffer, buffer.length,targetPacket.getAddress(),targetPacket.getPort());
                        serverSocket.send(newPacket);
                        
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "目标用户为空");
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_QUERY"))
                {
                    if(message.isAdmin())
                    {
                        Message backMessage=new Message();
                        backMessage.setType("M_SUCCESS");
                        byte[] buffer=Translate.ObjectToByte(backMessage);
                        DatagramPacket backPacket=new DatagramPacket(buffer, buffer.length,packet.getAddress(),packet.getPort());
                        serverSocket.send(backPacket);
                        
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_Silence"))
                {
                    Message backMessage = new Message();
                    backMessage.setType("M_SUCCESS");
                    byte[] buffer = Translate.ObjectToByte(backMessage);
                    DatagramPacket backPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(backPacket);
                    for(int i=0;i<userList.size();i++)
                    {
                        User currentUser=userList.get(i);
                        if(!currentUser.IsAdmin())
                        {
                            DatagramPacket oldPacket=currentUser.getPacket();
                            DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                            serverSocket.send(newPacket);
                        }
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_Speak"))
                {
                    Message backMessage = new Message();
                    backMessage.setType("M_SUCCESS");
                    byte[] buffer = Translate.ObjectToByte(backMessage);
                    DatagramPacket backPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(backPacket);
                    for(int i=0;i<userList.size();i++)
                    {
                        User currentUser=userList.get(i);
                        if(!currentUser.IsAdmin())
                        {
                            DatagramPacket oldPacket=currentUser.getPacket();
                            DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                            serverSocket.send(newPacket);
                        }
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_READY"))
                {
                    Message backMessage = new Message();
                    backMessage.setType("M_SUCCESS");
                    
                    int onlineNumber=0;
                    for(int i=0;i<userList.size();i++)
                    {
                        if(userList.get(i).IsAdmin())
                        {
                            continue;
                        }
                        onlineNumber++;
                    }
                    backMessage.setText(String.valueOf(onlineNumber));
                    byte[] buffer = Translate.ObjectToByte(backMessage);
                    DatagramPacket backPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(backPacket);
                    
                }
                else if(message.getType().equalsIgnoreCase("M_START"))
                {
                    //签到开始
                    signup=true;
                    continueTime=Integer.parseInt(message.getText());
                    
                    Message backMessage = new Message();
                    backMessage.setType("M_SUCCESS");
                    byte[] buffer = Translate.ObjectToByte(backMessage);
                    DatagramPacket backPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(backPacket);
                    parentUI.txtArea.append("老师发起了一次签到...\n");
                    //向当前在线学生转发信息
                    for(int i=0;i<userList.size();i++)
                    {
                        User currentUser=userList.get(i);
                        if(!currentUser.IsAdmin())
                        {
                            DatagramPacket oldPacket=currentUser.getPacket();
                            DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                            serverSocket.send(newPacket);
                        }
                    }
                    timer=new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            continueTime--;
                            if(continueTime==0)
                            {
                                
                                timer.cancel();
                                signup=false;
                            }
                        }
                    },0,1000);
                    
                    
                }
                else if(message.getType().equalsIgnoreCase("M_OK"))
                {
                    //学生确认签到
                    
                    
                    for(int i=0;i<userList.size();i++)
                    {
                        User currentUser;
                        if((currentUser=userList.get(i)).IsAdmin())
                        {
                            DatagramPacket oldPacket=currentUser.getPacket();
                            DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                            serverSocket.send(newPacket);

                            break;
                        }
                    }
                }
                else if(message.getType().equalsIgnoreCase("M_END"))
                {
                    //主动结束签到
                    timer.cancel();
                    signup=false;
                    
                    Message backMessage = new Message();
                    backMessage.setType("M_SUCCESS");
                    byte[] buffer = Translate.ObjectToByte(backMessage);
                    DatagramPacket backPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(backPacket);
                    
                    for(int i=0;i<userList.size();i++)
                    {
                        User currentUser=userList.get(i);
                        if(!currentUser.IsAdmin())
                        {
                            DatagramPacket oldPacket=currentUser.getPacket();
                            DatagramPacket newPacket=new DatagramPacket(data, data.length,oldPacket.getAddress(),oldPacket.getPort());
                            serverSocket.send(newPacket);
                        }
                    }
                }
                
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"服务器接收线程出错",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}
