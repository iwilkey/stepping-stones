import sqlite3 as sql
from os import path
from werkzeug.security import check_password_hash, generate_password_hash
import string
import random
import re

ROOT = path.dirname(path.relpath(__file__))

def check_login(username, password):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM accounts')
	accounts = cur.fetchall()
	connection.close()

	for account in accounts:
		if account[1] == username:
			passHash = account[2]
			if check_password_hash(passHash, password):
				return True
	else:
		return False

def check_user_exists(username, email):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM accounts')
	accounts = cur.fetchall()
	connection.close()

	for account in accounts:
		if account[1] == username:
			return True
		if account[5] == email:
			return True

	return False

def create_user(email, username, password, question, answer, activation):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()

	passHash = generate_password_hash(password)
	answerHash = generate_password_hash(answer)

	cur.execute('INSERT INTO accounts (email, username, passwordHash, question, answerHash, activated, actID) VALUES(?, ?, ?, ?, ?, ?, ?)', (email, username, passHash, question, answerHash, activation, password_generator(16)))
	connection.commit()
	connection.close()

def get_user(username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	user = cur.execute('SELECT * FROM accounts WHERE username = ?', (username,)).fetchone()
	connection.close()
	return user

def create_post(username, post):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('INSERT INTO threads (author, content, likes, dislikes, threadweb) VALUES(?, ?, ?, ?, ?)', (username, post, 0, 0, 0))
	connection.commit()
	connection.close()

def return_all_posts(username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM threads WHERE author = ?', (username,))
	posts = cur.fetchall()
	connection.close()
	return posts

def delete_post(username, post):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('DELETE FROM threads WHERE author = ? AND content = ?', (username, post))
	connection.commit()
	connection.close()

def update_password(password, username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	passHash = generate_password_hash(password)
	cur.execute('UPDATE accounts SET passwordHash = ? WHERE username = ?', (passHash, username))
	connection.commit()
	connection.close()

def update_email(email, username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('UPDATE accounts SET email = ? WHERE username = ?', (email, username))
	connection.commit()
	connection.close()

def activate_user(username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('UPDATE accounts SET activated = ? WHERE username = ?', (True, username))
	connection.commit()
	connection.close()

def email_sync(username, email):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM accounts WHERE username = ?', (username,))
	account = cur.fetchone()

	if account[5] == email:
		return True
	else:
		return False

def return_random_thread(username):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()

	cur.execute('SELECT * FROM votes WHERE user = ?', (username,))
	user_votes = cur.fetchall()

	cur.execute('SELECT * FROM threads WHERE author != ? ORDER BY RANDOM()', (username,))
	all_threads = cur.fetchall()
	connection.close()

	if len(all_threads) == 0: #If there are more than one thread in the database.
		return None

	if len(user_votes) == 0: #If the user hasn't voted on a thread before...
		return all_threads[0] #This works because the list returned from the database was already randomized.

	for thread in all_threads:
		thread_id = thread[0]
		is_vote = any(vote[2] == thread_id for vote in user_votes)
		if not is_vote:
			return thread

	return None

def return_thread(id):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM threads WHERE id = ?', (id,))
	thread = cur.fetchone()
	connection.close()

	return thread

def thread_vote(username, threadID, verdict):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('INSERT INTO votes (user, threadID, type) VALUES(?, ?, ?)', (username, threadID, verdict))
	if verdict == "yes":
		cur.execute('UPDATE threads SET likes = likes + 1 WHERE id = ?', (threadID,))
	elif verdict == "no":
		cur.execute('UPDATE threads SET dislikes = dislikes + 1 WHERE id = ?', (threadID,))
	connection.commit()
	connection.close()

def return_all_threadweb_threads():
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('SELECT * FROM threads WHERE threadweb = ?', (1,))
	threadweb = cur.fetchall()
	connection.close()

	if len(threadweb) == 0:
		return None

	return threadweb

def add_threadweb(tid):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('UPDATE threads SET threadweb = 1 WHERE id = ?', (tid,))
	connection.commit()
	connection.close()

def delete_threadweb(tid):
	connection = sql.connect(path.join(ROOT, 'threadfeed.db'))
	cur = connection.cursor()
	cur.execute('UPDATE threads SET threadweb = 0 WHERE id = ?', (tid,))
	connection.commit()
	connection.close()

def threadweb_algorithm():
	#TODO: This function should check the status of all the likes and dislikes on a thread and change it's
	#threadweb status if it meets these expectations:
		#1) The thread must be within the week (using the timestamp)
		#2) The thread must have an overwhelmingly positive reaction.
			#a) This means that it must have been voted on by 75% of the current ammount of users on Threadfeed, but it must have atleast 100 votes.
			#b) 95% of the total votes must be postive
		#3) It can't already be on the threadweb

	connection = sql.connect(path.join(ROOT, 'threadfeed.db')) #Connect
	cur = connection.cursor()

	#Update, so remove all the ones already there.
	cur.execute('UPDATE threads SET threadweb = 0 WHERE threadweb = 1')
	connection.commit()

	cur.execute('SELECT * FROM threads WHERE threadweb = 0 AND created BETWEEN DATE() - 7 AND DATE()')
	allowed_threads = cur.fetchall()

	cur.execute('SELECT * FROM accounts')
	accounts = cur.fetchall()

	cur.execute('SELECT * FROM votes')
	votes = cur.fetchall()

	connection.close()

	final_picks = []

	for thread in allowed_threads:

		received_votes = 0
		positive_votes = 0
		for vote in votes:
			if vote[2] == thread[0]:
				received_votes += 1
				if vote[4] == "yes":
					positive_votes += 1

			#At this point we know how many people voted on this thread and how many were positive
		if received_votes >= (len(accounts) * 0.75):
			if positive_votes >= (received_votes * 0.95):
				final_picks.append(thread)

	if len(final_picks) > 0:
		for pick in final_picks:
			add_threadweb(pick[0])

	print("No Threadweb finalists this time.")

#Helper methods

def password_generator(stringLength=16):
	letters = string.ascii_lowercase
	return ''.join(random.choice(letters) for i in range(stringLength))

def check_email(email):
	regex = '^[a-z0-9]+[\._]?[a-z0-9]+[@]\w+[.]\w{2,3}$'

	if(re.search(regex, email)):
		return True
	else:
		return False
