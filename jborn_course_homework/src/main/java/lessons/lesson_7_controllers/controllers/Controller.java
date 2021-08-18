package lessons.lesson_7_controllers.controllers;

public interface Controller <REQ, RESP> {
    RESP execute(REQ request) throws Exception;
    Class<REQ> getRequestClass();

}
