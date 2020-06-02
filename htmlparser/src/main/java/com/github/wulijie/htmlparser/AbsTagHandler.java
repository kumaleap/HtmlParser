package com.github.wulijie.htmlparser;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import org.xml.sax.Attributes;

import java.util.Stack;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

//需要支持自定义html标签
public abstract class AbsTagHandler implements HtmlParser.TagHandler {
    private String[] filterTags;//要过滤的自定义标签
    private Stack<Integer> tagIns;//识别对应tag的开始坐标
    private Stack<Attributes> attributesStack;//识别出对应tag的参数
    private OnTagClickListener onTagClickListener;
    private boolean underlineText;//tag 默认去掉下划线 可设置为true
    private @ColorInt
    int tagColor = -1;

    public AbsTagHandler() {
        this.filterTags = getFilterTags();
        this.tagIns = new Stack<>();
        this.attributesStack = new Stack<>();
    }

    protected abstract String[] getFilterTags();

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.onTagClickListener = listener;
    }

    //设置Tag是否支持下划线
    public void setTagUnderline(boolean underlineText) {
        this.underlineText = underlineText;
    }

    public void setTagColor(@ColorInt int tagColor) {
        this.tagColor = tagColor;
    }

    @Override
    public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
        boolean hasTag = hasTag(tag);
        if (hasTag) {//如果配置了自定义标签
            if (opening) handleStartTag(tag, output, attributes);
            else handleEndTag(tag, output, attributes);
        }
        return hasTag;
    }

    //标签开始
    protected void handleStartTag(String tag, Editable output, Attributes attributes) {
        tagIns.push(output.length());
        attributesStack.push(attributes);
    }

    //标签结束
    protected void handleEndTag(String tag, Editable output, Attributes attributes) {
        Attributes stackAttr = attributesStack.pop();
        int start = tagIns.pop();
        int end = output.length();
        HtmlSpan span = new HtmlSpan(tag, stackAttr, onTagClickListener);
        span.setUnderlineText(underlineText);// 是否支持下划线
        span.setColor(tagColor);
        output.setSpan(span, start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //从a哦
    private boolean hasTag(String tag) {
        if (filterTags == null || filterTags.length == 0) return false;//不需要识别自定义标签 交给系统处理
        for (String filterTag : filterTags) {
            if (filterTag.toLowerCase().equalsIgnoreCase(tag.toLowerCase())) {//如果存在过滤 则交给自己处理
                return true;
            }
        }
        return false;
    }

    //高亮及点击事件
    public class HtmlSpan extends ClickableSpan implements View.OnClickListener {
        private Attributes attributes;
        private OnTagClickListener listener;
        private String tag;
        private boolean underlineText;
        private @ColorInt
        int color;

        public HtmlSpan(String tag, Attributes attributes, OnTagClickListener listener) {
            this.attributes = attributes;
            this.listener = listener;
            this.tag = tag;
        }

        //设置Tag是否支持下划线
        public void setUnderlineText(boolean underlineText) {
            this.underlineText = underlineText;
        }

        //设置标签颜色
        public void setColor(@ColorInt int color) {
            this.color = color;
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            if (color != -1)
                ds.setColor(color);
            ds.setUnderlineText(underlineText);// 去掉下划线
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (listener != null) listener.onTagClick(tag, attributes, widget);
        }
    }

    public interface OnTagClickListener {
        void onTagClick(String tag, Attributes attributes, View widget);
    }
}
