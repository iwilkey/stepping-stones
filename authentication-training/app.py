from flask import Flask, render_template, url_for, redirect, request
from flask_cors import CORS
from models import check_login, create_account

app = Flask(__name__)
CORS(app)

@app.route('/', methods=['GET', 'POST'])
def main():
	return redirect(url_for('signin'))

@app.route('/signin', methods=['GET', 'POST'])
def signin():
	if request.method == 'GET':
		pass

	if request.method == 'POST':
		username = request.form.get('username')
		password = request.form.get('password')

		if not username == '' and not password == '':
			verdict = check_login(username, password)

			if verdict:
				return render_template('success.html')
			else:
				return render_template('failure.html')

	return render_template('index.html')

@app.route('/signup', methods=['GET', 'POST'])
def signup():
	if request.method == 'GET':
		pass

	if request.method == 'POST':
		email = request.form.get('email')
		username = request.form.get('username')
		password = request.form.get('password')

		if not email == '' and not username == '' and not password == '':
			create_account(email, username, password)

			return render_template('success.html')

	return render_template('signup.html')

if __name__ == '__main__':
	app.run(debug=True)