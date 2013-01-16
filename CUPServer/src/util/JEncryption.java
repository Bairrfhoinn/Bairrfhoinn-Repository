package util;

import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.lang.ArrayUtils;

public class JEncryption {

	private static DESKeySpec dks;
	private static SecretKeyFactory keyFactory;
	private static SecretKey myDesKey;
	private static Cipher desCipher;
	private static SecureRandom sr = new SecureRandom();

	public static void main(String[] args) {

		FileInputStream fis = null;
		byte[] start = { 0, 0, 0, 0, 0, 0, 0, 0 };
		boolean flag = true;
		String str = "0110 165218993117577976 890000 0331113842 010463 7011 00 0801021111 0801020000 30 12345678 123456789012345";
		des_ANSI_MAC(hexStringToBytes("5B2A970EE94A3EEC"), start, str.getBytes());
	}

	public static byte[] desAnsiMac(byte[] key, byte[] vector, byte[] data){
		boolean flag = true;
		byte[] b1 = new byte[8];
		byte[] b2 = new byte[8];
		try {
			dks = new DESKeySpec(key);
			keyFactory = SecretKeyFactory.getInstance("DES");
			myDesKey = keyFactory.generateSecret(dks);
			int len = data.length;
			int other = len % 8;
			if (other != 0) {
				byte[] tt = data;
				data = new byte[tt.length + (8 - other)];
				System.arraycopy(tt, 0, data, 0, len);
			}
		
			int arrLen = data.length / 8;
			for (int i = 0; i < arrLen; i++) {
				b1 = ArrayUtils.subarray(data, i * 8, i * 8 + 8);
				if (flag) {
					b2 = JEncryption.Xor(b1, vector);
				} else {
					b2 = JEncryption.Xor(b1, b2);
				}
				if (b2.length != 8) {
					System.out.println();
				}
				b2 = JEncryption.enCryption(b2);
				flag = false;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return b2;
	}
	
	public static void des_ANSI_MAC(byte[] key, byte[] vector, byte[] data) {
		try {
			boolean flag = true;
			dks = new DESKeySpec(key);
			keyFactory = SecretKeyFactory.getInstance("DES");
			myDesKey = keyFactory.generateSecret(dks);
			// String datas = bytesToHexString(data);
			// int len = datas.length();
			// if (len % 16 == 0) {
			// datas += "8000000000000000";
			// } else {
			// datas += "80";
			// len = datas.length();
			// for (int i = 0; i < 15 - len % 16; i++) {
			// datas += "0";
			// }
			// }
			int len = data.length;
			int other = len % 8;
			// ������������8���ֽڣ�����0����
			if (other != 0) {
				byte[] tt = data;
				data = new byte[tt.length + (8 - other)];
				System.arraycopy(tt, 0, data, 0, len);
			}
			byte[] b1 = new byte[8];
			byte[] b2 = new byte[8];
			// ѭ����ȡ���ģ�ÿ�ζ�ȡ8λ����һ�ζ�ȡ����start����Ǳ��?Ȼ����ܣ�֮���ȡ��ȥǰ��8λ�ļ��ܽ���������ټ���

			int arrLen = data.length / 8;
			for (int i = 0; i < arrLen; i++) {
				b1 = ArrayUtils.subarray(data, i * 8, i * 8 + 8);
				if (flag) {
					b2 = JEncryption.Xor(b1, vector);

				} else {
					b2 = JEncryption.Xor(b1, b2);
				}
				if (b2.length != 8) {
					System.out.println();
				}
				b2 = JEncryption.enCryption(b2);
				flag = false;
			}

			System.out.print("���ܽ��");
			for (int i = 0; i < b2.length; i++) {
				System.out.print(Byte.toString(b2[i]));
			}
			System.out.println();
			System.out.println(bytesToHexString(b2));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	// DES����
	public static byte[] enCryption(byte[] source) {

		try {
			// ����Cipherʵ��
			desCipher = Cipher.getInstance("DES/ECB/NoPadding");
			// ����desCipherΪ����ģʽ
			desCipher.init(Cipher.ENCRYPT_MODE, myDesKey, sr);
			try {
				desCipher.doFinal(source);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return desCipher.doFinal(source);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	// DES����
	public static byte[] deCryption(byte[] crypted) {
		try {
			// ����Cipherʵ��
			desCipher = Cipher.getInstance("DES/ECB/NoPadding");
			// ����desCipherΪ����ģʽ
			desCipher.init(Cipher.DECRYPT_MODE, myDesKey, sr);
			return desCipher.doFinal(crypted);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	// �������ֽ��������������㣬����������
	public static byte[] Xor(byte[] h, byte[] b) {
		byte[] out = new byte[h.length];
		for (int i = 0; i < h.length; i++) {
			out[i] = (byte) (h[i] ^ b[i]);
		}
		return out;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}

