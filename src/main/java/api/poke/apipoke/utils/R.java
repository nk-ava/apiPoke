package api.poke.apipoke.utils;

import java.util.Date;

public class R {
    private int code;
    private String msg;
    private long time;

    private R() {
    }

    public static R NullQQ() {
        R r = new R();
        r.setCode(-1);
        r.setMsg("qq号不能为空");
        r.setTime(new Date().getTime());
        return r;
    }

    public static R AVATARError() {
        R r = new R();
        r.setCode(-2);
        r.setMsg("请求qq头像出错");
        r.setTime(new Date().getTime());
        return r;
    }

    public static R UNAVAILABLEMethod() {
        R r = new R();
        r.setCode(-3);
        r.setMsg("无效的方法名");
        r.setTime(new Date().getTime());
        return r;
    }

    public static R OTHERError() {
        R r = new R();
        r.setCode(-4);
        r.setMsg("服务器未知错误");
        r.setTime(new Date().getTime());
        return r;
    }

    private void setCode(int code) {
        this.code = code;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    private void setTime(long time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public long getTime() {
        return time;
    }
}
