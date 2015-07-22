package com.felicity.mummyding.felicitychatrobot;

/**
 * Created by mummyding on 15-7-21.
 */
public class MsgItem {
    public static final int TYPE_ROBOT = 0;
    public static final int TYPE_USER  = 1;

    private String chatInfo;
    private int chatObj;

    public MsgItem(String chatInfo, int chatObj) {
        this.chatInfo = chatInfo;
        this.chatObj = chatObj;
    }

    public String getChatInfo() {
        return chatInfo;
    }

    public int getChatObj() {
        return chatObj;
    }





}
