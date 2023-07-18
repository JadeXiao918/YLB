package com.ylb;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class MyTest {
    @Test
    public void test01(){
        Date cur = new Date();
        System.out.println("cur=" + cur);
        Date truncate = DateUtils.truncate(DateUtils.addDays(cur,-1), Calendar.DATE);
        System.out.println("truncate=" + truncate);
        Date end = DateUtils.truncate(cur, Calendar.DATE);
        System.out.println("end = " + end);
    }
}
