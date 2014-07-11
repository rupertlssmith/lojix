package org.jpc.salt.jpl;

import org.jpc.salt.TermBuilder;
import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermWriter;

public class JplTermWriter extends TermWriter<jpl.Term> {

	@Override
	public TermContentHandler startIntegerTerm(long value) {
		process(new jpl.Integer(value));
		return this;
	}

	@Override
	public TermContentHandler startFloatTerm(double value) {
		process(new jpl.Float(value));
		return this;
	}

	@Override
	public TermContentHandler startVariable(String name) {
		process(new jpl.Variable(name));
		return this;
	}
	
	@Override
	public TermContentHandler startAtom(String name) {
		process(new jpl.Atom(name));
		return this;
	}

	@Override
	public TermContentHandler startJRef(Object ref) {
		throw new UnsupportedOperationException();
	}

	protected TermBuilder<jpl.Term> createCompoundBuilder() {
		return new LojixTermBuilder();
	}
	
}
