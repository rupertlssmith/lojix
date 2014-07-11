package org.jpc.engine.jpl;

import static java.util.Arrays.asList;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.BB_REF_TERM_FLAG;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.RETURN_SERIALIZED_SPECIFIER;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.RETURN_TERM_SPECIFIER;
import static org.jpc.engine.prolog.ThreadModel.MULTI_THREADED;
import static org.jpc.engine.prolog.ThreadModel.SINGLE_THREADED;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import jpl.JPL;
import jpl.fli.Prolog;

import org.jpc.Jpc;
import org.jpc.JpcException;
import org.jpc.converter.catalog.refterm.TermToRefTermTypeConverter;
import org.jpc.converter.catalog.serialized.ToSerializedConverter;
import org.jpc.engine.listener.DriverStateListener;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.engine.prolog.PrologEngineInitializationException;
import org.jpc.engine.prolog.ReturnSpecifierConstants;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.engine.prolog.driver.UniquePrologEngineDriver;
import org.jpc.term.AbstractVar;
import org.jpc.term.Atom;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.jpc.term.Var;
import org.jpc.term.refterm.RefTermManager;
import org.jpc.term.refterm.RefTermType;
import org.jpc.util.JpcPreferences;
import org.jpc.util.engine.supported.EngineDescription;
import org.jpc.util.engine.supported.Swi;
import org.jpc.util.engine.supported.Yap;
import org.minitoolbox.collections.CollectionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JplDriver extends UniquePrologEngineDriver<JplEngine> {

	private static final Logger logger = LoggerFactory.getLogger(JplDriver.class);
	public static final String JPL_LIBRARY_NAME = "JPL";
	//public static final String JPLPATH_ENV_VAR = "JPLPATH"; //environment variable with the path to the JPL library. This will determine if the prolog engine is SWI or YAP

	private static final Collection<DriverStateListener> stateListeners = CollectionsUtil.createWeakSet();
	
	private String jplPath;
	private final String jplPathPropertyVar;

	public JplDriver(EngineDescription engineDescription, String jplPathPropertyVar, JpcPreferences preferences) {
		super(engineDescription, preferences);
		this.jplPathPropertyVar = jplPathPropertyVar;
	}
	
	public String getJplPath() {
		return jplPath;
	}

	public void setJplPath(String jplPath) {
		this.jplPath = jplPath;
	}

	@Override
	protected boolean isInstanceRunning() {
		return JplEngine.prologEngine != null;
	}
	
	@Override
	public void readyOrThrow() {
		JPL.setDTMMode(false); //so all the variables (including the ones starting with '_') will be returned. Otherwise those variables will be excluded from the result. The anonymous variable '_' is never returned.
		if(jplPath == null) {
			jplPath = getDefaultJplPath();
		}
		if(jplPath != null) {
			if(!new File(jplPath).exists())
				throw new PrologEngineInitializationException("The JPL library directory does not exist: " + jplPath);
			JPL.setNativeLibraryDir(jplPath); //configuring the JPL path according to an environment variable.
		} else {
			//do nothing, it may be that the JVM argument java.library.path includes the path to the JPL library
			logger.warn("The directory of the native JPL library has not been configured.");
			logger.warn("If the 'java.library.path' property is set JPL will try to find the native library from there. Otherwise from the default OS search paths."); 
		}
	}

	public String getDefaultJplPath() {
		return getPreferences().getVar(getJplPathPropertyVar());
	}
	
	public String getJplPathPropertyVar() {
		return jplPathPropertyVar;
	}

	/**
	 * 
	 * @return a JPL Prolog engine with multi-threading support.
	 */
	public JplEngine createMTPrologEngine() {
		return createPrologEngine(new PrologEngineFactory<JplEngine>() {
			@Override
			public JplEngine createPrologEngine() {
				return new JplEngine(MULTI_THREADED);
			}
		});
	}
	
	@Override
	protected synchronized JplEngine createPrologEngine(PrologEngineFactory<JplEngine> basicFactory) {
		JplEngine.prologEngine = super.createPrologEngine(basicFactory);
		return JplEngine.prologEngine;
	}
	
	@Override
	protected PrologEngineFactory<JplEngine> defaultBasicFactory() {
		return new PrologEngineFactory<JplEngine>() {
			@Override
			public JplEngine createPrologEngine() {
				return new JplEngine(SINGLE_THREADED);
			}
		};
	}
	
	@Override
	public String getLibraryName() {
		return JPL_LIBRARY_NAME;
	}
	
	@Override
	public String getLicenseUrl() {
		return "http://www.swi-prolog.org/packages/jpl/java_api/lgpl.html";
	}
	
	@Override
	public String getSiteUrl() {
		return "http://www.swi-prolog.org/packages/jpl/";
	}
	
	@Override
	protected Collection<DriverStateListener> getListeners() {
		return stateListeners;
	}
	
	
	public static void setupFromProlog(String dialect) {
		if(dialect.equals(Swi.SWI_DIALECT)) {
			//new JplSwiDriver().createPrologEngine().withLogtalk();
			PrologEngine pe = new JplSwiDriver().createPrologEngine();
			pe.loadJpcForLogtalk();
		} else if(dialect.equals(Yap.YAP_DIALECT)) {
			new JplYapDriver().createPrologEngine().withLogtalk();
		} else
			throw new JpcException("Unrecognized dialect: " + dialect + ".");
	}
	
	public static jpl.Term evalAsTerm(jpl.Term evalTermJpl) {
		Term unifiedEvalTerm;
		Term resultTerm;
		Jpc jpc = Jpc.getDefault();
		try {
			Compound evalTerm = (Compound) JplBridge.fromJplToJpc(evalTermJpl);
			evalTerm = (Compound) evalTerm.compile(true); //compile preserving variable names.
			Term expTerm = evalTerm.arg(1);
			Object result = jpc.fromTerm(expTerm); //the result of evaluating the term expression in the Java side.
			
			if(evalTerm.arg(2) instanceof Compound) {
				Compound returnSpecifierTerm = (Compound) evalTerm.arg(2);
				if(returnSpecifierTerm.getNameString().equals(BB_REF_TERM_FLAG)) { //the object in the Java side should be garbage collected only if the atom representing it is garbage collected in the Prolog side.
					resultTerm = jpc.refTerm(result); //find out if the result is already associated with a term representation.
					if(resultTerm == null) { //the result is not associated with a term representation.
						Compound jpcTmpTerm = jpc.newRefTerm(result); //obtaining a temporal (black box) term reference to the Java object resulting of evaluating the term expression. 
						jpl.Term jplRefTerm = (jpl.Term)new jpl.Query("jpl_call(class([org,jpc,engine,jpl],['JplDriver']), returnRef, [{" + jpcTmpTerm.toEscapedString() + "}], JplRef)").oneSolution().get("JplRef"); //get the JPL representation of the object.
						jpc.forgetRefTerm(jpcTmpTerm);
						Compound translatedJplRefTerm = (Compound) JplBridge.fromJplToJpc(jplRefTerm);
						resultTerm = new Compound(RefTermManager.JREF_TERM_FUNCTOR_NAME, asList(translatedJplRefTerm.arg(1))); //adapting the JPL term representation to the JPC format for Prolog side references.
						jpc.newWeakRefTerm(result, (Compound)resultTerm); //associating the term representation to the result object.
					} else { //the result is already associated with a term representation.
						String tag = ((Atom)resultTerm.arg(1)).getName();
						if (!Prolog.is_tag(tag))
							throw new JpcException("Attempt to use a Prolog side reference term with an object already associated with the term: " + resultTerm + ".");
					}
				} else if(returnSpecifierTerm.getNameString().equals(RETURN_TERM_SPECIFIER)) {
					resultTerm = jpc.toTerm(result);
				} else if(returnSpecifierTerm.getNameString().equals(RETURN_SERIALIZED_SPECIFIER)) {
					resultTerm = new ToSerializedConverter().toTerm((Serializable)result, Compound.class, jpc);
				} else {
					RefTermType refTermType = new TermToRefTermTypeConverter().fromTerm((Compound) returnSpecifierTerm, RefTermType.class, jpc);
					resultTerm = refTermType.toTerm(result, jpc);
				}
				
				Term termToUnify;
				if(ReturnSpecifierConstants.isReferenceModifierFlag(returnSpecifierTerm.getNameString())) {
					termToUnify = ((Compound)returnSpecifierTerm.arg(1)).arg(1);
				} else {
					termToUnify = returnSpecifierTerm.arg(1);
				}
				termToUnify.unify(resultTerm); //will either set the unbound return variable to the expression return value or will throw an exception if it is a constant not unifiable.
				
			} else if(evalTerm.arg(2) instanceof AbstractVar)
				resultTerm = Var.ANONYMOUS_VAR;
			else
				throw new JpcException("Wrong return specifier: " + evalTerm.arg(2));
			
			unifiedEvalTerm = evalTerm.resolveBindings();
			
			

		} catch(Exception e) {
			logJavaSideException(e);
			Term exceptionTerm = jpc.toTerm(e);
			unifiedEvalTerm = new Compound(JAVA_SIDE_EXCEPTION_SPECIFIER, asList(exceptionTerm));
		}
		return JplBridge.fromJpcToJpl(unifiedEvalTerm);
	}
	
	public static Object returnRef(jpl.Term jplTerm) {
		Compound refTerm = (Compound) JplBridge.fromJplToJpc(jplTerm);
		return Jpc.getDefault().resolveRefTerm(refTerm);
	}
	
	/*
	public static Object evalAsObject(jpl.Term evalTermJpl) {
		try {
			Term evalTerm = JplBridge.fromJplToJpc(evalTermJpl);
			Term targetTerm = evalTerm.arg(1);
			return Jpc.getDefault().fromTerm(targetTerm);
		} catch(Exception e) {
			logJavaSideException(e);
			throw e;
		}
	}
*/
	/*
	public static void newWeakJRefTerm(Object ref, jpl.Term jrefTermJpl) {
		try {
			Compound jrefTerm = (Compound) JplBridge.fromJplToJpc(jrefTermJpl);
			Jpc.getDefault().newWeakRefTerm(ref, jrefTerm);
		} catch(Exception e) {
			logJavaSideException(e);
			throw e;
		}
	}
*/
	
	//this adhoc method should be replaced by something better, maybe using slf4j.
	private static void logJavaSideException(Exception e) {
		e.printStackTrace();
		File tmpDir = new JpcPreferences().getJpcTmpDirectory();
		if(!tmpDir.exists())
			try {
				tmpDir.createNewFile();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");  
	    df.setTimeZone(TimeZone.getTimeZone("PST"));  
	    String timeStamp = df.format(new Date());  
		File logFile = new File(tmpDir, "log"+"_"+timeStamp);
		try {
			logFile.createNewFile();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		try(PrintStream ps = new PrintStream(logFile)) {
			e.printStackTrace(ps);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}
	}
	
}

