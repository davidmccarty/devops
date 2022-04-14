class Example {
   static void main(String[] args) {
      def str1 = "Hello";
      def clos = {param -> println "${str1} ${param}"}
      clos.call("World");

      // We are now changing the value of the String str1 which is referenced in the closure
      str1 = "Goodbye";
      clos.call("World");
   }
}
