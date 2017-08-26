package com.gdrc.panda.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteUtil {

	public static Object byteToObject(byte[] bytes) {
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return obj;
	}

	public static byte[] objectToByte(java.lang.Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return bytes;
	}

	public static byte[] int2byte(int in) {

		byte[] target = new byte[4];
		target[0] = (byte) (in & 0xff);
		target[1] = (byte) (in >> 8 & 0xff);
		target[2] = (byte) (in >> 16 & 0xff);
		target[3] = (byte) (in >> 24 );

		return target;

	}

	public static int byte2int(byte[] res) {

		int t = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >> 8) | (res[3] << 24);
		return t;

	}
	
	public static String byte2string(byte[] in){
		
		
		
		return new String(in);
	}
	public static byte[] string2byte(String in){
		
		return in.getBytes();
	}

}
