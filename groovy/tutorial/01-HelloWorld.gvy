class Example {

   String hello = 'Hello'

   static void main(String[] args) {
      // Using a simple println statement to print output to the console
      println('Hello World')

      // Examples of basic types
      int x = 5
      long y = 100L
      float a = 10.56f
      double b = 10.5e40
      BigInteger bi = 30g
      BigDecimal bd = 3.5g

      println x
      println y
      println a
      println b
      println bi
      println bd

      // Example of range operator
      Range range = 5..10
      println 'Print Range'
      println range
      println range.get(2)

      // Call static method
      println 'This is how methods work in groovy'
      displayName('Fred')
      displayName()
      displayName(getName())

      // Class instane
      Example instance = new Example()
      instance.sayHello()
   }

   // Example of method
   static void displayName(String name = 'Ginger') {
      println('Hello ' + name)
   }

   // Example of method with result
   static String getName() {
      return 'Frank'
   }

   // Example of instance method
   public void sayHello() {
      println(this.hello + ' from instance')
   }

}
