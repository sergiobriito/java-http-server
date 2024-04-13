public enum Status {
    OK(200),
    NOT_FOUND(404),
    NOT_IMPLEMENTED(501);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
