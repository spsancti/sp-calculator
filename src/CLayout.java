import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public class CLayout extends JFrame {
	private static final long serialVersionUID = -7938832965854967458L;

    private JButton 	buttons[];
    private CDisplay 	mLCD;
    private CVisualiser mVisualiser;
    private StringBuilder mLCDNow;
    private CCalc 	mCalc;
    
    private Font resultFont;
    private Font errorFont;

    private boolean mTyped, mLocked;
    
    public CLayout() {
        super("Calculator");
        mCalc 		 = new CCalc();
        mVisualiser  = new CVisualiser();
        mLCDNow = new StringBuilder("0");
        mTyped 		 = true;
        mLocked 	 = false;
        
        initGui();
        addListeners();
        update();
    }

    private void setLocked(boolean lock) {
        mLocked = lock;
        
        if(mLocked) mLCD.setFont(errorFont);
        else      	mLCD.setFont(resultFont);        
    }

    private void performBackspace() {
        if(!mTyped) {
            return;
        }
        
        mLCDNow.deleteCharAt(mLCDNow.length()-1);
        
        if(mLCDNow.length() == 0)
        	mLCDNow.append("0");
        
        else if(mLCDNow.length() == 1 && mLCDNow.charAt(0) == '-')
            mLCDNow.setCharAt(0, '0');
        
        else if(mLCDNow.length() == 2 && mLCDNow.indexOf("-0") == 0) {
            mLCDNow.setLength(1);
            mLCDNow.setCharAt(0, '0');
        }
    }

    private void typeInput(String c) {
        boolean dotAppears = mLCDNow.indexOf(",") != -1;
        boolean minAppears = mLCDNow.indexOf("-") ==  0;
        boolean isComma = c.equals(",");
        
        int currentLength = mLCDNow.length();
        
        if(dotAppears) 	currentLength --; 
        if(minAppears) 	currentLength --;
        if(currentLength == 15)		return;
        if(isComma && dotAppears)	return;
        
        mTyped = true;        
        
        if(mLCDNow.length()  ==  1 
        && mLCDNow.charAt(0) == '0' 
        && !isComma) 
        {
            mLCDNow.replace(0, 1, c);
            return;
        }       
        
        mLCDNow.append(c);
    }

    private void invert() {
        char c = mLCDNow.charAt(0);
        
        if(c == '0' && mLCDNow.length() == 1) return;
        
        if(c != '-')
        	mLCDNow.insert(0, '-');
        else
            mLCDNow.deleteCharAt(0);            
    }

    private void clear() {
        mTyped = false;        
        mLCDNow.setLength(1);
        mLCDNow.setCharAt(0, '0');
        
    }

    private void onError(String error) {        
        mLCD.setText(error);
        setLocked(true);
    }

    private void show(double result) {
    	String tmp = mVisualiser.doFormat(result);
    	System.out.println("CLayout.showResults():tmp : " + tmp);
        mLCD.setText(tmp);
    }

    private void putM(boolean m) {
        mLCD.setNeedPutM(m);
    }

    private void update() {
        if(!mTyped) return;        
        mLCD.setText(mLCDNow.toString());
    }

    private double getLCD() { //wtf commas :(
        return Double.valueOf(mLCDNow.toString().replace(',', '.'));
    }

    public static void main(String[] args) {
        new CLayout();
    }

    public void onInput(String c) {
        if(mLocked) return;
        
        typeInput(c);
        update();
    }

    public void onOperation(CCalc.EOperation operation) {
        if(mLocked) return;
        
        try {
        	
            if(mTyped) mCalc.input(getLCD());            
            mCalc.setOperation(operation);
            
            show(mCalc.getResult());
            
        } catch (CCalcException e) {
            onError(e.getMessage());
            mCalc.clear();
        }
        clear();
    }

    public void onEquals() {
        if(mLocked) return;
        
        try {
        	if(mTyped) mCalc.input(getLCD());
            mCalc.calculate();
            
            show(mCalc.getResult());
            
        } catch (CCalcException e) {
            onError(e.getMessage());
            mCalc.clear();
        }
        clear();
    }

    public void onFunction(CCalc.EFunction function) {
    	   if(mLocked) return;
           
           try {
           	if(mTyped) mCalc.input(getLCD());
            mCalc.calculateFunction(function);
               
            show(mCalc.getResult());
               
           } catch (Exception e) {
               onError(e.getMessage());
               mCalc.clear();
           }
           clear();
    }
    
    public void onBackspace() {
        if(mLocked) return;
        
        performBackspace();
        update();
    }

    public void onChangeSign() {
        if(mLocked)  return;
        
        if(mTyped) {
            invert();
            update();
        } 
        else {
            try {
                mCalc.changeSign();
                show(mCalc.getResult()); 
                
            } catch (CCalcException e) {
                onError(e.getMessage());
                clear();
                mCalc.clear();
            }
        }
    }

    //mb buggy
    public void onClear() {
        clear();
        mCalc.clearElement();
        //sometimes nothing happens here :(
        mTyped = true;
        
        update();
        setLocked(false);
    }


    public void onClearAll() {
        mCalc.clear();
        onClear();
        
    }

    public void onMemory(CCalc.EMemory action) {
        if(mLocked) return;
    	if(mTyped) 
        {
        	double tmp = getLCD();
            mCalc.input(tmp);
            clear();
            if(tmp == 0.0 || tmp == -0.0) {
            	//spike detected half an hour before presentation
                mTyped = true;
                update();
                mTyped = false;
            }
        }
        
        mCalc.memoryAction(action);
        if(action != CCalc.EMemory.READ) {
            putM(mCalc.getMemory() != 0.0);
            return;
        }
        
        
        try {
            show(mCalc.getResult());
            
        } catch (CCalcException e) {
            onError(e.getMessage());
            mCalc.clear();
        }
        clear();
    }

    private void addListeners() {
        KeyListener onKey = new CListenerContainer.onKeyPress(this);
        for(int i = 0; i < buttons.length; i++) 
            buttons[i].addKeyListener(onKey);
        
        //TODO: Auto-generated method stub
        
        buttons[CLayoutServicer.BUTTONS.PLUS.ordinal()] .addActionListener(new CListenerContainer.onOperation(this, CCalc.EOperation.PLUS));
        buttons[CLayoutServicer.BUTTONS.MINUS.ordinal()].addActionListener(new CListenerContainer.onOperation(this, CCalc.EOperation.MINUS));
        buttons[CLayoutServicer.BUTTONS.DIV.ordinal()]	 .addActionListener(new CListenerContainer.onOperation(this, CCalc.EOperation.DIV));
        buttons[CLayoutServicer.BUTTONS.MUL.ordinal()]	 .addActionListener(new CListenerContainer.onOperation(this, CCalc.EOperation.MUL));

        buttons[CLayoutServicer.BUTTONS.EQUAL.ordinal()].addActionListener(new CListenerContainer.onEqual(this));

        buttons[CLayoutServicer.BUTTONS.SQRT.ordinal()]   .addActionListener(new CListenerContainer.onFunction(this, CCalc.EFunction.SQRT));
        buttons[CLayoutServicer.BUTTONS.PERCENT.ordinal()].addActionListener(new CListenerContainer.onFunction(this, CCalc.EFunction.PERCENT));
        buttons[CLayoutServicer.BUTTONS.INVERSE.ordinal()].addActionListener(new CListenerContainer.onFunction(this, CCalc.EFunction.INVERSE));

        ActionListener onNumberClick = new CListenerContainer.onNumber(this);

        for(int i = 0; i < 10; i++) buttons[i].addActionListener(onNumberClick);
        
        buttons[CLayoutServicer.BUTTONS.DOT.ordinal()].addActionListener(onNumberClick);

        buttons[CLayoutServicer.BUTTONS.BACKSPACE.ordinal()].addActionListener(new CListenerContainer.onBackspace(this));
        buttons[CLayoutServicer.BUTTONS.PLUS_MINUS.ordinal()].addActionListener(new CListenerContainer.onChangeSign(this));

        buttons[CLayoutServicer.BUTTONS.CLEAR.ordinal()].addActionListener(new CListenerContainer.onClear(this));
        buttons[CLayoutServicer.BUTTONS.CLEAR_ALL.ordinal()].addActionListener(new CListenerContainer.onClearAll(this));

        buttons[CLayoutServicer.BUTTONS.MEMORY_CLEAR.ordinal()].addActionListener(new CListenerContainer.onMemory(this, CCalc.EMemory.CLEAR));
        buttons[CLayoutServicer.BUTTONS.MEMORY_SET.ordinal()]  .addActionListener(new CListenerContainer.onMemory(this, CCalc.EMemory.SET));
        buttons[CLayoutServicer.BUTTONS.MEMORY_READ.ordinal()] .addActionListener(new CListenerContainer.onMemory(this, CCalc.EMemory.READ));
        buttons[CLayoutServicer.BUTTONS.MEMORY_PLUS.ordinal()] .addActionListener(new CListenerContainer.onMemory(this, CCalc.EMemory.PLUS));
        buttons[CLayoutServicer.BUTTONS.MEMORY_MINUS.ordinal()].addActionListener(new CListenerContainer.onMemory(this, CCalc.EMemory.MINUS));
    }

    private void initGui() {
    	//TODO: Auto-generated method stub
        Container pane = getContentPane();
        GridBagConstraints constraints = new GridBagConstraints();
        pane.setLayout(new GridBagLayout());

        mLCD = new CDisplay();
        
        resultFont = new Font("Helvetica", Font.PLAIN, 13);
        errorFont  = new Font("Helvetica", Font.PLAIN, 11);
        
        mLCD.setFont(resultFont);
        mLCD.setDisabledTextColor(Color.BLACK);
        mLCD.setEnabled(false);
        mLCD.setHorizontalAlignment(JTextField.RIGHT);

        Dimension size 	= new Dimension(37, 33);
        Insets noMargin = new Insets(0, 0, 0, 0);

        buttons = new JButton[CLayoutServicer.BUTTONS.values().length];
        for(int i=0; i<buttons.length; i++) {
            buttons[i] = new JButton(CLayoutServicer.BUTTONS_NAMES[i]);
            buttons[i].setPreferredSize(size);
            buttons[i].setMargin(noMargin);
        }

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.insets = new Insets(8,3,3,3);
        constraints.gridwidth = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        pane.add(mLCD, constraints);

        constraints.fill = GridBagConstraints.NONE;

        constraints.gridwidth = 1;

        constraints.gridy = 1;

        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.MEMORY_CLEAR.ordinal()], constraints);

        constraints.gridx = 1;
        pane.add(buttons[CLayoutServicer.BUTTONS.MEMORY_READ.ordinal()], constraints);

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.MEMORY_SET.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.MEMORY_PLUS.ordinal()], constraints);

        constraints.gridx = 4;
        pane.add(buttons[CLayoutServicer.BUTTONS.MEMORY_MINUS.ordinal()], constraints);

        constraints.gridy = 2;

        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.BACKSPACE.ordinal()], constraints);

        constraints.gridx = 1;
        pane.add(buttons[CLayoutServicer.BUTTONS.CLEAR.ordinal()], constraints);

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.CLEAR_ALL.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.PLUS_MINUS.ordinal()], constraints);

        constraints.gridx = 4;
        pane.add(buttons[CLayoutServicer.BUTTONS.SQRT.ordinal()], constraints);

        constraints.gridy = 3;

        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.SEVEN.ordinal()], constraints);

        constraints.gridx = 1;
        pane.add(buttons[CLayoutServicer.BUTTONS.EIGHT.ordinal()], constraints);

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.NINE.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.DIV.ordinal()], constraints);

        constraints.gridx = 4;
        pane.add(buttons[CLayoutServicer.BUTTONS.PERCENT.ordinal()], constraints);

        constraints.gridy = 4;

        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.FOUR.ordinal()], constraints);

        constraints.gridx = 1;
        pane.add(buttons[CLayoutServicer.BUTTONS.FIVE.ordinal()], constraints);

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.SIX.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.MUL.ordinal()], constraints);

        constraints.gridx = 4;
        pane.add(buttons[CLayoutServicer.BUTTONS.INVERSE.ordinal()], constraints);

        constraints.gridy = 5;

        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.ONE.ordinal()], constraints);

        constraints.gridx = 1;
        pane.add(buttons[CLayoutServicer.BUTTONS.TWO.ordinal()], constraints);

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.THREE.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.MINUS.ordinal()], constraints);

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridheight = 2;
        constraints.gridx = 4;
        pane.add(buttons[CLayoutServicer.BUTTONS.EQUAL.ordinal()], constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridy = 6;

        constraints.gridheight = 1;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        pane.add(buttons[CLayoutServicer.BUTTONS.ZERO.ordinal()], constraints);

        constraints.fill = GridBagConstraints.NONE;

        constraints.gridwidth = 1;

        constraints.gridx = 2;
        pane.add(buttons[CLayoutServicer.BUTTONS.DOT.ordinal()], constraints);

        constraints.gridx = 3;
        pane.add(buttons[CLayoutServicer.BUTTONS.PLUS.ordinal()], constraints);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
       // setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
