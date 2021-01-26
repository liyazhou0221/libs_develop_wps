/**
 *	 文件名：EfficientAdapter.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：
 */
package cn.wps.moffice.demo.fileManager;

import java.io.File;

import cn.wps.moffice.demo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class EfficientAdapter extends BaseAdapter 
{
    private LayoutInflater mInflater;
    private Bitmap mIcon1;
    private Bitmap mIcon2;
    private File[] fileList;
    public EfficientAdapter(Context context, File[] files) 
    {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        fileList = files;
        // Icons bound to the rows.
        mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.format_folder);//文件夹
        mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.format_unkown);//文件
    }

    public int getCount() 
    {
        return fileList.length;
    }
 
    public Object getItem(int position) 
    {
        return position;
    }

    public long getItemId(int position) 
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {
        ViewHolder holder;
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.list_item_icon_text, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        if (fileList.length != 0)
        holder.text.setText(fileList[position].getName());
       if (fileList[position].isFile())
    	   holder.icon.setImageBitmap(mIcon2);
       else 
    	   holder.icon.setImageBitmap(mIcon1);
        return convertView;
    }

    static class ViewHolder
    {
        TextView text;
        ImageView icon;
    }
}