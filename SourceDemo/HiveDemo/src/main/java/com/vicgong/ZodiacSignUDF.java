package com.vicgong;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.Description;

@Description(name = "Zodiac",
        value = "_FUNC_(date) - from the input data string " +
                "or separate month and day arguments,return the sign of the Zodiac.",
        extended = "Example:\n"
                + "> SELECT _FUNC_(date_string) FROM src;\n"
                + "> SELECT _FUNC_(month,day) FROM src;")
public class ZodiacSignUDF extends UDF {
    private SimpleDateFormat df = null;

    /**
     * 在默认构造函数中初始化df
     */
    public ZodiacSignUDF() {
        df = new SimpleDateFormat("MM-dd-yyyy");
    }

    /**
     * 由出生日期获取星座
     *
     * @param birthday
     * @return
     */
    public String evaluate(Date birthday) {
        return this.evaluate(birthday.getMonth() + 1, birthday.getDay());
    }

    /**
     * 由出生日期获取星座
     *
     * @param birthday
     * @return
     */
    public String evaluate(String birthday) {
        Date date = null;
        try {
            date = df.parse(birthday);
        } catch (Exception e) {
            //如果日期转换失败，就表明星座是未知的
            return null;
        }
        return this.evaluate(date.getMonth() + 1, date.getDay());
    }

    /**
     * 由月份和出生日获取星座
     *
     * @param month
     * @param day
     * @return
     */
    public String evaluate(Integer month, Integer day) {
        if (month == 1) {
            if (day < 20) {
                return "Capricorn";
            } else {
                return "Aquarius";
            }
        }
        if (month == 2) {
            if (day < 19) {
                return "Capricorn";
            } else {
                return "Pisces";
            }
        }
        if (month == 3) {
            if (day < 20) {
                return "Pisces";
            } else {
                return "Aries";
            }
        }
        if (month == 4) {
            if (day < 20) {
                return "Aries";
            } else {
                return "Taurus";
            }
        }
        if (month == 5) {
            if (day < 20) {
                return "Taurus";
            } else {
                return "Gemini";
            }
        }
        if (month == 6) {
            if (day < 21) {
                return "Gemini";
            } else {
                return "Cancer";
            }
        }
        if (month == 7) {
            if (day < 22) {
                return "Cancer";
            } else {
                return "Leo";
            }
        }
        if (month == 8) {
            if (day < 23) {
                return "Leo";
            } else {
                return "Virgo";
            }
        }
        if (month == 9) {
            if (day < 22) {
                return "Virgo";
            } else {
                return "Libra";
            }
        }
        if (month == 10) {
            if (day < 24) {
                return "Libra";
            } else {
                return "Scorpio";
            }
        }
        if (month == 11) {
            if (day < 22) {
                return "Scorpio";
            } else {
                return "Sagittarius";
            }
        }
        if (month == 12) {
            if (day < 22) {
                return "Sagittarius";
            } else {
                return "Capricorn";
            }
        }

        return null;
    }
}