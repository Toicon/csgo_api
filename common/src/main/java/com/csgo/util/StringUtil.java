package com.csgo.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class StringUtil {
    /**
     * 空字符串
     */
    private static final String NULLSTR = "";
    public static final String EMPTY_JSON = "{}";
    public static final char C_BACKSLASH = '\\';
    public static final char C_DELIM_START = '{';
    private static Random randGen = null;
    private static Random numberRandGen = null;
    private static Random capitalRandGen = null;
    private static char[] numbersAndLetters = null;
    private static char[] numbersAndCapitalLetters = null;
    private static char[] numbers = null;

    public static String Long2String(Long long1, int i) {
        if (long1 == null)
            return null;
        String s = long1.toString();
        if (s.length() >= i)
            return s.substring(s.length() - i);
        int j = s.length();
        for (int k = 0; k < i - j; k++)
            s = (new StringBuilder()).append("0").append(s).toString();

        return s;
    }

    public static String formatting(String str) {
        if (str == null || "".equals(str) || "null".equals(str)) {
            return "";
        }
        return str;
    }


    /**
     * 生成随机字符串
     */
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }


    /**
     * 生成随机字符串 大写
     */
    public static final String randomStringCapital(int length) {
        if (length < 1) {
            return null;
        }
        if (capitalRandGen == null) {
            capitalRandGen = new Random();
            numbersAndCapitalLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndCapitalLetters[capitalRandGen.nextInt(35)];
        }
        return new String(randBuffer);
    }

    /**
     * 生成随机字符串 数字
     */
    public static final String randomNumber(int length) {
        if (length < 1) {
            return null;
        }
        if (numberRandGen == null) {
            numberRandGen = new Random();
            numbers = ("01234567890123456789").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbers[numberRandGen.nextInt(19)];
        }
        return new String(randBuffer);
    }

    /**
     * 处理批次号，流水号
     *
     * @param batchNo  批次号
     * @param systrace 流水号
     * @return
     */
    public static final String[] systrace(BigDecimal batchNo, String systrace) {
        String flag = "00";
        BigDecimal systraceBig = new BigDecimal(systrace);
        if (systraceBig.compareTo(new BigDecimal(999998)) > -1) {
            batchNo = batchNo.add(new BigDecimal(1));
            systraceBig = new BigDecimal(0);
            flag = "01";
        } else {
            systraceBig = systraceBig.add(new BigDecimal(1));
        }
        String[] str = {String.valueOf(batchNo), String.valueOf(systraceBig), flag};
        return str;
    }

    /**
     * 生成trans_id
     *
     * @param date     时间
     * @param tid      终端号
     * @param systrace 流水号
     * @return
     */
    public static final String transId(Date date, String tid, String systrace) {
        StringBuilder transId = new StringBuilder();
        String day = dateToStr(date, "yyyyMMdd");

        Long time = date.getTime();//毫秒数
        String millisecond = String.valueOf(time).substring(0, 8);

        String wm = String.valueOf(System.nanoTime());//微秒数
        String microsecond = wm.substring(0, 6);

        transId.append(day).append(tid).append(systrace).append(millisecond).append(microsecond);
        return transId.toString();
    }

    public static String dateToStr(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String str = format.format(date);
        return str;
    }

    /**
     * @param str
     * @param i
     * @return
     */
    public static String stringFormat(String str, int i) {
        if (str == null)
            str = "0";
        if (str.length() >= i)
            return str.substring(str.length() - i);
        int j = str.length();
        for (int k = 0; k < i - j; k++)
            str = (new StringBuilder()).append("0").append(str).toString();

        return str;
    }

    /*
     *判断一个字符串是否都是数字
     **/
    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    /*
     *判断手机号是否合法 -- 验证比较宽松
     **/
    public static boolean isMobileNumber(String mobiles) {
        return Pattern.compile("^((1[2-8][0-9])|(15[^4,\\D])|(18[^1^4,\\D]))\\d{8}").matcher(mobiles).matches();
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N                                                 1000000
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    public static boolean IsIDcard(String str) {
        String regex = "(\\d{14}\\w)|\\d{17}\\w";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //获取当前时间
    public static String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    //获取当前日期
    public static String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (isEmpty(strPattern) || isEmpty(argArray)) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();
        // 初始化定义好的长度以获得更好的性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
        int handledPosition = 0;
        int delimIndex;// 占位符所在位置
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
            if (delimIndex == -1) {
                if (handledPosition == 0) {
                    return strPattern;
                } else { // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            } else {
                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {
                        // 转义符之前还有一个转义符，占位符依旧有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(Convert.utf8Str(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    } else {
                        // 占位符被转义
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(C_DELIM_START);
                        handledPosition = delimIndex + 1;
                    }
                } else {
                    // 正常占位符
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(Convert.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }
        // 加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }

}
