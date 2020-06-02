package com.github.html.parser.demo;

import com.github.wulijie.htmlparser.AbsTagHandler;

/**
 * 测试识别自定义tag <wu a="参数a" b='参数b' c="参数c">测试wu标签</wu>
 * <leo a="参数a" b='参数b' c="参数c">测试leo标签</leo>
 */
public class WuTagHandler extends AbsTagHandler {
    @Override
    protected String[] getFilterTags() {
        return new String[]{"wu", "leo"};
    }
}
