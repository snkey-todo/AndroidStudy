package com.example.taskdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskdemo.R;
import com.example.taskdemo.bean.TaskItem;
import com.example.taskdemo.util.CircularImage;
import com.example.taskdemo.util.Util;
import com.lidroid.xutils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class TasksAdapter extends BaseAdapter {
    private Context mContext;
    private List<TaskItem> mList;
    SimpleDateFormat df;
    BitmapUtils mBitmapUtils;

    public TasksAdapter(Context mContext, List<TaskItem> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        mBitmapUtils = Util.getBitmapUtils(mContext, R.drawable.talk_portrait);
        df = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public TaskItem getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.taskitem, null);
            holder.mCircularImage = (CircularImage) convertView.findViewById(R.id.iv_index_show);
            holder.projectImg = (ImageView) convertView.findViewById(R.id.iv_itemtask_stateimg);
            holder.mContenttv = (TextView) convertView.findViewById(R.id.tv_task_content);
            holder.projectTag = (ImageView) convertView.findViewById(R.id.task_tag);
            holder.mtvCourse = (TextView) convertView.findViewById(R.id.tv_itemtask_week);
            holder.mtvInstructor = (TextView) convertView.findViewById(R.id.tv_itemtask_person);
            holder.mCreate = (TextView) convertView.findViewById(R.id.tv_task_creater);
            holder.mtvTimeline = (TextView) convertView.findViewById(R.id.tv_itemtask_createtime);
            holder.mtvTitle = (TextView) convertView.findViewById(R.id.tv_itemtask_name);
            holder.mAttachment = (TextView) convertView.findViewById(R.id.tv_attachment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mBitmapUtils.display(holder.mCircularImage, mList.get(position).getSender_head_img());

        if (mList.get(position).getStatus() <= 10 && mList.get(position).getStatus() > 0) {

            if (mList.get(position).getStatus() == 9) {
                holder.projectImg.setImageResource(R.drawable.project_delay);
                holder.projectTag.setImageResource(R.drawable.task_arrow4);
            } else {
                holder.projectImg.setImageResource(R.drawable.project_ing);
                holder.projectTag.setImageResource(R.drawable.task_arrow2);
            }
        } else if (mList.get(position).getStatus() > 10 && mList.get(position).getStatus() <= 20) {
            holder.projectImg.setImageResource(R.drawable.project_finish);
            holder.projectTag.setImageResource(R.drawable.task_arrow1);
        } else if (mList.get(position).getStatus() == 31) {
            holder.projectImg.setImageResource(R.drawable.project_cancel);
            holder.projectTag.setImageResource(R.drawable.task_arrow3);
        }
        String str = "";
        if (mList.get(position).getTask_executer_list().size() > 3) {
            for (int i = 0; i < 2; i++) {
                str = str + mList.get(position).getTask_executer_list().get(i).getExecuter_name() + "、";
            }
            holder.mtvInstructor.setText("执行人：" + str.substring(0, str.length() - 1) + "等" + mList.get(position).getTask_executer_list().size() + "人");
        } else {
            for (int i = 0; i < mList.get(position).getTask_executer_list().size(); i++) {
                str = str + mList.get(position).getTask_executer_list().get(i).getExecuter_name() + "、";
            }
            holder.mtvInstructor.setText("执行人：" + str.substring(0, str.length() - 1));
        }
        holder.mCreate.setText("发起人：" + mList.get(position).getSender_user_name());
        holder.mtvTitle.setText(mList.get(position).getTitle());
        holder.mContenttv.setText(mList.get(position).getContent());
        if (mList.get(position).getTask_finish_day() < 0) {
            holder.mtvCourse.setText("任务还未开始/共" + mList.get(position).getTask_total_day() + "天");
        } else {
            holder.mtvCourse.setText("第" + mList.get(position).getTask_finish_day() + "天/共" + mList.get(position).getTask_total_day() + "天");
        }
        holder.mtvTimeline.setText(spiltTime(mList.get(position).getStart_date()));

        return convertView;
    }

    private String spiltTime(String str) {
        return str.substring(5, "yyyy-MM-dd".length() + 1);
    }

    class ViewHolder {
        CircularImage mCircularImage;
        TextView mAttachment;
        TextView mContenttv;
        TextView mCreate;
        ImageView projectImg;
        ImageView projectTag;
        TextView mtvTitle;
        TextView mtvInstructor;
        TextView mtvCourse;
        TextView mtvTimeline;
    }
}
