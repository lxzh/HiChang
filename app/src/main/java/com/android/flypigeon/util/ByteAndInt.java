package com.android.flypigeon.util;

public class ByteAndInt {

	public static byte[] short2ByteArray(short s) {
		byte[] shortBuf = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = (shortBuf.length - 1 - i) * 8;
			shortBuf[i] = (byte) ((s >>> offset) & 0xff);
		}
		return shortBuf;
	}

	public static final int byteArray2Short(byte[] b) {
		return (b[0] << 8) + (b[1] & 0xFF);
	}

	public static byte[] int2ByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
	public static byte[] int2ByteArray3(int value) {
		byte[] b = new byte[3];
		for (int i = 0; i < 3; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
	public static byte[] int2ByteArray1(int value) {
		byte[] b = new byte[1];
		for (int i = 0; i < 1; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}	
	public static byte int2Byte(int value) {
		byte b = 0;
			b = (byte) (value & 0xFF);
		return b;
	}
	public static final int byteArray2Int(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}
	public static final int byteArray2Int1(byte[] b) {
		return (b[0] & 0xFF);
	}
	public static final int byte2Int(byte b) {
		return (b & 0xFF);
	}
	public static final int byteArray2Int3(byte[] b) {
		return ((b[0] & 0xFF) << 16) + ((b[1] & 0xFF) << 8)+ (b[2] & 0xFF);
	}
	public static void main(String[] args){
		byte b[] = int2ByteArray1(222);
		System.out.println(byteArray2Int1(b));
	}
	public static byte int2ByteArray11(int value) {
		byte[] b = new byte[1];
		for (int i = 0; i < 1; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b[0];
	}

	public static byte[] longToByteArray(long a) {
		byte[] bArray = new byte[8];
		for (int i = 0; i < bArray.length; i++) {
			bArray[i] = new Long(a & 0XFF).byteValue();
			a >>= 8;
		}
		return bArray;
	}

	public static long byteArrayToLong(byte[] bArray) {
		long a = 0;
		for (int i = 0; i < bArray.length; i++) {
			a += (long) ((bArray[i] & 0XFF) << (8 * i));
		}
		return a;
	}
}
