from flask import Flask, render_template, redirect, url_for, session, request, flash
from flask_cors import CORS
from requests import check_login, check_user_exists, create_user, get_user, create_post, return_all_posts, delete_post, password_generator, update_password, email_sync, update_email, check_email, activate_user, return_random_thread, thread_vote, return_thread, return_all_threadweb_threads, threadweb_algorithm, add_threadweb, delete_threadweb
from flask_mail import Mail, Message

app = Flask(__name__)
app.config.update(
	DEBUG=True,
	MAIL_SERVER="smtp.gmail.com",
	MAIL_PORT=465,
	MAIL_USE_SSL=True,
	MAIL_USERNAME = 'threadfeeddotcom@gmail.com',
	MAIL_PASSWORD = '**********'
	)
mail = Mail(app)
app.jinja_env.globals.update(return_all_posts=return_all_posts)
app.jinja_env.globals.update(len=len)
app.jinja_env.globals.update(get_user=get_user)
app.jinja_env.globals.update(return_thread=return_thread)
app.jinja_env.globals.update(return_all_threadweb_threads=return_all_threadweb_threads)
CORS(app)
app.config.from_mapping(SECRET_KEY = "149493049329439nfbnu39234nf3123")

#Auth routes
@app.route('/', methods=['GET'])
def home():

	if session.get('username') == None:
		return redirect(url_for('login'))

	return redirect(url_for('profile'))

@app.route('/login', methods=['GET', 'POST'])
def login():

	session.clear()

	if request.method == 'GET':
		pass

	if request.method == 'POST':
		username = request.form.get('username')
		password = request.form.get('password')

		if check_login(username, password):

			id = get_user(username)[0]
			name = get_user(username)[1]
			session['user_id'] = id
			session['username'] = name

			return redirect(url_for('profile'))

		else:
			flash("Incorrect username or password")

	return render_template('auth/login.html')

@app.route('/signup', methods=['GET', 'POST'])
def signup():

	if request.method == 'GET':
		pass

	if request.method == 'POST':
		email = request.form.get('email')
		username = request.form.get('username')

		if not check_email(email):
			flash("Invalid email")
			return redirect(url_for('signup'))

		if len(email) == 0:
			flash("Please enter an email address!")
			return redirect(url_for('signup'))

		if len(username) == 0:
			flash("Please enter a username!")
			return redirect(url_for('signup'))

		if not check_user_exists(username, email):
			password = request.form.get('password')
			passwordAgain = request.form.get('passwordagain')
			question = request.form.get('question')
			answer = request.form.get('answer')

			if len(password) == 0:
				flash("Please enter a password!")
				return redirect(url_for('signup'))

			if len(password) < 6:
				flash("Please make sure your password is at least six characters!")
				return redirect(url_for('signup'))

			if not password == passwordAgain:
				flash("Please make sure the passwords match!")
				return redirect(url_for('signup'))

			if len(question) == 0:
				flash("Please enter a security question!")
				return redirect(url_for('signup'))

			if len(answer) == 0:
				flash("Please answer your security question!")
				return redirect(url_for('signup'))

			session.clear()
			create_user(email, username, password, question, answer, False)

			msg = Message("New account made!",
				sender="threadfeeddotcom@gmail.com",
				recipients= ["ianzz1233@gmail.com", "iwilkey@iwu.edu"])
			msg.body = "user info: \nemail" + email + "\nusername: " + username
			mail.send(msg)

			flash("Account created successfully! Now, please log in")
			return redirect('login')

		else:
			flash("An account already exists with this username or email")
			return redirect('signup')

	return render_template('auth/signup.html')

@app.route('/about', methods=['GET'])
def about():
	return render_template('about.html')

@app.route('/forgot', methods=['GET', 'POST'])
def forgot():

	if request.method == 'GET':
		pass

	if request.method == 'POST':
		username = request.form.get('username')
		email = request.form.get('email')

		if not check_user_exists(username, email):
			flash("This account does not exist!")
			return redirect(url_for('forgot'))

		if not email_sync(username, email):
			flash("That email is not registered to this username!")
			return redirect(url_for('forgot'))

		user = get_user(username)
		if email == user[5]:
			msg = Message("Threadfeed Recovery",
				sender="threadfeeddotcom@gmail.com",
				recipients=[email])

			temporaryPass = password_generator()

			msg.body = "***DO NOT REPLY, ALL RESPONSES GET AUTOMATICALLY DELETED***\n\nHello! It seems you have requested a password recovery for your Threadfeed account. You have been supplied a temporary password. Use it to login, then change your password immediately.\n\nThank you for using Threadfeed!\n\nTEMPORARY PASSWORD: \n" + temporaryPass + "\n\nDON'T SHOW THIS INFORMATION TO ANYONE!"
			mail.send(msg)

			update_password(temporaryPass, username)
			flash("Further instructions have been sent to " + email + " from threadfeeddotcom@gmail.com. If you cannot find it, please check your spam folder.")

		else:
			flash("The username and email entered are not registered to the same account.")

	return render_template('auth/forgot.html')

