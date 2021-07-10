package lessons.lesson_2_all_in_one_class;

public class TestClass {
    private int outerNonStaticPrivate = -1;
    public int outerNonStaticPublic = -2;
    private static int outerStaticPrivate = -3;
    public static int outerStaticPublic = -4;

    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        testClass.getInnerClasses();
    }

    private static void staticMethod() {
        System.out.println("This is Outer class static method");
    }
    private void nonStaticMethod() {
        System.out.println("This is Outer class non-static method");
        staticMethod();
    }


    public void getInnerClasses() {
        int a = InnerStaticClass.innerStaticPublic;
        InnerClass innerNonStatic = new InnerClass();
        InnerStaticClass innerStatic = new InnerStaticClass();
        System.out.println(innerNonStatic.innerPrivate);
        System.out.println(innerNonStatic.innerPublic);

        System.out.println(innerStatic.innerPrivate);
        System.out.println(InnerStaticClass.innerStaticPrivate);
        System.out.println(innerStatic.innerPublic);
        System.out.println(InnerStaticClass.innerStaticPublic);
    }

    class InnerClass {
        private int innerPrivate = 4;
        public int innerPublic = 5;

        InnerClass() {
            System.out.println("Calling outer class static method from non-static inner class");
            staticMethod();
            System.out.println("Calling outer class non-static method from non-static inner class");
            nonStaticMethod();
            getOuterClassVariables();
        }

        void getOuterClassVariables() {
            System.out.println(outerNonStaticPrivate);
            System.out.println(outerNonStaticPublic);
            System.out.println(outerStaticPrivate);
            System.out.println(outerStaticPublic);
        }
    }

    static class InnerStaticClass {
        private int innerPrivate = 0;
        private static int innerStaticPrivate = 1;
        public int innerPublic = 2;
        public static int innerStaticPublic = 3;

        InnerStaticClass() {
            System.out.println("Calling outer class static method from static inner class");
            staticMethod();
            getOuterClassVariables();

            // can't access from static context
//            System.out.println("Calling outer class non-static method from static inner class");
//            nonStaticMethod();
        }

        void getOuterClassVariables() {
            System.out.println(outerStaticPrivate);
            System.out.println(outerStaticPublic);

            // can't access from static context
//            System.out.println(outerNonStaticPrivate);
//            System.out.println(outerNonStaticPublic);
        }
    }
}

class ParallelClass {


    public void getInnerClasses() {
        TestClass.InnerClass innerNonStatic = new TestClass().new InnerClass();
        TestClass.InnerStaticClass innerStatic = new TestClass.InnerStaticClass();
        System.out.println(innerNonStatic.innerPublic);


        System.out.println(innerStatic.innerPublic);
        System.out.println(TestClass.InnerStaticClass.innerStaticPublic);

        // can't access private members
//        System.out.println(innerNonStatic.innerPrivate);
//        System.out.println(innerStatic.innerPrivate);
//        System.out.println(TestClass.InnerStaticClass.innerStaticPrivate);
    }
}
