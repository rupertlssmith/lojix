package org.jpc.salt.jpl;


import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermReader;

/**
 * a SALT jpl term reader
 * @author sergioc
 *
 */
public class JplTermReader extends TermReader {

	private jpl.Term jplTerm;
	
	public JplTermReader(jpl.Term jplTerm, TermContentHandler contentHandler) {
		super(contentHandler);
		this.jplTerm = jplTerm;
	}

	@Override
	public void read() {
		read(jplTerm);
	}
	
	private void read(jpl.Term term) {
		if(term.isInteger()) {
			jpl.Integer jplInteger = (jpl.Integer) term;
			getContentHandler().startIntegerTerm(jplInteger.longValue());
		} else 	if(term.isFloat()) {
			jpl.Float jplFloat = (jpl.Float) term;
			getContentHandler().startFloatTerm(jplFloat.doubleValue());
		} else if (term.isVariable()) {
			getContentHandler().startVariable(term.name());
		} else if (term.isAtom()) {
			getContentHandler().startAtom(term.name());
		} else if(term.isCompound()) {
			getContentHandler().startCompound();
			getContentHandler().startAtom(term.name());
			for(jpl.Term child : term.args()) {
				read(child);
			}
			getContentHandler().endCompound();
		} else
			throw new RuntimeException("Unrecognized JPL term: " + term);
	}
	
}
