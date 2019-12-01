package com.example.leeseungchan.chulbalhama.Service;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

public class NonLinearRegression  { // serialize하여 객체 인텐트 전송가능하게
    private double a;
    private double b;
    private double c;
    ParametricUnivariateFunction model;

    public NonLinearRegression(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;

        model = new ParametricUnivariateFunction() {
            @Override
            public double value(double x, double... parameters) {
                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                return a - b * Math.exp(-c * x);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                return new double[]{
                        1,
                        -Math.exp(-c * x),
                        b * c * Math.exp(-c * x)
                };
            }
        };
    }

    public NonLinearRegression() {
        this(40, 40, 0.1);
    }
    public void resetParameters() {
        this.a = 1.0;
        this.b = 2.0;
        this.c = 3.0;
    }
    public void resetParameters(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double[] getParamenters(){
        return new double[]{a,b,c};
    }

    public double estimate(int day) {
        return a - b * Math.exp(-c * day);
    }
    public double estimate(double x) {
        return a - b * Math.exp(-c * x);
    }
    public ArrayList<Double> estimate(ArrayList<Integer> days) {
        ArrayList<Double> results = new ArrayList<Double>();
        for (int day : days)
            results.add(estimate(day));
        return results;
    }
    public int formationDateEstimate()
    {
        int date =0;
        double score=0.0;
        do{
            date++;
            score=estimate(date);
        }while(date<300 && score < 21);
        Log.e("score","date : "+date);
        return date;
    }
    public void optimize(ArrayList <Integer> day, ArrayList<Double> score) {
        double[] initialGuess = new double[]{a, b, c};
        //initialize the optimizer and curve fitter.
        LevenbergMarquardtOptimizer optim = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optim);
        for(int i =0; i<day.size();i++)
            fitter.addObservedPoint(day.get(i), score.get(i));
        double result[] = fitter.fit(model, initialGuess);
        a =result[0];
        b =result[1];
        c =result[2];
    }

}
