package lessons.lesson_8_hibernate.controllers;

public interface Controller <REQ, RESP> {
    RESP execute(REQ request) throws Exception;
    Class<REQ> getRequestClass();

}
