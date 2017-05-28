public class CCalc {

	public enum EOperation {
    	DIV, 
    	MUL, 
    	MINUS, 
    	PLUS, 
    	NONE
    	};

    public enum EFunction {
    	SQRT, 
    	PERCENT, 
    	INVERSE
    	};

    public enum EMemory {
    	CLEAR, 
    	SET, 
    	READ, 
    	PLUS, 
    	MINUS
    	};

	
	private static final String OVERFLOW 			= "Переполнение";
	private static final String RESULT_UNDEFINED 	= "Результат не определен";
	private static final String ILLEGAL_VALUE 		= "Недопустимый ввод";
	private static final String DIVISION_BY_ZERO 	= "Деление на ноль невозможно";
     
    private static final int ACT_NOTHING   = 0;
    private static final int ACT_OPERATION = 1;
    private static final int ACT_CALCULATE = 2;

	private static final int ACC = 0;
    private static final int ARG = 1;
    private static final int INP = 2;
    
    private double mMemoryContent	= 0.0;
    private double mOpStack[]		= new double[3];
    private EOperation mLastOper 	= EOperation.NONE;
    
    private int mLastAct;
    private int mResultIdx;
    private boolean mNewValueTyped;
    

    public CCalc() {
        
    	clear();
    }

    public void input(double in) {
        
    	mOpStack[INP] = in;
        mNewValueTyped = true;
    }

    public void clearElement() {
    	
        if(mLastAct != ACT_OPERATION)
        	mOpStack[mResultIdx] = 0;
        else
        	mOpStack[ARG] = 0;        
    }

    public double getResult() throws CCalcException {
    	
        if(Double.isInfinite(mOpStack[mResultIdx]))
            throw new CCalcException(OVERFLOW);
        
        return mOpStack[mResultIdx];
    }

    public double getMemory() {
    	
        return mMemoryContent;
    }

    public void setOperation(EOperation operation) throws CCalcException {
        
    	switch (mLastAct) 
    	{
            case ACT_NOTHING:
            {
                if(mNewValueTyped) {
                    mOpStack[ACC] = mOpStack[ARG] = mOpStack[INP];
                    mNewValueTyped = false;
                }
            }break;
            
            case ACT_OPERATION:
            {
                if(mNewValueTyped) {
                    mOpStack[ARG] = mOpStack[INP];
                    mNewValueTyped = false;
                    doOperation();
                }
                mOpStack[ARG] = mOpStack[ACC];
            }break;
            
            case ACT_CALCULATE:
            {
                if(mNewValueTyped) {
                    mOpStack[ACC] = mOpStack[INP];
                    mOpStack[ARG] = mOpStack[INP];
                    mNewValueTyped = false;
                } 
                else 
                    mOpStack[ARG] = mOpStack[ACC];                
            }   break;
        }
    	
        mLastOper = operation;
        mLastAct = ACT_OPERATION;
        mResultIdx = 0;
    }

    public void calculateFunction(EFunction f) throws CCalcException {
        if(!mNewValueTyped) {
            mOpStack[INP] = mOpStack[ACC];
        }
        input(doFunction(f, mOpStack[INP], mOpStack[ACC]));
        mResultIdx = 2;
    }

    //aware of copypaste!
    public void calculate() throws CCalcException {
        switch (mLastAct) {
            case ACT_NOTHING:
            {
                if(mNewValueTyped) {
                    mOpStack[ARG] = mOpStack[INP];
                    mOpStack[ACC] = mOpStack[INP];
                    mNewValueTyped = false;
                }
            }break;
            
            case ACT_OPERATION:
            {
                if(mNewValueTyped) {
                    mOpStack[ARG] = mOpStack[INP];
                    mNewValueTyped = false;
                }
                doOperation();

            }break;
            
            case ACT_CALCULATE:
            {
                if(mNewValueTyped) {
                    mOpStack[ACC] = mOpStack[INP];
                    mNewValueTyped = false;
                }
                doOperation();
            }break;
        }
        mLastAct = ACT_CALCULATE;
        mResultIdx = 0;
    }

    public double doFunction(EFunction f, double arg0, double arg1) throws CCalcException {
        double res = 0.0;
        switch (f) 
        {
			case INVERSE:
			{
			    if(arg0 == 0.0)  throw new CCalcException(DIVISION_BY_ZERO);
			    
			    res = 1 / arg0;
			    
			    if(res == 0.0 || Double.isInfinite(res)) throw new CCalcException(OVERFLOW);
			    
			}break;
			
			case PERCENT:
			{               
				res = arg1 / 100 * arg0; //reduce number of overflow situations
			    if(res == 0.0 && arg0 != 0.0 && arg1 != 0.0)  throw new CCalcException(OVERFLOW);
			    
			}break;
			
			case SQRT:
			{
			    if(arg0 < 0.0) throw new CCalcException(ILLEGAL_VALUE);
			    
			    res = Math.sqrt(arg0);
			    
			    if(res == 0.0 && arg0 != 0.0) throw new CCalcException(OVERFLOW);
			    
			}break;
        }
        return res;
    }

    public void doOperation() throws CCalcException {
        double res = 0.0;
        switch (mLastOper) 
        {
            case DIV:
            {
                if(mOpStack[ARG] == 0) {
                	
                	if(mOpStack[ACC] == 0)
                		throw new CCalcException(RESULT_UNDEFINED);
                    
                	throw new CCalcException(DIVISION_BY_ZERO);
                }
                res = mOpStack[ACC] / mOpStack[ARG];
                
                if((res == 0.0 || Double.isInfinite(res)) && mOpStack[ACC] != 0.0) throw new CCalcException(OVERFLOW);                
            }   break;
            
            case MUL:
            {
                res = mOpStack[ACC] * mOpStack[ARG];
                
                if((res == 0.0 || Double.isInfinite(res)) 
                	&& mOpStack[ACC] != 0 
                	&& mOpStack[ARG] != 0)// :(
                    	throw new CCalcException(OVERFLOW);
                
            }break;
            
            case PLUS:
            {
                res = mOpStack[ACC] + mOpStack[ARG];
                
                if((mOpStack[ACC] != 0 && mOpStack[ARG] != 0) && (res == 0.0 || Double.isInfinite(res))
                && !(mOpStack[ACC] != mOpStack[ARG] && Math.abs(mOpStack[ACC]) == Math.abs(mOpStack[ARG])))

                	throw new CCalcException(OVERFLOW);
                
            }break;

            case MINUS:
            {
		        res = mOpStack[ACC] - mOpStack[ARG];
		        
		        if((res == 0.0 || Double.isInfinite(res)) && mOpStack[ACC] != mOpStack[ARG])
		            throw new CCalcException(OVERFLOW);
		        
            }break;
            case NONE: return;
        }
        mOpStack[ACC] = res;
    }

    public void changeSign() {
    	
        input(-1.0 * mOpStack[mResultIdx]);
        
        mResultIdx = 2;
    }

    public void memoryAction(EMemory action) {
        int pos;
        if(mNewValueTyped) 
        	pos = INP;
        else
        	pos = mResultIdx;
        
        switch (action) {
            case CLEAR:
            {
                mMemoryContent = 0;
            }break;
            case READ:
            {
                input(mMemoryContent);
                mResultIdx = 2;
            }break;
            case SET:
            {
                mMemoryContent = mOpStack[pos];
               
            }break;
            case PLUS:
            {
                mMemoryContent += mOpStack[pos];
            }break;
            case MINUS:
            {
                mMemoryContent -= mOpStack[pos];
            }break;
        }
    }

    public void clear() {
    	
        mOpStack[ACC] = 0.0;
        mOpStack[ARG] = 0.0;
        mOpStack[INP] = 0.0;
        mResultIdx = ACC;
        mLastOper = EOperation.NONE;
        mNewValueTyped = false;
        mLastAct = ACT_NOTHING;
    }
}
