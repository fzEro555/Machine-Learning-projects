public class p1 {
    public static void main( String args[] ) {
        try {
            TrainTestSets tts = new TrainTestSets();
            tts.setOptions( args );
        } // try
        catch ( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        } // catch
    } // p1::main
}
