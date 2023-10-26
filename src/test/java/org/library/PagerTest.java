package org.library;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.library.view.Pager;

public class PagerTest {
    @Test
    public void testPager() throws Exception {
        List<Integer> list = new ArrayList();
        for (int i = 0; i < 50; i++) {
            list.add(i);
        }

        Pager<Integer> pager = new Pager<Integer>(list, null, null);
        Assert.assertEquals(1, pager.getCurrPage());
        Assert.assertEquals(1, pager.getPrevPage());
        Assert.assertEquals(2, pager.getNextPage());
        Assert.assertEquals(20, pager.getPageItems().size());
        Assert.assertEquals(50, pager.getAllItems().size());

        pager = new Pager<Integer>(list, 2, null);
        Assert.assertEquals(2, pager.getCurrPage());
        Assert.assertEquals(1, pager.getPrevPage());
        Assert.assertEquals(3, pager.getNextPage());
        Assert.assertTrue(20 == pager.getPageItems().get(0));

        pager = new Pager<Integer>(list, 3, null);
        Assert.assertEquals(3, pager.getCurrPage());
        Assert.assertEquals(2, pager.getPrevPage());
        Assert.assertEquals(3, pager.getNextPage());
        Assert.assertEquals(10, pager.getPageItems().size());
        Assert.assertEquals(50, pager.getAllItems().size());

    }
}
