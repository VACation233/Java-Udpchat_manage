/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

/**
 *
 * @author VACation
 */
import java.sql.*;
import java.util.Iterator;
import java.util.Timer;
import java.util.Vector;

public class Test {
    
    private SqlHelper sqlHelper;
    public Test()
    {
        sqlHelper=new SqlHelper();
    }
    public static void main(String[] args) {
        Timer timer=new Timer();
        Test test=new Test();
        Vector<String> accountStrings=test.sqlHelper.GetAllUserAccount("users");
        Iterator it=accountStrings.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
            
        }
        
        
    }
    
}
