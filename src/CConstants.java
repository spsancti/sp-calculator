
public class CConstants {

    static final int ACCUMULATOR = 0;
    static final int ARGUMENT = 1;
    static final int INPUT = 2;

    static final int NO_ACTION = 0;
    static final int OPERATION_ACTION = 1;
    static final int EQUAL_ACTION = 3;

    static final String OVERFLOW = "Overflow";
    static final String DIVISION_BY_ZERO = "Division by zero";
    static final String ILLEGAL_VALUE = "Illegal value";
    static final String RESULT_UNDEFINED = "Result undefined";
    
    enum OPERATION {DIV, MUL, MINUS, PLUS, NONE}

    enum FUNCTION {SQRT, PERCENT, BACKWARD}

    enum MEMORY {CLEAR, SET, READ, PLUS, MINUS}

}
