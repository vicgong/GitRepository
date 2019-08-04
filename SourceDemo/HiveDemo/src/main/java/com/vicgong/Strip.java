package com.vicgong;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

@Description(name = "Strip",
        value = "_FUNC_(String) - from the input string , return String trimed space char.\n"
                + "_FUNC_(String,StripString) - from the input string and stripChars, return String trimed stripChars.",
        extended = "Example:\n"
                + "> SELECT _FUNC_(String) FROM src;\n"
                + "> SELECT _FUNC_(String,StripString) FROM src;")
public class Strip extends UDF {

    /**
     * 去除前导和结束的空白字符
     * @param str
     * @return
     */
    public String evaluate(String str){
        if(str == null) return null;
        return StringUtils.strip(str);
    }

    /**
     * 去除指定字符集合中的任何字符
     * @param str
     * @param stripChars
     * @return
     */
    public String evaluate(String str, String stripChars){
        if(str == null) return null;
        return StringUtils.strip(str,stripChars);
    }

}
