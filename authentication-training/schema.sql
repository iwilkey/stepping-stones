drop table if exists accounts;
	create table accounts (
		id integer primary key autoincrement,
		email text not null,
		username text not null,
		password text not null
	);