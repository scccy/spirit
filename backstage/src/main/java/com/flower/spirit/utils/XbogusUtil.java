package com.flower.spirit.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;

public class XbogusUtil {

	
	public static Integer[] array = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 10, 11, 12, 13, 14, 15 };

	
	public static String _0x377d66="Dkdpgh4ZKsQB80/Mfvw36XI1R25-WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe=";
	
	public static int[] _0x39ced2(String l) {
	    int[] n = new int[l.length() / 2];
	    for (int u = 0, i = 0; u < l.length(); ) {
	        n[i++] = (array[l.charAt(u++)] << 4) | array[l.charAt(u++)];
	    }
	    return n;
	}

	public static int[] _0x1da120(String l) {
		return _0x39ced2(MD5(intArrayToString(_0x39ced2(MD5(l)))));
	}
	
	public static int _0x2efd11(char l) {
	    String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	    return base64Chars.indexOf(l);
	}

	public static String decodeBase64(String l) {
	    StringBuilder sb = new StringBuilder();
	    for (int n = 0; n < l.length() - 3; n += 4) {
	        int u = _0x2efd11(l.charAt(n));
	        int e = _0x2efd11(l.charAt(n + 1));
	        int t = _0x2efd11(l.charAt(n + 2));
	        int r = _0x2efd11(l.charAt(n + 3));
	        sb.append((char) ((u << 2) | (e >>> 4)));
	        if (l.charAt(n + 2) != '=') {
	            sb.append((char) ((e << 4 & 240) | (t >>> 2 & 15)));
	        }
	        if (l.charAt(n + 3) != '=') {
	            sb.append((char) ((t << 6 & 192) | r));
	        }
	    }

	    return sb.toString();
	}

	public static String _0x4d54ed() {
		return null;
	}
	
	public static String _0x478bb3(int l, int n, int u) {
	    int e = (255 & l) << 16;
	    int t = (255 & n) << 8;
	    int r = e | t | u;
	    return _0x377d66.charAt((16515072 & r) >> 18) + "" + _0x377d66.charAt((258048 & r) >> 12) + "" + _0x377d66.charAt((4032 & r) >> 6) + "" + _0x377d66.charAt(63 & r);
	}
	public static String _0x37f15d() {
		return "";
	}
	
	public static String _0x330d11(int l, int n, int u, int e, int t, int r, int o, int d, int a, int c, int i, int f, int x, int _, int h, int g, int C, int s, int p) {
	    byte[] w = new byte[] {
	            (byte) l, (byte) i, (byte) n, (byte) f, (byte) u, (byte) x, (byte) e, (byte) _, 
	            (byte) t, (byte) h, (byte) r, (byte) g, (byte) o, (byte) C, (byte) d, (byte) s, 
	            (byte) a, (byte) p, (byte) c
	    };
	    return new String(w, StandardCharsets.ISO_8859_1);
	}

	
	public static String _0x330d112(String key, String data) {
	    int[] e = new int[256];
	    for (int i = 0; i < 256; i++) {
	        e[i] = i;
	    }

	    int t = 0;
	    for (int o = 0; o < 256; o++) {
	        t = (t + e[o] + key.charAt(o % key.length())) % 256;
	        int u = e[o];
	        e[o] = e[t];
	        e[t] = u;
	    }

	    t = 0;
	    int a = 0;
	    String r = "";
	    for (int d = 0; d < data.length(); d++) {
	        a = (a + 1) % 256;
	        t = (t + e[a]) % 256;
	        int u = e[a];
	        e[a] = e[t];
	        e[t] = u;
	        r += (char) (data.charAt(d) ^ e[(e[a] + e[t]) % 256]);
	    }

	    return r;
	}

	public static String _0x33baa6(int l, int n, String u) {
	    return Character.toString((char) l) + Character.toString((char) n) + u;
	}

	
	
	public static String intArrayToString(int[] arr) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < arr.length; i++) {
	        sb.append(arr[i]);
	        if (i != arr.length - 1) {
	            sb.append(",");
	        }
	    }
	    return sb.toString();
	}

	public static String getXB(String l) {
		int[] n = _0x39ced2(MD5("d4+pTKoNjJFb5tMtAC3XB9XrDDxlig1kjbh32u+x5YcwWb/me2pvLTh6ZdBVN5skEeIaOYNixbnFK6wyJdl/Lcy9CDAcpXLLQc3QFKIDQ3KkQYie3n258eLS1YFUqFLDjn7dqCRp1jjoORamU2SV"));
		int[] u = _0x39ced2(MD5(intArrayToString(_0x39ced2("d41d8cd98f00b204e9800998ecf8427e"))));
		int[] e = _0x1da120(l);
		long t = new Date().getTime() / 1000;
		int r = 536919696;
		int[]  o = new int[10];
		int[]  d = new int[10];
		String a = "";
		int[] c = new int[]{64, (int)(0.00390625), 1, 8, e[14], e[15], u[14], u[15], n[14], n[15], (int)(t >> 24 & 255), (int)(t >> 16 & 255), (int)(t >> 8 & 255), (int)(t >> 0 & 255), (int)(r >> 24 & 255), (int)(r >> 16 & 255), (int)(r >> 8 & 255), (int)(r >> 0 & 255)};
		int xor = c[0];
		for (int i = 1; i < c.length; i++) {
		    xor ^= c[i];
		}
		c = Arrays.copyOf(c, c.length+1);
		c[c.length-1] = xor;
		for (int i = 0; i < c.length; i += 2) {
		  try {
			  o[i]=(c[i]);
			  d[i] =(c[i+1]);
		} catch (Exception e2) {
		}
		}

		int[] oarr = IntStream.concat(Arrays.stream(o), Arrays.stream(d)).toArray();
//		char [] oarr =o.toString().toCharArray();
		String _0x330d11 = _0x330d11(
		    		 oarr[0],
		    		 oarr[1],
		    		 oarr[2],
		    		 oarr[3],
		    		 oarr[4],
		    		 oarr[5],
		    		 oarr[6],
		    		 oarr[7],
		    		 oarr[8],
		    		 oarr[9],
		    		 oarr[10],
		    		 oarr[11],
		    		 oarr[12],
		    		 oarr[13],
		    		 oarr[14],
		    		 oarr[15],
		    		 oarr[16],
		    		 oarr[17],
		    		 oarr[18]
		    );
	        String i = _0x33baa6(2, 255, _0x330d112(String.valueOf((char) 255), _0x330d11.substring(0, 19)));
	        for (int j = 0; j < i.length(); ) {
	            a += _0x478bb3(i.charAt(j++), i.charAt(j++), i.charAt(j++));
	        }
	        return a;
		}

		String _0x180b4c = _0x37f15d();

    
	
    public static String MD5(String input) {
    	return DigestUtils.md5Hex(input);
    }
    public static void main(String[] args) {
    	//DFSzswVuDAxANxXhteeNrl9WX7rQ
        String result = getXB("aweme_id=7221047525594139944&aid=6383&cookie_enabled=true&platform=PC&downlink=10");
        System.out.println(result);
	}
}
