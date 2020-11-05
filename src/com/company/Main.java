package com.company;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private boolean appendFlag = true;
    private boolean errorFlag = false;
    private boolean nullFlag = false;

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseExpressions(String expression){
        List<String> components = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            String ch = String.valueOf(expression.charAt(i));
            if (ch.equals(" ")) continue;
            else if(ch.matches("[0-9]+")){
                if (!components.isEmpty()){
                    if (components.get(components.size()-1).matches("[0-9]+")){
                        components.set(components.size()-1, components.get(components.size()-1) + ch);
                    }else{
                        components.add(ch);
                    }
                }else{
                    components.add(ch);
                }
            }
            else if (ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/")  || ch.equals("(") || ch.equals(")")){
                components.add(ch);
            }else{
                errorFlag = true;
            }
        }
        return components;
    }

    private void run() throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
        List<String> expressions = reader.lines().collect(Collectors.toList());
            for (String e : expressions) {
                //List<String> operatorsAndOperands = Arrays.asList(e.split(" "));
                List<String> operatorsAndOperands = parseExpressions(e);
                Deque<String> operationStack = new ArrayDeque<>();
                Deque<String> outputStack = new ArrayDeque<>();
                for (String x : operatorsAndOperands) {
                    distributeToStacks(x, operationStack, outputStack);
                }
                if (!errorFlag){
                    int size = operationStack.size();
                    for (int i = 0; i < size; i++) {
                        outputStack.add(operationStack.removeLast());
                    }
                    if (outputStack.size() > 0){
                        List<String> resultList = new ArrayList<>(outputStack);
                        System.out.println(resultList);
                        changeOperators(resultList);
                        double res = count(resultList);
                        if (appendFlag){
                            result.append(res).append("\n");
                        }else if(nullFlag){
                            result.append("Деление на ноль").append("\n");
                            nullFlag = false;
                        }
                        else {
                            result.append("Вырвжение введено неверно").append("\n");
                        }
                        errorFlag = false;
                    }
                }else {
                    result.append("Вырвжение введено неверно").append("\n");
                    errorFlag = false;
                }
            }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));){
            writer.append(result);
        }
    }

    private void distributeToStacks(String x, Deque<String> operationStack, Deque<String> outputStack){
        if ((x.equals("*") | x.equals("/") | x.equals(")")) & outputStack.isEmpty()){
            errorFlag = true;
        }else {
            if(x.equals("*") || x.equals("/")){
                operationStack.add(x);
            }else if(x.equals("(")){
                operationStack.add(x);
            } else if(x.equals(")")){
                if(!operationStack.contains("(")) errorFlag = true;
                operationStack.remove("(");
                int size = operationStack.size();
                for (int i = 0; i < size; i++) {
                    outputStack.add(operationStack.removeLast());
                }
                outputStack.addAll(operationStack);
            }else if(x.equals("+") || x.equals("-")){
                if (!operationStack.isEmpty()){
                    String operator = operationStack.getLast();
                    if (operator.equals("*") || operator.equals("/") || operator.equals("+") || operator.equals("-") || operator.equals("u-")){
                        outputStack.add(operationStack.removeLast());
                        operationStack.add(x);
                    }
                    if (operator.equals("(") & x.equals("-")){
                        operationStack.add("u-");
                    }
                }else if(x.equals("-") & outputStack.size() == 0){
                    operationStack.add("u-");
                }else{
                    operationStack.add(x);
                }
            }else if (x.matches("[0-9]+")){
                outputStack.add(x);
            }else {
                errorFlag = true;
            }
        }
    }

    private void changeOperators(List<String> resultList){
        for (int i = 0; i < resultList.size()-1; i++) {
            if(resultList.get(i).equals("-")){
                if (resultList.get(i).equals(resultList.get(i+1))){
                    resultList.set(i, "+");
                    resultList.remove(i+1);
                }
            }
        }
    }

    private Double count(List<String> expression){
        try {
            Deque<Double> result = new ArrayDeque<>();
            for (String x : expression) {
                switch (x) {
                    case "+":
                        result.push(result.pop() + result.pop());
                        break;
                    case "-": {
                        Double b = result.pop(), a = result.pop();
                        result.push(a - b);
                        break;
                    }
                    case "*":
                        result.push(result.pop() * result.pop());
                        break;
                    case "/": {
                        Double b = result.pop(), a = result.pop();
                        if (b == 0) {
                            System.err.println("Деление на ноль");
                            nullFlag = true;
                            appendFlag = false;
                            return -1.0;
                        }
                        result.push(a / b);
                        break;
                    }
                    case "u-": {
                        result.push(-result.pop());
                        break;
                    }
                    default:
                        result.push(Double.valueOf(x));
                        break;
                }
            }
            appendFlag = true;
            return result.pop();
        }catch (Exception ex){
            System.err.println("Вырвжение введено неверно");
        }
        appendFlag = false;
        return -1.0;
    }
}
