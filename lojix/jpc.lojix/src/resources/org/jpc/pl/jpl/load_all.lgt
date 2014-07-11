
:- initialization((
	%set_logtalk_flag(report, off),
	logtalk_load([jpc_driver, jpc_core(load_driver_required), prolog_engines])
)).