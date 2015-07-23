package com.felicity.mummyding.felicitychatrobot;

/**
 * Created by mummyding on 15-7-22.
 */
public class VALUES {
    /**
     * 接口KEY 一个Key每天限制访问5k次
     * 可以多申请几个Key 直接加到数组里面即可
     */
    public static final String[] KEYS = {
            "438f2495a228535ce458acabf4efcf42"
    };
    public static final int CODE_TEXT = 100000;
    public static final int CODE_TRAIN = 305000;
    public static final int CODE_FLIGHT= 306000;
    public static final int CODE_LINK= 200000;
    public static final int CODE_NEWS = 302000;
    public static final int CODE_MENU = 308000;
    public static final int CODE_KEYOVER = 40004;

    public static final String ERROR = "<b>呀,不好... 服务器出问题了,这下我真成傻逼了！<b>";
    public static final String INTERNET_ERROR = "<b>你没网不行呀→_→[抠鼻态]<b>";
    public static final String HELLO = "你叫什么";
    public static String LOCATION = "";
    public static String CITY = "";
    public static double Latitude = 0;
    public static double Longitude = 0;


}
