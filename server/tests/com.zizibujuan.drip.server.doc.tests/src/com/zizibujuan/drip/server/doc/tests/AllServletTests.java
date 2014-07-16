package com.zizibujuan.drip.server.doc.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.zizibujuan.drip.server.doc.tests.servlets.FileServletTests;
import com.zizibujuan.drip.server.doc.tests.servlets.ProjectServletTests;

/**
 * Servlet测试套件
 * @author jzw
 * @since 0.0.1
 */
@RunWith(Suite.class)
@SuiteClasses({
	ProjectServletTests.class,
	FileServletTests.class
})
public class AllServletTests {

}
