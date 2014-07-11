package org.jpc.jpl;

import org.jpc.engine.logtalk.LogtalkEngineTestSuite;
import org.jpc.examples.LogtalkExamplesTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The Logtalk specific tests the engine should pass if Logtalk is available
 * @author sergioc
 *
 */
@RunWith(Suite.class)
@SuiteClasses({LogtalkEngineTestSuite.class, LogtalkExamplesTestSuite.class})
public abstract class JplLogtalkEngineTestSuite {}
