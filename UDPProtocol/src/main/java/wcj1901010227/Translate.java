/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wcj1901010227;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author VACation
 */
public class Translate {
    //将对象转化为字节数组形式，实现对象序列化
    public static byte[] ObjectToByte(Object obj)
    {
        byte[] buffer=null;
        try {
            ByteArrayOutputStream byteArrayOutput =new ByteArrayOutputStream();//字节数组输出流
            ObjectOutputStream objectOutput=new ObjectOutputStream(byteArrayOutput);//对象输出流
            objectOutput.writeObject(obj);
            buffer=byteArrayOutput.toByteArray();
            
            
        } catch (Exception e) {
        }
        return buffer;
    }
    //字节数组转化为Object对象模式，实现对象反序列化
    public static Object ByteToObject(byte[] buffer)
    {
        Object object=null;
        try {
            ByteArrayInputStream byteInput=new ByteArrayInputStream(buffer);
            
            ObjectInputStream objectInput=new ObjectInputStream(byteInput);
            object=objectInput.readObject();
            
        } catch (Exception e) {
        }
        return object;
    }
}
