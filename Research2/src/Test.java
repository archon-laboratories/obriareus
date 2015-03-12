public class Test
{

    public static void main(String[] args) {
        SayHelloInterface hello;
        hello = howdy("HelloEnglish");
        System.out.println("First Hello: " + hello.sayHello());
        hello = howdy("HelloSwedish");
        System.out.println("Second Hello: " + hello.sayHello());
    }

    public static SayHelloInterface howdy(String whichClass) {
        try {
            Class clazz = Class.forName(whichClass);
            return (SayHelloInterface) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}