#User route
@app.route('/profile', methods=['GET', 'POST'])
def profile():

	if session.get('username') == None:
		return redirect(url_for('login'))
	if get_user(session.get('username'))[6] == False:
		return redirect(url_for('activate'))

	if request.method == 'GET':
		pass

	if request.method == 'POST':
		if 'delete' in request.form:
			delete_post(session.get('username'), request.form['delete'])
		else:
			return redirect(url_for('create'))

	return render_template('user/profile.html')

@app.route('/create', methods=['GET', 'POST'])
def create():

	if session.get('username') == None:
		return redirect(url_for('login'))
	if get_user(session.get('username'))[6] == False:
		return redirect(url_for('activate'))
	if request.method == 'GET':
		pass

	if request.method == 'POST':
		thread = request.form.get('thread-post')
		user = session.get('username')

		if len(thread) == 0:
			return redirect(url_for('create'))

		create_post(user, thread)

		return redirect(url_for('profile'))

	return render_template('user/create.html')

@app.route('/vote', methods=['GET', 'POST'])
def vote():

	if session.get('username') == None:
		return redirect(url_for('login'))

	if get_user(session.get('username'))[6] == False:
		return redirect(url_for('activate'))

	if not return_random_thread(session.get('username')) == None:
		session['tiq'] = return_random_thread(session.get('username'))[0]
	else:
		session['tiq'] = None

	if request.method == 'GET':
		pass

	if request.method == 'POST' and (('yes' in request.form) or ('no' in request.form)):
		if 'yes' in request.form:
			thread_vote(session.get('username'), request.form['yes'], "yes")
			return redirect(url_for('vote'))
		if 'no' in request.form:
			thread_vote(session.get('username'), request.form['no'], "no")
			return redirect(url_for('vote'))

	return render_template('user/vote.html')

@app.route('/settings', methods=['GET', 'POST'])
def settings():

	if session.get('username') == None:
		return redirect(url_for('login'))
	elif get_user(session.get('username'))[6] == False:
		return redirect(url_for('activate'))
	else:

		if request.method == 'GET':
			pass

		if request.method == 'POST':
			if 'updateEmail' in request.form:
				currentE = request.form.get('cemail')
				newE = request.form.get('nemail')

				if len(newE) == 0:
					flash("Please enter a new email!")
					return redirect(url_for('settings'))

				if not check_email(newE):
					flash("New email is invalid.")
					return redirect(url_for('settings'))

				if email_sync(session.get('username'), currentE):
					update_email(newE, session.get('username'))
					flash("Email successfully updated!")
				else:
					flash("Current email entered is incorrect!")
					return redirect(url_for('settings'))

			if 'updatePass' in request.form:
				currentPass = request.form.get('cpass')
				newPass = request.form.get('npass')

				if len(newPass) == 0:
					flash("Please enter a new password.")
					return redirect(url_for('settings'))

				if len(newPass) < 6:
					flash("Please make sure your new password is at least six characters!")
					return redirect(url_for('settings'))

				if check_login(session.get('username'), currentPass):
					update_password(newPass, session.get('username'))
					flash("Password successfully updated!")
				else:
					flash("Current password entered is incorrect!")
					return redirect(url_for('settings'))

		return render_template('user/settings.html')

@app.route('/activate', methods=['GET', 'POST'])
def activate():

	if get_user(session.get('username'))[6] == True:
		return redirect(url_for('profile'))

	activationCode = get_user(session.get('username'))[7]

	if request.method == 'GET':

		msg = Message("Threadfeed Account Activation",
					sender="threadfeeddotcom@gmail.com",
					recipients=[get_user(session.get('username'))[5]])

		msg.body = "***DO NOT REPLY, ALL RESPONSES GET AUTOMATICALLY DELETED***\n\nHello! Thank you for signing up for a Threadfeed account! You have been supplied a code below that will activate your account. Copy and paste it into the field located on the activation page then click 'Check'.\n\nThank you for using Threadfeed!\n\nACTIVATION CODE: \n" + activationCode
		mail.send(msg)

	if request.method == 'POST':
		e_c = request.form.get('ac')
		if e_c == activationCode:
			activate_user(session.get('username'))
			return redirect(url_for('profile'))

	return render_template('user/activate.html')

@app.route('/threadweb', methods=['GET'])
def threadweb():

	if session.get('username') == None:
		return redirect(url_for('login'))

	if get_user(session.get('username'))[6] == False:
		return redirect(url_for('activate'))

	threadweb_algorithm()

	return render_template('user/threadweb.html')

