class Example {

    static void main(String[] args) {
        try {
            int[] arr = new int[3]
            arr[5] = 7
        } catch (ArrayIndexOutOfBoundsException ex) {
            println 'Catching the exception'
            println ex.getMessage()
        }

        println "Let's move on after the exception"
    }

}
