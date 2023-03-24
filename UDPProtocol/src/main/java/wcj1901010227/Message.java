/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

import java.io.*;
import java.net.*;

/**
 *
 * @author VACation
 */
public class Message implements Serializable{
    
    private String userAccount=null;
    private String userPassword=null;
    private String nickName=null;
    private String Sex=null;
    private String userClass=null;
    private boolean isAdmin;
    //消息类型：M_LOGIN为用户登录消息；M_SUCCESS为登录成功；M_FAILURE为登陆失败
    //M_ACK为服务器对用户登录的回应消息；M_MEG为会话消息；M_QUIT为用户退出消息
    //M_REG为注册消息;M_PASSWORD为找回密码消息;M_BANNED为用户被封禁消息
    //M_DUPLICATE为重复登录消息;M_PRIVATE为私聊消息;M_PRIVATE_QUIT为退出私聊消息
    //M_PRIVATE_CONFIRM为私聊确认;M_Query为查询请求;M_Silence为禁言;M_Speak为解除全体禁言
    //M_READY为即将发起签到;M_START为签到开始;M_END为签到结束;M_OK为签到成功
    private boolean isSigning=false;
    private String type=null;
    private String text=null;//消息体
    private InetAddress toAddr=null;//目标用户地址
    private int toPort;//目标用户端口
    private String targetAccount=null;//目标用户账号
    private String targetNickName=null;

    public String getUserAccount() {
        return userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public InetAddress getToAddr() {
        return toAddr;
    }

    public int getToPort() {
        return toPort;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setToAddr(InetAddress toAddr) {
        this.toAddr = toAddr;
    }

    public void setToPort(int toPort) {
        this.toPort = toPort;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }

    public String getUserNickName() {
        return nickName;
    }

    public void setUserNickName(String nickName) {
        this.nickName = nickName;
    }
    
    public String getUserSex() {
        return Sex;
    }

    public void setUserSex(String Sex) {
        this.Sex = Sex;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getTargetNickName() {
        return targetNickName;
    }

    public void setTargetNickName(String targetNickName) {
        this.targetNickName = targetNickName;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public boolean isIsSigning() {
        return isSigning;
    }

    public void setIsSigning(boolean isSigning) {
        this.isSigning = isSigning;
    }
    
}
