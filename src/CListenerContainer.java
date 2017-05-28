import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class CListenerContainer {

    public static class onNumber implements ActionListener {

        private CLayout mGUI;

        public onNumber(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton)e.getSource();
            mGUI.onInput(source.getText());
        }
    }

    public static class onOperation implements ActionListener {

        private CCalc.EOperation mOp;
        private CLayout mGUI;

        public onOperation(CLayout gui, CCalc.EOperation op) {
            mOp = op;
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onOperation(mOp);
        }
    }

    public static class onEqual implements ActionListener {

        private CLayout mGUI;

        public onEqual(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onEquals();
        }
    }

    public static class onFunction implements ActionListener {

        private CCalc.EFunction mFunc;
        private CLayout mGUI;

        public onFunction(CLayout gui, CCalc.EFunction function) {
            this.mFunc = function;
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onFunction(mFunc);
        }
    }

    public static class onBackspace implements ActionListener {

        private CLayout mGUI;

        public onBackspace(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onBackspace();
        }
    }

    public static class onChangeSign implements ActionListener {

        private CLayout mGUI;

        public onChangeSign(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onChangeSign();
        }
    }

    public static class onClear implements ActionListener {

        private CLayout mGUI;

        public onClear(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onClear();
        }

    }

    public static class onClearAll implements ActionListener {

        private CLayout mGUI;

        public onClearAll(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onClearAll();
        }

    }

    public static class onMemory implements ActionListener {

        private CLayout mGUI;
        private CCalc.EMemory mAct;

        public onMemory(CLayout gui, CCalc.EMemory action) {
            this.mGUI = gui;
            this.mAct = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mGUI.onMemory(mAct);
        }

    }

    public static class onKeyPress implements KeyListener {

        private CLayout mGUI;

        public onKeyPress(CLayout gui) {
            this.mGUI = gui;
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ',':
                    mGUI.onInput(e.getKeyChar()+"");
                    return;
                case '.':
                    mGUI.onInput(",");
                    return;
                case '+':
                    mGUI.onOperation(CCalc.EOperation.PLUS);
                    return;
                case '-':
                    mGUI.onOperation(CCalc.EOperation.MINUS);
                    return;
                case '*':
                    mGUI.onOperation(CCalc.EOperation.MUL);
                    return;
                case '/':
                    mGUI.onOperation(CCalc.EOperation.DIV);
                    return;
                case '%':
                    mGUI.onFunction(CCalc.EFunction.PERCENT);
                    return;
            }
            switch (e.getKeyCode()) {
                case KeyEvent.VK_EQUALS:
                case KeyEvent.VK_ENTER:
                    mGUI.onEquals();
                    return;
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_DELETE:
                    mGUI.onClearAll();
                    return;
                case KeyEvent.VK_BACK_SPACE:
                    mGUI.onBackspace();

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        	
        }
    }

}
