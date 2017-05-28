import junit.framework.TestCase;

public class test1 extends TestCase{
  public void testTrue() throws Exception{
	assertTrue(true);

        CCalc tester = new CCalc();

        // assert statements
	tester.input(10);
	tester.setOperation(CCalc.EOperation.MUL);
	tester.input(0);	
	tester.calculate();
        assertEquals("10 x 0 must be 0.0", 0.0, tester.getResult());

	tester.clear();
	tester.input(0);
	tester.setOperation(CCalc.EOperation.MUL);
	tester.input(10);
	tester.calculate();
        assertEquals("10 x 0 must be 0.0", 0.0, tester.getResult());

  }
}
