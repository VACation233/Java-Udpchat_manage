/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

import java.awt.Checkbox;
import java.net.DatagramPacket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author VACation
 */
public class SqlHelper {
    private Connection connection;
    private Statement statement;
    public enum logState
    {
        BANNED,LOG_OUT,LOG_IN
    }
    public SqlHelper() {
        try {
//            String url = "jdbc:mysql://localhost:3306/chatmanager?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
            String url = "jdbc:mysql://localhost:3306/chatmanager?useSSL=false&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8";
            String user = "root";
            String password = "200326";
            connection=DriverManager.getConnection(url, user, password);
            statement=connection.createStatement();
        } catch (Exception e) {
        }
    }
    
    
    public  Vector<String> GetAllUserAccount(String tableName)
    {
        Vector<String> result=new Vector();
        String sqlString="select account from "+tableName;
        try {
            statement.executeQuery(sqlString);
            ResultSet resultSet=statement.getResultSet();
            while (resultSet.next()) {                
                String account=resultSet.getString("Account");
                result.add(account);
            }
            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"错误提示",JOptionPane.ERROR_MESSAGE);
            
        }
        
        return result;
    }
    public String GetPasswordByAccount(String account)
    {
         String password=null;
        try {
            String sqlString="select userPassword from users where account="+"'"+account+"'";
            statement.executeQuery(sqlString);
            ResultSet resultSet=statement.getResultSet();
            while(resultSet.next())
            {
                password=resultSet.getString("userPassword");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"错误提示",JOptionPane.ERROR_MESSAGE);
        }
        return password;
    }
    public boolean UserIsExist(String account)
    {
        boolean check=false;
        String sqlString="select * from users where account= '"+account+"'";
        try {
            ResultSet resultSet=statement.executeQuery(sqlString);
            while(resultSet.next())
            {
                check=true;
            }
        } catch (Exception e) {
        }
        return check;
    }
    public int UserRegister(String account, String passWord, String nickName, String Sex, String classString) {
        int check = 0;
        if (!UserIsExist(account)) {
            String sql = "insert into users(Account,userPassword,nickName,Sex,class) values ('" + account + "','" + passWord + "','" + nickName + "','" + Sex + "','" + classString + "')";

            try {
                check = statement.executeUpdate(sql);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "创建用户时出错", JOptionPane.ERROR_MESSAGE);

            }
        }

        return check;
    }
    public String GetUserNickName(String account)
    {
        String sql="Select nickName from users where account= '"+account+"'";
        String nickName=null;
        try {
            ResultSet resultSet=statement.executeQuery(sql);
            while(resultSet.next())
            {
                nickName=resultSet.getString("nickName");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"查找用户名时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return nickName;
    }
    public boolean LoginCheck(String account,String passWord)
    {
        
        boolean check=false;
        try {
            String userPassword=GetPasswordByAccount(account);
            if(passWord.equals(userPassword))
            {
                check=true;
            }
            else
                check=false;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"登录验证时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return check;
    }
    public boolean isAdmin(String account)
    {
        boolean check=false;
        int result=0;
        try {
            String sql="select Administrator from users where account= "+"'"+account+"'";
            statement.executeQuery(sql);
            ResultSet resultSet=statement.getResultSet();
            while(resultSet.next())
            {
                result=resultSet.getInt("Administrator");
                
            }
            if(result==0)
            {
                check=false;
            }
            else
            {
                check=true;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"检查用户是否为管理员时的错误",JOptionPane.ERROR_MESSAGE);
            
        }
        return check;
    }
    
    public User GenerateUser(String account,String password,String nickName,String sex,DatagramPacket packet)
    {
        User user=new User(account, password, nickName,sex);
        user.setPacket(packet);
//        user.setUserAddress(packet.getAddress());
//        user.setUserPort(packet.getPort());
        user.setIsAdmin(isAdmin(account));
        user.setUserClass(GetUserClass(account));
        
        
        
        return user;
    }
    
    public int GetUserState(String account)
    {
        int state=1;
        try {
            Statement substatement=connection.createStatement();
            String sql="select state from users where account= '"+account+"'";
            substatement.executeQuery(sql);
            ResultSet resultSet=substatement.getResultSet();
            while(resultSet.next())
            {
                state=resultSet.getInt("state");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"获取用户状态时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return state;
    }
    
    public boolean SetUserState(String account,logState state)
    {
        boolean check=false;
        try {
            
            String sql="update users set state= "+state.ordinal()+" where account= '"+account+"'";
            statement.executeUpdate(sql);
            check=true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"改变用户状态时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return check;
    }
    public String GetUserClass(String account)
    {
        String userClass=null;
        try {
            String sqlString="select class from users where account='"+account+"'";
            ResultSet resultSet=statement.executeQuery(sqlString);
            while(resultSet.next())
            {
                userClass=resultSet.getString("class");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"获取用户班级时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return userClass;
    }
    
    public DefaultTableModel GenerateTableModel(String orderString)
    {
        DefaultTableModel tableModel=null;
        if(orderString.equals("查询所有人信息"))
        {
            Vector<String> colList=new Vector<>();//列名集合
            Vector<Vector<Object>> rowList=new Vector<>();//每一行的数据
            colList.add("账号");
            colList.add("密码");
            colList.add("姓名");
            colList.add("性别");
            colList.add("登录状态");
            colList.add("班级");
            colList.add("身份");
            colList.add("注册时间");
            String sqlString="select * from users";
            try {
                ResultSet resultSet=statement.executeQuery(sqlString);
                while(resultSet.next())
                {
                    Vector<Object> subrow=new Vector<>();
                    subrow.add(resultSet.getString("account"));
                    subrow.add(resultSet.getString("userPassword"));
                    subrow.add(resultSet.getString("nickName"));
                    subrow.add(resultSet.getString("sex"));
                    int state=resultSet.getInt("state");
                    String stateString=null;
                    switch (state) {
                        case 0:
                            stateString="被封禁";
                            break;
                        case 1:
                            stateString="未登录";
                            break;
                        case 2:
                            stateString="在线";
                            break;
                        default:
                            throw new AssertionError();
                    }
                    
                    subrow.add(stateString);
                    subrow.add(resultSet.getString("class"));
                    //subrow.add(resultSet.getTimestamp("registertime"));
                    int license=resultSet.getInt("Administrator");
                    String licenseString=null;
                    switch (license) {
                        case 0:
                            licenseString="学生";
                            break;
                        case 1:
                            licenseString="老师";                            
                            break;
                        default:
                            throw new AssertionError();
                    }
                    subrow.add(licenseString);
                    String dateString=resultSet.getTimestamp("registertime").toString();
                    dateString=dateString.substring(0, 10);
                    subrow.add(dateString);
                    rowList.add(subrow);

                }
                tableModel = new DefaultTableModel(rowList, colList) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }

                };

                
                
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"查询信息时出现错误",JOptionPane.ERROR_MESSAGE);
            }
            
            
        }
        else if(orderString.equals("删除学生信息"))
        {
            
            Vector<Object> colList=new Vector<>();//列名集合
            Vector<Vector<Object>> rowList=new Vector<>();//每一行的数据
            colList.add("账号");
            colList.add("密码");
            colList.add("姓名");
            colList.add("性别");
            colList.add("登录状态");
            colList.add("班级");
            colList.add("身份");
            colList.add("注册时间");
            colList.add("选取");
            
            String sqlString="select * from users";
            try {
                ResultSet resultSet=statement.executeQuery(sqlString);
                while(resultSet.next())
                {
                    Vector<Object> subrow=new Vector<>();
                    subrow.add(resultSet.getString("account"));
                    subrow.add(resultSet.getString("userPassword"));
                    subrow.add(resultSet.getString("nickName"));
                    subrow.add(resultSet.getString("sex"));
                    int state=resultSet.getInt("state");
                    String stateString=null;
                    switch (state) {
                        case 0:
                            stateString="被封禁";
                            break;
                        case 1:
                            stateString="未登录";
                            break;
                        case 2:
                            stateString="在线";
                            break;
                        default:
                            throw new AssertionError();
                    }
                    
                    subrow.add(stateString);
                    subrow.add(resultSet.getString("class"));
                    //subrow.add(resultSet.getTimestamp("registertime"));
                    int license=resultSet.getInt("Administrator");
                    String licenseString=null;
                    switch (license) {
                        case 0:
                            licenseString="学生";
                            break;
                        case 1:
                            licenseString="老师";                            
                            break;
                        default:
                            throw new AssertionError();
                    }
                    if(licenseString.equals("老师"))
                        continue;//老师不能删除老师的信息
                    subrow.add(licenseString);
                    String dateString=resultSet.getTimestamp("registertime").toString();
                    dateString=dateString.substring(0, 10);
                    subrow.add(dateString);
                    
                    subrow.add(new Boolean(false));
                    rowList.add(subrow);

                }
                tableModel=new DefaultTableModel(rowList,colList){
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        if(column==8)
                            return true;
                        return false;
                    }
                    @Override
                    public Class<?> getColumnClass(int columnIndex)
                    {
                        if(columnIndex==8)
                            return Boolean.class;
                        return Object.class;
                    }
                    
                    
                };
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"查询信息时出现错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(orderString.equals("修改学生信息"))
        {
            Vector<Object> colList=new Vector<>();//列名集合
            Vector<Vector<Object>> rowList=new Vector<>();//每一行的数据
            colList.add("账号");
            colList.add("密码");
            colList.add("姓名");
            colList.add("性别");
            colList.add("是否封禁");
            colList.add("班级");
            
            
            
            
            String sqlString="select * from users";
            try {
                
                ResultSet resultSet=statement.executeQuery(sqlString);
                while(resultSet.next())
                {
                    Vector<Object> subrow=new Vector<>();
                    String account=resultSet.getString("account");
                    subrow.add(account);
                    subrow.add(resultSet.getString("userPassword"));
                    subrow.add(resultSet.getString("nickName"));
                    subrow.add(resultSet.getString("sex"));
                    int state=resultSet.getInt("state");
                    if(state==0)
                    {
                        subrow.add(new Boolean(true));
                    }
                    else
                    {
                        subrow.add(new Boolean(false));
                    }
                    
                    subrow.add(resultSet.getString("class"));
                    //subrow.add(resultSet.getTimestamp("registertime"));
                      int license=resultSet.getInt("Administrator");
                    String licenseString=null;
                    switch (license) {
                        case 0:
                            licenseString="学生";
                            break;
                        case 1:
                            licenseString="老师";                            
                            break;
                        default:
                            throw new AssertionError();
                    }
                    if(!licenseString.equals("老师")&&
                            state!=2)
                        rowList.add(subrow);//老师不能修改老师的信息
//                    if(GetUserState(account)==SqlHelper.logState.LOG_IN.ordinal())
//                        continue;//无法修改在线学生的信息
                    
                    

                }
                tableModel=new DefaultTableModel(rowList,colList){

                    @Override
                    public Class<?> getColumnClass(int columnIndex)
                    {
                        if(columnIndex==4)
                            return Boolean.class;
                        return Object.class;
                    }
                    
                    
                };
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(),"查询信息时出现错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        return tableModel;
    }
    
    public boolean DeleteInfo(String account)
    {
        boolean check=false;
        String sqlString="delete from users where account='"+account+"'";
        try {
            statement.executeUpdate(sqlString);
            check=true;
            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"删除记录时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return check;
    }
    
    public boolean  UpdateInfo(String account,String password,String nickName,String sex,int state,String classString)
    {
        int currentstate=GetUserState(account);
        boolean check=false;
        if(currentstate==logState.LOG_IN.ordinal()&&state==logState.LOG_OUT.ordinal())
        {
            state=2;
        }
        String sqlString="update users set account= '"+account+"', userpassword= '"+password+"', nickName='"+nickName+
                "', sex= '"+sex+"', state="+state+", class= '"+classString+"' where account = '"+account+"'";
        try {
            statement.executeUpdate(sqlString);
            check=true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"更新数据时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return check;
    }
    
    public int GetSignUpTableNumber()
    {
        int tableNumber=0;
        String sqlString="select count(*) table_name from information_schema.TABLES where table_name like '%sign%' and table_schema = 'chatmanager'";
        try {
            ResultSet resultSet=statement.executeQuery(sqlString);
            if(resultSet!=null)
            {
                while(resultSet.next())
                {
                    tableNumber=resultSet.getInt("table_name");
                }
                
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"查询表格数据时出错",JOptionPane.ERROR_MESSAGE);
        }
        return tableNumber;
    }
    
    public boolean CreateSignUpTable(int i)
    {
        boolean check=false;
        String sqlString="create table signup_"+i+"(number int(3) PRIMARY KEY auto_increment,account varchar(20) not null UNIQUE,"
                + "nickName varchar(20) not null,state int(1) DEFAULT 0,signup_time TIMESTAMP)"
                + "ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 COLLATE=utf8_unicode_ci";
        String sqlString1="insert into signup_"+i+"(account,nickName) SELECT Account,nickName from users where Administrator!=1";
        try {
            statement.execute(sqlString);
            statement.execute(sqlString1);
            check=true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"创建表格时出错",JOptionPane.ERROR_MESSAGE);
        }
        return check;
    }
    public boolean CheckSignUp(String tableName,String userAccount,int state)
    {
        boolean check = false;
        String sqlString=null;
        try {
            if (state == 1) {
                sqlString = "update " + tableName + " set state=1,signup_time=CURRENT_TIMESTAMP where account='" + userAccount + "'";

            }
            if(state==-1)
            {
                sqlString="update " + tableName + " set state=0,signup_time=null where account='" + userAccount + "'";
            }
            statement.execute(sqlString);
            check = true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "修改签到信息时出错", JOptionPane.ERROR_MESSAGE);

        }

        return check;
    }
    
    public ArrayList GetSignupTableName()
    {
        ArrayList<String> tableList=new ArrayList<>();
        String sqlString="select table_name from information_schema.TABLES where table_name like '%sign%' and table_schema = 'chatmanager'";
        try {
            ResultSet resultSet=statement.executeQuery(sqlString);
            while(resultSet.next())
            {
                tableList.add(resultSet.getString("table_name"));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "获取签到表名字的时候出错", JOptionPane.ERROR_MESSAGE);
        }
        return tableList;
    }
    
    public DefaultTableModel GenerateSignUpTableModel(String tableName)
    {
        DefaultTableModel tableModel=null;
        Vector<Object> colList = new Vector<>();//列名集合
        Vector<Vector<Object>> rowList = new Vector<>();//每一行的数据
        colList.add("序号");
        colList.add("账号");
        colList.add("姓名");
        colList.add("签到状态");
        colList.add("签到时间");

        String sqlString = "select * from "+tableName;
        try {
            ResultSet resultSet=statement.executeQuery(sqlString);
            while(resultSet.next())
            {
                Vector<Object> subRow=new Vector();
                int number=resultSet.getInt("number");
                subRow.add(number);
                String account=resultSet.getString("account");
                subRow.add(account);
                String nickName=resultSet.getString("nickName");
                subRow.add(nickName);
                int state=resultSet.getInt("state");
                
                if(state==0)
                {
                    subRow.add(new Boolean(false));
                }
                else
                {
                    subRow.add(new Boolean(true));
                }
                Timestamp timestamp=resultSet.getTimestamp("signup_time");
                String dateString;
                if(timestamp==null)
                    dateString="";
                else
                {
                    dateString=timestamp.toString();
                    dateString=dateString.substring(0, dateString.length()-2);
                }
                    
                subRow.add(dateString);
                rowList.add(subRow);
            }
            
            tableModel = new DefaultTableModel(rowList, colList) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    if (column == 3) {
                        return true;
                    }
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 3) {
                        return Boolean.class;
                    }
                    return Object.class;
                }

            };
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"获取签到表数据时出错",JOptionPane.ERROR_MESSAGE);
            
        }
        return tableModel;
    }
    
    public boolean UpdateSign(String tableNameString,String account,int state)
    {
        boolean check=false;
        String sqlString=null;
        if(state==1)
        {
            sqlString="update "+tableNameString+" set state= "+state+",signup_time=CURRENT_TIMESTAMP where account = '"+account+"'";
        }
        else
        {
            sqlString="update "+tableNameString+" set state= "+state+",signup_time=null where account = '"+account+"'";
        }
        
        try {
            
            statement.execute(sqlString);
            check=true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"修改签到信息出错",JOptionPane.ERROR_MESSAGE);
            
        }
        
        return check;
    }
}
