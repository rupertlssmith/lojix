:- use_module(library(jpl)).


:- object(jpc_driver).

	:- info([
		version is 1.0,
		author is 'Sergio Castro',
		date is 2014/04/13,
		comment is 'Prolog side of the JPL based driver.',
		parnames is []
	]).

	:- use_module(jpl, [
		jpl_get/3, jpl_set/3,
		jpl_new/3,
		jpl_call/4
	]).

/*
	process_result(_, JavaResult) :- var(JavaResult).
	process_result(_, JavaResult) :- \+ var(JavaResult), JavaResult = error(Error), throw(Error).
	process_result(Result, JavaResult) :- \+ var(JavaResult), JavaResult = result(Returned), set_result(Result, Returned).
	

	set_result(term(JavaResult), JavaResult).
	set_result(serialized(JavaResult), JavaResult).
	set_result(jref(JavaResult), JavaResult).
	set_result(strong(jref(JavaResult)), JavaResult).
	set_result(weak(jref(JavaResult)), JavaResult).
	set_result(soft(jref(JavaResult)), JavaResult).
	set_result(strong(jref_term(JavaResult)), JavaResult).
	set_result(weak(jref_term(JavaResult)), JavaResult).
	set_result(soft(jref_term(JavaResult)), JavaResult).
*/
	
	process_result(_, error(Error)) :- throw(Error).
	process_result(Eval, Eval).
		
	
	:- public(eval/2).
	eval(Exp, Output) :-
		Eval = eval(Exp, Output),
		DriverClass = class([org,jpc,engine,jpl],['JplDriver']),
		%logtalk::print_message(comment, jpc, calling(eval(DriverClass, Eval, Output))),
		jpl_call(DriverClass, 'evalAsTerm', [{Eval}], {JavaResult}),
		process_result(Eval, JavaResult).

		
	
/*
	:- multifile(logtalk::message_prefix_stream/4).
	:- dynamic(logtalk::message_prefix_stream/4).

	% Quintus Prolog based prefixes (also used in SICStus Prolog):
	logtalk::message_prefix_stream(comment, jpc, 'INFO ', user_output).
	logtalk::message_prefix_stream(warning, jpc, 'WARNING ', user_output).
	logtalk::message_prefix_stream(error, jpc,   'ERROR ', user_output).

	:- multifile(logtalk::message_tokens//2).
	:- dynamic(logtalk::message_tokens//2).

	% messages for tests handling

	logtalk::message_tokens(calling(Goal), jpc) -->
		[nl, '    CALLING: ~q'-[Goal], nl].
*/

:- end_object.


