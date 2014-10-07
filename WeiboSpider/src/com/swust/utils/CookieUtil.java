package com.swust.utils;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class CookieUtil {

	public static BasicCookieStore stringToCookie(String strCookie) throws  Exception{
		String cookies[]=strCookie.split(";");
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie [] cookie= new BasicClientCookie[cookies.length]; 
		for(int i = 0;i<cookies.length;i++){
			String cookieStr[] = cookies[i].split("=");
			try {
				cookie[i] = new BasicClientCookie(cookieStr[0],cookieStr[1]);
			} catch (Exception e) {
				throw new Exception();
			}
		}
		cookieStore.addCookies(cookie);;
		return cookieStore;
		//return testGetCookie();
	}
	public static void main(String[] args) {
		String cookieValue="SINAGLOBAL=233511789011.5179.1374487122470; ULV=1374744032229:6:6:6:527782098793.7598.1374744031954:1374737442297; ALF=1377336019; un=pangjuntaoer@163.com; wvr=5; UOR=,,login.sina.com.cn; SUS=SID-1805841815-1374744020-GZ-l1brd-a058db7d42bd10cdbdf029bb868fdd7a; SUE=es%3Dd02bd5ecb3a965c3402889d2f02f9b48%26ev%3Dv1%26es2%3D54ff8755374f755a5701b04a23f55ce6%26rs0%3Do5LIQiJe8HHZlW80j0SQFo4DfCDdtz24Y7ejuae0fkjNRJFmu1ThnJoAb0FmY16%252Fnvkhd17%252BJwn88mBw%252BoL231BrYx0AeiWWGSPZ00DJajUy0KfjN4y1Aafiktr7DXKp85iLaH%252FcJSBbGU3qMViA0YSQcK8V5H6y40DpmvvWp3Y%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1374744020%26et%3D1374830420%26d%3Dc909%26i%3D0d56%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D2%26st%3D0%26uid%3D1805841815%26name%3Dpangjuntaoer%2540163.com%26nick%3D%25E6%2583%2585%25E9%259D%259E%25E5%25BE%2597%25E5%25B7%25B2%26fmp%3D%26lcp%3D2011-12-30%252019%253A29%253A48; SSOLoginState=1374744020; SWB=usrmd1083; _s_tentry=login.sina.com.cn; Apache=527782098793.7598.1374744031954; WBStore=7df5532e3799ef19|undefined";
		try {
			System.out.println(stringToCookie(cookieValue));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static BasicCookieStore testGetCookie(){
		BasicClientCookie [] cookie= new BasicClientCookie[17]; 
		//.weibo.com
		cookie[0]=new BasicClientCookie("USRUG","usrmdins1540_24"); //会话
		cookie[1]=new BasicClientCookie("USRHAWB","usrmdins540_95"); //会话
		cookie[2]=new BasicClientCookie("SinaRot_wb_r_topic","32");//静态
		cookie[3]=new BasicClientCookie("SINAGLOBAL","1412020411931.556.1375261451990");//客户端唯一标识
		cookie[4]=new BasicClientCookie("un","pangjuntaoer@163.com");
		cookie[5]=new BasicClientCookie("wvr","5");
		//会话
		cookie[6]=new BasicClientCookie("SUE","SUE=es%3Df4b9c5673d0c0053b29a7f832a084bc3%26ev%3Dv1%26es2%3Dd8ec35476ed1d91e82d0d6bbef43132d%26rs0%3D2je8og5kigMIBpSn9YoTVF8UKvEeEXYPu6Kx1n1UdwnAiYY5Rasp3UjIoSvQ%252BMxWO2y9TXPrN%252FmYMM%252B9ap96ar%252Bg6%252B%252BvP2d9keBFNaYIuGewUUBeiK3u3yx2Md3BvqbfRhHDTtJjlaZNuBfyHRBfkfTcx0WVyKdJIBlWx7q0nEY%253D%26rv%3D0");
		//会话
		cookie[7]=new BasicClientCookie("SUP","cv%3D1%26bt%3D1375321369%26et%3D1375407769%26d%3Dc909%26i%3D0a0e%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D2%26st%3D0%26uid%3D1805841815%26name%3Dpangjuntaoer%2540163.com%26nick%3D%25E6%2583%2585%25E9%259D%259E%25E5%25BE%2597%25E5%25B7%25B2%26fmp%3D%26lcp%3D2011-12-30%252019%253A29%253A48");
		//会话
		cookie[8]=new BasicClientCookie("SUS","SID-1805841815-1375321369-GZ-e9d04-7c369831260f8987e678507785cc619e");
		cookie[9]=new BasicClientCookie("ALF","1377940892"); //静态 生命 1月，变化 
		//会话
		cookie[10]=new BasicClientCookie("SSOLoginState","1375348893");
		cookie[11]=new BasicClientCookie("_s_tentry","login.sina.com.cn");//静态
		cookie[12]=new BasicClientCookie("UOR",",,login.sina.com.cn");//静态
		//会话
		cookie[13]=new BasicClientCookie("Apache","1793541492413.6106.1375348900168");
		//静态 生命周期1年
		cookie[14]=new BasicClientCookie("ULV","1375321377716:2:1:2:4216585190311.367.1375321377709:1375261452002");
		//s.weibo.com
		cookie[15]=new BasicClientCookie("WBStore","6cbb98a0932759aa|undefined"); //会话
		cookie[16]=new BasicClientCookie("SWB","usrmd1080");//会话
		
		BasicCookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookies(cookie);
		return cookieStore;
	}
	
	public static String cookieToString(BasicCookieStore storeCookie){
		/////////////////Testing
		if(storeCookie!=null)
			return "SINAGLOBAL=1412020411931.556.1375261451990; ULV=1375405630546:6:5:6:8563741863007.853.1375405625562:1375348900201; ALF=1377997575; un=pangjuntaoer@163.com; wvr=5; UOR=,,login.sina.com.cn; SUS=SID-1805841815-1375405578-GZ-an72s-df7aa7c67b05abd681c4e5074d727653; SUE=es%3Db062c53369d839f2c7b709ff1aa412e6%26ev%3Dv1%26es2%3Dcbfe6467059dfc68243c55f441390e33%26rs0%3DO4cEf2gtld%252B%252FV8T1Wd3RpziKyF7%252FY1S%252Bbi1eKhi6%252FmN1LRP0%252BC%252FrLYrKhq1m%252BjmBnhut8Rp3nbrMsDWoMEHmh2JKyFM47InQSVaaIRekN5HpJI2sUnL38M1tuV%252FzT1mVMMNP3BShVByZIr3dgg46NURAv3%252B0LyvQm4Szx1wt0nc%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1375405578%26et%3D1375491978%26d%3Dc909%26i%3D420a%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D2%26st%3D0%26uid%3D1805841815%26name%3Dpangjuntaoer%2540163.com%26nick%3D%25E6%2583%2585%25E9%259D%259E%25E5%25BE%2597%25E5%25B7%25B2%26fmp%3D%26lcp%3D2011-12-30%252019%253A29%253A48; SSOLoginState=1375405578; SWB=usrmd1071; WBStore=15022e1d3a6dfef4|undefined; _s_tentry=weibo.com; Apache=8563741863007.853.1375405625562; ULOGIN_IMG=13754085595883";
		/////
		List<Cookie> cookieList = storeCookie.getCookies();
		String strCookie="";
		for (int i = 0; i < cookieList.size(); i++) {
			Cookie cookie = cookieList.get(i);
			strCookie+=cookie.getName()+"="+cookie.getValue()+";";
		}
		if(strCookie.endsWith(";")){
			strCookie = strCookie.substring(0, strCookie.length()-1);
		}
		return strCookie;
	}
}
