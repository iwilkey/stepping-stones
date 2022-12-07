import sqlite3 as sql
from os import path

ROOT = path.dirname(path.relpath((__file__)))

def check_login(username, password):
	con = sql.connect(path.join(ROOT, 'accounts.db'))
	cur = con.cursor()
	cur.execute('select * from accounts')
	accounts = cur.fetchall()

	"""
	accounts[0] = id
	accounts[1] = email
	accounts[2] = username
	accounts[3] = password

	"""
	for account in accounts:
		if account[2] == username and account[3] == password:
			return True

	return False

def create_account(email, username, password):
	con = sql.connect(path.join(ROOT, 'accounts.db'))
	cur = con.cursor()
	cur.execute('insert into accounts (email, username, password) values(?, ?, ?)', (email, username, password))
	con.commit()
	con.close()