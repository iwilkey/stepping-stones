drop table if exists accounts;
	create table accounts (
		id integer primary key autoincrement,
		username text not null,
		passwordHash text not null,
		question text not null,
		answerHash text not null,
		email text not null,
		activated boolean not null,
		actID text not null
	);

drop table if exists threads;
	create table threads (
		id integer primary key autoincrement,
		author text not null,
		content text not null,
		created timestamp not null default current_timestamp,
		likes integer not null,
		dislikes integer not null,
		threadweb integer not null
	);

drop table if exists votes;
	create table votes (
		id integer primary key autoincrement,
		user text not null,
		threadID integer not null,
		created timestamp not null default current_timestamp,
		type text not null
	);
