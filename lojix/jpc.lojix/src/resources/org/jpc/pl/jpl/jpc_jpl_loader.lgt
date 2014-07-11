:- use_module(library(jpl)).


:- object(jpc_jpl).

	:- public(setup/0).
	:- mode(setup, one).
	:- info(setup/0, [
		comment is 'Configures JPC by means of JPL from the Prolog side.',
		argnames is []
	]).
	
	setup :- 
		prolog_flag(dialect, Dialect),
		DriverClass = class([org,jpc,engine,jpl],['JplDriver']),
		jpl_call(DriverClass, 'setupFromProlog', [Dialect], _).
	
	:- use_module(jpl, [
		jpl_call/4
	]).

	:- uses(user, [
		prolog_flag/2
	]).
	
:- end_object.



:- initialization((
	jpc_jpl::setup
)).
