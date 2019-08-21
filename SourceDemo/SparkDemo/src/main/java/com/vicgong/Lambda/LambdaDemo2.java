package com.vicgong.Lambda;

/**
 * 定义含返回值的函数式接口
 */
interface MathOperation {
    int operation(int a, int b);
}

/**
 * 定义无返回值的函数式接口
 */
interface MessageService {
    void sayMessage(String message);
}

/**
 * 定义无返回值无参数的函数式接口
 */
interface OutputService{
    void OutputNumber();
}

public class LambdaDemo2 {
    private static int operate(int a, int b, MathOperation mathOperation){
        return mathOperation.operation(a, b);
    }
    public static void main(String[] args) {
        /**
         * 无参数传入且有返回值的lambda表达式示例
         */
        MathOperation addition = (int a, int b) -> a + b; //使用类型声明
        MathOperation subtraction = (a, b) -> a - b;      // 不用类型声明
        MathOperation multiplication = (int a, int b) -> { return a * b; }; // 大括号中必须指定return
        MathOperation division = (int a, int b) -> a / b; // 不使用大括号，则可直接返回
        System.out.println("10 + 5 = " + operate(10, 5, addition));
        System.out.println("10 - 5 = " + operate(10, 5, subtraction));
        System.out.println("10 x 5 = " + operate(10, 5, multiplication));
        System.out.println("10 / 5 = " + operate(10, 5, division));

        /**
         * 有参数传入且没有返回值的lambda表达式示例
         */
        MessageService ms = message -> System.out.println(message); //一个参数可省略括号
        ms.sayMessage("Hello");

        /**
         * 无参数传入且没有返回值的lambda表达式示例
         */
        OutputService os = () -> {
            for(int i=0; i<=9; i++){
                System.out.println(i);
            }
        };
        os.OutputNumber();
    }
}

