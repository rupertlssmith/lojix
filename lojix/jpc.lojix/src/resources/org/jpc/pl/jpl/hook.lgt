/*
:- object(hook,
	implements(expanding)).

	term_expansion('>>>'(Functor/Arity, java), [Clause]) :-
		functor(Goal, Functor, Arity),
		Clause = (
			Goal :-
				jpl:jpl_get('java.lang.Math', 'PI', Value),
				arg(1, Goal, Value)
		).

	%goal_expansion(print_it_or_else(Arg), (write(Arg), nl)).

:- end_object.
*/