package lessons.lesson_5_spring.services;

public enum RequestResult {
    SUCCESS("Request successfull", null),
    FAIL("Request cancelled", null),
    ERROR("Request finished with error", null);

    private String msg;
    private Object data;

    RequestResult(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }

    public RequestResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public RequestResult setData(Object data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
