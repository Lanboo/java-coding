/**
 * https://segmentfault.com/a/1190000010841143
 */
public class AsciiUtil {

    public static final char SBC_SPACE = 12288; // 全角空格 12288

    public static final char DBC_SPACE = 32; //半角空格 32

    // ASCII character 33-126 <-> unicode 65281-65374
    public static final char ASCII_START = 33;

    public static final char ASCII_END = 126;

    public static final char UNICODE_START = 65281;

    public static final char UNICODE_END = 65374;

    public static final char DBC_SBC_STEP = 65248; // 全角半角转换间隔

    /**
     * 全角转半角
     */
    public static char sbc2dbc(char src) {
        if(src == SBC_SPACE) {
            return DBC_SPACE;
        }

        if(src >= UNICODE_START && src <= UNICODE_END) {
            return (char) (src - DBC_SBC_STEP);
        }

        return src;
    }

    /**
     * 全角转半角
     */
    public static String sbc2dbc(String src) {
        if(src == null) {
            return null;
        }
        char[] c = src.toCharArray();
        for(int i = 0; i < c.length; i++) {
            c[i] = sbc2dbc(c[i]);
        }
        return new String(c);
    }

    /**
     * 半角转全角
     * @Author WeiXiaowei
     * @CreateDate 2020年3月31日下午3:59:45
     */
    public static char dbc2sbc(char src) {
        if(src == DBC_SPACE) {
            return SBC_SPACE;
        }
        if(src >= ASCII_START && src <= ASCII_END) {
            return (char) (src + DBC_SBC_STEP);
        }
        return src;
    }

    /**
     * 半角转全角
     */
    public static String dbc2sbc(String src) {
        if(src == null) {
            return null;
        }
        char[] c = src.toCharArray();
        for(int i = 0; i < c.length; i++) {
            c[i] = dbc2sbc(c[i]);
        }
        return new String(c);
    }

    public static void main(String[] args) {
        System.out.println(AsciiUtil.dbc2sbc('a'));
        System.out.println(AsciiUtil.dbc2sbc("我azAZ10?."));
        System.out.println(AsciiUtil.dbc2sbc("我ａｚＡＺ１０？．"));
        System.out.println(AsciiUtil.sbc2dbc("我azAZ10?."));
        System.out.println(AsciiUtil.sbc2dbc("我ａｚＡＺ１０？．"));
    }
}
