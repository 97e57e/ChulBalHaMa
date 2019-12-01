package com.example.leeseungchan.chulbalhama.Service;

import android.util.Log;

import java.io.Serializable;

public class RegressionVO implements Serializable {

    public double a;
    public double b;
    public double c;

    public void setParameters(double a, double b, double c)
    {
        this.a=a;
        this.b=b;
        this.c=c;
    }

    public double estimate(int day) {
        return a - b * Math.exp(-c * day);
    }
    public double estimate(double x) {
        return a - b * Math.exp(-c * x);
    }

    public int formationDateEstimate()
    {
        int date =0;
        double score=0.0;
        do{
            date++;
            score=estimate(date);
        }while(date<300 && date<21);
        return date;
    }


    int habitID=0;

    public double getHabitDueDate(int a)
    {
        habitFormationMeasure(1);
        return 0.0;
    }

    public double habitFormationMeasure(int goal)
    {
        double estDate=formationDateEstimate();
        double wantDate = getHabitDueDate(habitID);

        return estDate/wantDate;

    }


    public String encourageMessage(int goal)
    {
        double estDate=formationDateEstimate();
        double wantDate = getHabitDueDate(habitID);
        if(estDate>wantDate)
        {//목표 보다 일찍 완성될 것으로 예상
            return "지금 처럼만 하신다면, 목표 기간 내에 습관을 형성하실 수 있을겁니다. 잘하고 계십니다!";
        }
        else
        {// 목표 보다 늦게 완성될 것으로 예상
            if(estDate/wantDate > 2)
            {
                return "목표 달성까지 많은 노력이 필요합니다. 습관의 난이도를 낮추거나 부단한 노력이 필요해보입니다.";
            }
            else if(estDate/wantDate>1.5)
            {
                return "목표 달성에 근접하고 있습니다. 조금 더 노력하세요!";
            }
            else
            {
                return "조금만 더 노력하시면, 목표하신 기간내에 습관을 형성하실 수 있습니다!";
            }
        }

    }


}
