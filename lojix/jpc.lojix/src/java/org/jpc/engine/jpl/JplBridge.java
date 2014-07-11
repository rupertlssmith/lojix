package org.jpc.engine.jpl;


import org.jpc.salt.JpcTermWriter;
import org.jpc.salt.jpl.JplTermReader;
import org.jpc.salt.jpl.JplTermWriter;
import org.jpc.term.Term;

/**
 * A utility class for transforming Jpc terms to Jpl and vice-versa
 * @author sergioc
 *
 */
public class JplBridge {

	public static jpl.Term fromJpcToJpl(Term term) {
		JplTermWriter jplTermWriter = new JplTermWriter();
		term.read(jplTermWriter);
		return jplTermWriter.getFirst();
	} 
	
	public static Term fromJplToJpc(jpl.Term term) {
		JpcTermWriter jpcTermWriter = new JpcTermWriter();
		new JplTermReader(term, jpcTermWriter).read();
		return jpcTermWriter.getFirst();
	} 

}
