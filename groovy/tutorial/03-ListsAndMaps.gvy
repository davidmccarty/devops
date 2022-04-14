class Example {

   static void main(String[] args) {
      println 'Lists'

      List list = []
      println list
      for (int x in 0..9) {
         list.add(x)
         println list
      }
      println "reverse ${list.reverse()}"
      println "odds ${list.minus([0, 2, 4, 6, 8])}"
      println "evens ${list.minus([1, 3, 5, 7, 9])}"

      List colors = ['red', 'yellow', 'blue', 'green']
      println colors
      println "sorted ${colors.sort()}"

      Map map = ['key2':'value2']
      println map
      map.put('key1','value1')
      println map

      Map tree = ['key2':'value2'] as TreeMap
      println tree
      tree.put('key1','value1')
      println tree
      println "keys ${tree.keySet()}"
      println "values ${tree.values()}"
   }

}
