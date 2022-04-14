class Example {

   static void main(String[] args) {
      Student st = new Student()
      st.StudentID = 1
      st.marks = 10

      st.DisplayMarks()
   }

}

interface MarksInterface {

   void DisplayMarks()

}

trait Marks implements MarksInterface {

    void DisplayMarks() {
      println('Marks:' + this.marks)
    }

}

class Student implements Marks {

   int StudentID
   int marks

}
