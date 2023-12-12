package com.my.slide.window.slidewindow;

import com.my.slide.window.slidewindow.flow.RequestLimit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SlideWindowApplicationTests {

    @Autowired
    private RequestLimit requestLimit;

    @Test
    public void contextLoads() {
        for (int i = 0; i < 20; i++) {
            System.out.println(requestLimit.countAndCheckRequestRate(123L, "test"));
        }
    }

}
