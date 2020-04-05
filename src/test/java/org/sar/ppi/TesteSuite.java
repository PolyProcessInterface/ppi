package org.sar.ppi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(value=Suite.class)
@Suite.SuiteClasses(value={
        org.sar.ppi.RingMpiTest.class,
        //org.sar.ppi.NodeProcessTest.class,
        org.sar.ppi.AnnotatedProcessTest.class,
})


public class TesteSuite {
}
