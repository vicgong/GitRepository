package com.vicgong.Lambda;
public class LambdaDemo3 {
    interface MessageSend{
        void send();
    }
    private static final String message = "final的成员变量";
    public static void main(String[] args) {
        final String message2 = "final的局部变量";
        MessageSend ms = () -> {
          System.out.println(message + "\t" +message2);
        };
        ms.send();
    }
}
