package com.github.html.parser.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.wulijie.htmlparser.AbsTagHandler;
import com.github.wulijie.htmlparser.HtmlParser;

import org.xml.sax.Attributes;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView tagTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuilder builder = new StringBuilder();
        String html = "测试一下标签<br/><wu a=\"参数a\" b='参数b' c=\"参数c\">测试wu标签</wu> <br/><leo a=\"参数a\">测试leo标签</leo> <br/> <font color=\"#827717\">测试标签font</font>";
        String aTag = "<a href=\"http://www.baidu.com\">百度</a>";//默认颜色为 colorAccent 也可以自定义这个标签换颜色
        //测试邮箱
        String address = "<br/><address>Written by <a href=\"mailto:androidzk@163.com\">Donald Duck</a>.<br> Visit us at:<br>163.com<br>Box 564, Disneyland<br>USA</address>";
        String hTag = "<br/><h1>这是标题 1</h1><h2>这是标题 2</h2><h3>这是标题 3</h3><h4>这是标题 4</h4><h5>这是标题 5</h5><h6>这是标题 6</h6>";
        String delTag = "1打有<del>20</del>件";
        builder.append(html);
        builder.append("<br/>");//测试换行
        builder.append(aTag);//测试tag标签
        builder.append(address);
        builder.append(hTag);
        builder.append(delTag);
        tagTv = findViewById(R.id.tagTv);
        WuTagHandler tagHandler = new WuTagHandler();
        tagHandler.setTagColor(getResources().getColor(R.color.tag));
        tagHandler.setOnTagClickListener(new AbsTagHandler.OnTagClickListener() {
            @Override
            public void onTagClick(String tag, Attributes attributes, View widget) {
                String aValue = HtmlParser.getValue(attributes, "a");//获取当前标签内a属性的值
                switch (tag) {
                    case "wu":
                        //处理wu标签的点击事件
                        String bValue = HtmlParser.getValue(attributes, "b");//获取当前标签内b属性的值
                        String cValue = HtmlParser.getValue(attributes, "c");//获取当前标签内c属性的值
                        Log.i(TAG, "tag->" + tag + ";a属性的值=" + aValue + ";b属性的值=" + bValue + ";c属性的值=" + cValue);
                        Toast.makeText(MainActivity.this, "tag->" + tag + ";a属性的值=" + aValue + ";b属性的值=" + bValue + ";c属性的值=" + cValue, Toast.LENGTH_SHORT).show();
                        break;
                    case "leo":
                        Log.i(TAG, "tag->" + tag + ";a属性的值=" + aValue);
                        Toast.makeText(MainActivity.this, "tag->" + tag + ";a属性的值=" + aValue, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
//        tagTv.setText(HtmlParser.buildSpannedText(html, tagHandler));
//        tagTv.setClickable(true);
//        tagTv.setMovementMethod(LinkMovementMethod.getInstance());
        HtmlParser.setHtml(tagTv, builder.toString(), tagHandler);
    }
}
