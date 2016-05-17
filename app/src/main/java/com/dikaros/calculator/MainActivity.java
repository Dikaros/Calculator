package com.dikaros.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.Stack;

public class MainActivity extends Activity {

    ViewGroup group;
    TextView tvMain;
    boolean calculated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_normal);
        group = (ViewGroup) findViewById(R.id.grid);
        tvMain = (TextView) findViewById(R.id.tv_main);
        tvMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //动态改变数字区域文字大小
                if (tvMain.getLineHeight()*tvMain.getLineCount()>=tvMain.getHeight())
                    tvMain.setTextSize(tvMain.getTextSize()/2);
            }
        });
        //获取group中所有的按钮
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof Button) {
                final Button b = (Button) v;
                switch (b.getText().toString()) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    case "0":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleDigitalButton(b);
                            }
                        });

                        break;
                    case "+":
                    case "-":
                    case "×":
                    case "÷":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleSignalButton(b);
                            }
                        });
                        break;
                    case "AC":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tvMain.setText("");
                            }
                        });

                        break;
                    case "=":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                calculate();
                            }
                        });
                        break;
                    case "DEL":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String t = tvMain.getText().toString();
                                tvMain.setText(t.length() > 0 ? t.substring(0, t.length() - 1) : "");
                            }
                        });

                        break;
                    case  ".":
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleSignalButton(b);
                            }
                        });

                        break;

                    default:
                        break;
                }
            }

        }
    }


    /**
     * 处理数字
     * @param b
     */
    public void handleDigitalButton(Button b) {
        if (calculated){
            tvMain.setText(b.getText().toString());
            calculated =false;
        }else {
            tvMain.append(b.getText().toString());
        }
    }

    /**
     * 处理符号
     * @param b
     */
    public void handleSignalButton(Button b) {
        String t =tvMain.getText().toString();
        if (t.length()== 0){
            return;
        }
        if (t.endsWith(".")){
            return;
        }
        if (t.endsWith("+")||t.endsWith("-")||t.endsWith("×")||t.endsWith("÷")){
            tvMain.setText(t.substring(0,t.length()-1)+b.getText().toString());
            return;
        }
        tvMain.append(b.getText().toString());
    }

    /**
     * 处理AC +/- %
     *
     * @param b
     */
    public void handleFunctionButton(Button b) {

    }

    /**
     * 计算
     */
    public void calculate() {


        if (tvMain.getText().toString().length()<1){
            return;
        }
        float result = 0f;
        try {
            result = caculate(tvMain.getText().toString() + "#");
            calculated = true;
        }catch (Exception e){
            tvMain.setText("wrong");
            calculated = true;
        }
        tvMain.setText(result+"");
    }


    private Stack<String> priStack = new Stack<>();// 操作符栈
    private Stack<Float> numStack = new Stack<>();// 操作数栈

    /**
     * 传入需要解析的字符串，返回计算结果(此处因为时间问题，省略合法性验证)
     * @param str 需要进行技术的表达式
     * @return 计算结果
     */
    public float caculate(String str) {
        // 1.判断string当中有没有非法字符
        String temp;// 用来临时存放读取的字符
        // 2.循环开始解析字符串，当字符串解析完，且符号栈为空时，则计算完成
        StringBuffer tempNum = new StringBuffer();// 用来临时存放数字字符串(当为多位数时)
        StringBuffer string = new StringBuffer().append(str);// 用来保存，提高效率

        while (string.length() != 0) {
            temp = string.substring(0, 1);
            string.delete(0, 1);
            // 判断temp，当temp为操作符时
            if (!isNum(temp)) {
                // 1.此时的tempNum内即为需要操作的数，取出数，压栈，并且清空tempNum
                if (!"".equals(tempNum.toString())) {
                    // 当表达式的第一个符号为括号
                    float num = Float.parseFloat(tempNum.toString());
                    numStack.push(num);
                    tempNum.delete(0, tempNum.length());
                }
                // 用当前取得的运算符与栈顶运算符比较优先级：若高于，则因为会先运算，放入栈顶；若等于，因为出现在后面，所以会后计算，所以栈顶元素出栈，取出操作数运算；
                // 若小于，则同理，取出栈顶元素运算，将结果入操作数栈。

                // 判断当前运算符与栈顶元素优先级，取出元素，进行计算(因为优先级可能小于栈顶元素，还小于第二个元素等等，需要用循环判断)
                while (!compare(temp.substring(0,1)) && (!priStack.empty())) {
                    float a =  numStack.pop();// 第二个运算数
                    float b =  numStack.pop();// 第一个运算数
                    String ope = priStack.pop();
                    float result = 0;// 运算结果
                    switch (ope) {
                        // 如果是加号或者减号，则
                        case "+":
                            result = b + a;
                            // 将操作结果放入操作数栈
                            numStack.push(result);
                            break;
                        case "-":
                            result = b - a;
                            // 将操作结果放入操作数栈
                            numStack.push(result);
                            break;
                        case "×":
                            result = b * a;
                            // 将操作结果放入操作数栈
                            numStack.push(result);
                            break;
                        case "÷":
                            result = b / a;// 将操作结果放入操作数栈
                            numStack.push(result);
                            break;
                    }

                }
                // 判断当前运算符与栈顶元素优先级， 如果高，或者低于平，计算完后，将当前操作符号，放入操作符栈
                if (!temp.substring(0,1).equals("#")) {
                    priStack.push(temp.substring(0, 1));
                    if (temp.substring(0,1).equals(")")) {// 当栈顶为"("，而当前元素为")"时，则是括号内以算完，去掉括号
                        priStack.pop();
                        priStack.pop();
                    }
                }
            } else
                // 当为非操作符时（数字）
                tempNum = tempNum.append(temp);// 将读到的这一位数接到以读出的数后(当不是个位数的时候)
        }
        return numStack.pop();
    }

    /**
     * 判断传入的字符是不是0-9的数字
     *
     * @param temp
     *            传入的字符串
     * @return
     */
    private boolean isNum(String temp) {
        return temp.matches("[0-9.]");
    }

    /**
     * 比较当前操作符与栈顶元素操作符优先级，如果比栈顶元素优先级高，则返回true，否则返回false
     *
     * @param str 需要进行比较的字符
     * @return 比较结果 true代表比栈顶元素优先级高，false代表比栈顶元素优先级低
     */
    private boolean compare(String str) {
        if (priStack.empty()) {
            // 当为空时，显然 当前优先级最低，返回高
            return true;
        }
        String last =  priStack.lastElement();
        // 如果栈顶为'('显然，优先级最低，')'不可能为栈顶。
        if (last == "(") {
            return true;
        }
        switch (str) {
            case "#":
                return false;// 结束符
            case "(":
                // "("优先级最高,显然返回true
                return true;
            case ")":
                // ")"优先级最低，
                return false;
            case "×":
            case "÷":
                // "*/"优先级只比"+-"高
                if (last .equals("+")|| last.equals("-"))
                    return true;
                else
                    return false;
                // "+-"为最低，一直返回false
            case "+":
                return false;
            case "-":
                return false;
        }
        return true;
    }

}
