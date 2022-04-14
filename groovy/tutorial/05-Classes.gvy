class Example {

   static void main(String[] args) {
      Student st = new Student()
      st.StudentID = 1

      st.Marks1 = 10
      st.name = 'Joe'

      println(st.name)
   }

}

class Person {

   public String name
   public Person() { }

}

class Student extends Person {

   int StudentID
   int Marks1

   public Student() {
      super()
   }

}
