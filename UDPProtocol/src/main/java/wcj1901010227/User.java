/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author VACation
 */
public class User {
    private String account;    
    private String password;
    private String nickName;
    private String sex;
    private int state=1;
    private Boolean isAdmin=false;
    private String userClass;
//    private InetAddress userAddress;
//    private int userPort;

    private DatagramPacket userPacket;
    public User(String account,String password,String nickName,String sex) 
    {
        this.account=account;
        this.password=password;
        this.nickName=nickName;
        this.sex=sex;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getNickName() {
        return nickName;
    }

    public String getSex() {
        return sex;
    }

    public int getState() {
        return state;
    }

    public Boolean IsAdmin() {
        return isAdmin;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public DatagramPacket getPacket() {
        return userPacket;
    }

    public void setPacket(DatagramPacket userPacket) {
        this.userPacket = userPacket;
    }

//    public InetAddress getUserAddress() {
//        return userAddress;
//    }
//
//    public void setUserAddress(InetAddress userAddress) {
//        this.userAddress = userAddress;
//    }
//
//    public int getUserPort() {
//        return userPort;
//    }
//
//    public void setUserPort(int userPort) {
//        this.userPort = userPort;
//    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }
    
    
}
