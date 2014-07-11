:- object(prolog_engines).

	:- public(this_engine/1).
	:- mode(this_engine(?nonvar), one).
	:- info(this_engine/1, [
		comment is 'Answers a representation of this Prolog engine in the Java side.',
		argnames is ['PrologEngine']
	]).
	
	this_engine(PrologEngine) :-
		class([org,jpc,engine,jpl],['JplEngine'])::getPrologEngine return weak(jref(PrologEngine)).

:- end_object.
