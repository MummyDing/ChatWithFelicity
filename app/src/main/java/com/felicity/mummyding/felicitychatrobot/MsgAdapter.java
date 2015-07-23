package com.felicity.mummyding.felicitychatrobot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

/**
 * Created by mummyding on 15-7-21.
 */
public class MsgAdapter extends ArrayAdapter<MsgItem> {

    public MsgAdapter(Context context, int resource, List<MsgItem> objects) {
        super(context, resource, objects);
    }

    Drawable drawable = null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgItem item = getItem(position);
        ViewHolder viewHolder;
        // 初始化
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_msg_view,null);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) convertView.findViewById(R.id.right_layout);
            viewHolder.left_msg = (TextView) convertView.findViewById(R.id.left_msg);
            viewHolder.right_msg = (TextView) convertView.findViewById(R.id.right_msg);
            // 这里可以设置链接可以点击
            viewHolder.left_msg.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.right_msg.setMovementMethod(LinkMovementMethod.getInstance());
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * 这里实现ImageGetter接口，下载图片，一定要开新线程！！！
         */
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String msource) {
                final String source =msource;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url;
                        try {
                            url = new URL(source);
                            drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ;
                        }
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return drawable;
            }
        };


        /**
         * 根据消息发送人动态加载布局
         */
        if(item.getChatObj() == MsgItem.TYPE_ROBOT){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.left_msg.setText(Html.fromHtml(item.getChatInfo(),imgGetter,null));
        }else{
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.right_msg.setText(Html.fromHtml(item.getChatInfo(),imgGetter,null));
        }

        return convertView;
    }
    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView left_msg;
        TextView right_msg;
    }
}
