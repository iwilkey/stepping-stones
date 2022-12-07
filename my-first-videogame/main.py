#Dodge-A-Dot! Original game programmed by Ian Wilkey in Fall 2019
#Language: Python
#Using the pyxel library
#Project time: One week

#Import libraries
import pyxel
import random
import math

#Define constants
SCREENHEIGHT = 200
SCREENWIDTH = 200

#Define the player
class Player(object):
	#When initalized...
	def __init__(self, w, h, color):
		self.width = w
		self.height = h
		self._x = (SCREENWIDTH / 2)
		self._y = (SCREENHEIGHT / 2)
		self._color = color
	#Create a settable x value
	@property
	def x(self):
		return self._x
	@x.setter
	def x(self, value):
		self._x = value

	#Create a settable y value
	@property
	def y(self):
		return self._y
	@y.setter
	def y(self, value):
		self._y = value

#Define an enemy
class Enemy(object):
	#When initalized...
	def __init__(self, x, y):
		self._x = x
		self._y = y
		self.color = 7

	#Create a settable x value
	@property
	def x(self):
		return self._x
	@x.setter
	def x(self, value):
		self._x = value

	#Create a settable y value
	@property
	def y(self):
		return self._y
	@y.setter
	def y(self, value):
		self._y = value

#Define a money pickup
class Money(object):
	#When initalized...
	def __init__(self, x, y):
		self._x = x
		self._y = y
		self._isActive = True

	#Create a settable x value...
	@property
	def x(self):
		return self._x
	@x.setter
	def x(self, value):
		self._x = value

	#Create a settable y value...
	@property
	def y(self):
		return self._y
	@y.setter
	def y(self, value):
		self._y = value	

	#Create a settable isActive attribute
	@property
	def isActive(self):
		return self._isActive
	@isActive.setter
	def isActive(self, value):
		self._isActive = value
	
	#Change the possible x and y values for collisions and save them to a list
	def setCollisionValues(self):
		self.collisionValuesMoneyx = [(self.x + i) for i in range(4)]
		self.collisionValuesMoneyy = [(self.y + i) for i in range(4)]

#Define the goal object
class Goal(object):
	def __init__(self, x, y):
		self._x = x
		self._y = y

	#Create a settable x value
	@property
	def x(self):
		return self._x
	@x.setter
	def x(self, value):
		self._x = value

	#Create a settable y value
	@property
	def y(self):
		return self._y
	@y.setter
	def y(self, value):
		self._y = value
	
#Define the application
class Dodge_A_Dot_Core(object):
	#When initalized...
	def __init__(self, x, y, caption, fps):

		#Create the player and save it as player
		self.player = Player(3,3,8)

		#Define Game Variables
		self.collisionValuesPlayerx = []
		self.collisionValuesPlayery = []
		self.collisionValuesGoalx = []
		self.collisionValuesGoaly = []

		self.speed = 1
		self.speedMultiplier = 2
		self.amountEnemies = 20

		self.lives = 3
		self.money = 0
		self.bombs = 3
		self.freeze = 1
		self.energy = 10
		self.wave = 1
		self.energies = 5

		self.score = 0

		self.mainMenu = True
		self.gameOver = False
		self.shopOn = False
		self.freezed = False

		#Initalize the enemy list starting with 20, then scatter them across the screen
		self.allEnemies = [Enemy(0, 0) for i in range(self.amountEnemies)]

		#Redefine random positions for enemies
		for enemy in range(len(self.allEnemies)):
			self.allEnemies[enemy].x = random.randint(0, SCREENWIDTH)
			self.allEnemies[enemy].y = random.randint(0, SCREENHEIGHT)

		#Populate a money list
		self.allMoney = [Money(0,0) for i in range(3)]

		#Randomize their spawn
		for money in range(len(self.allMoney)):
			self.allMoney[money].x = random.randint(20, SCREENWIDTH - 10)
			self.allMoney[money].y = random.randint(60, SCREENHEIGHT - 10)

		#Create a goal
		self.goal = Goal(0, 0)
		#Set the goals location to somewhere random
		self.goal.x = random.randint(25, SCREENWIDTH - 10)
		self.goal.y = random.randint(60, SCREENHEIGHT - 10)

		#Initialize engine
		pyxel.init(x, y, caption = caption, fps = fps)
		#Import assets
		pyxel.load("assets.pyxres")
		#Update gamestate and draw while game is running
		pyxel.run(self.update, self.draw)

	##############
	# GAME LOGIC #
	##############

	#Define a function that will return the distance between two objects
	def distance(self, obj1_x, obj1_y, obj2_x, obj2_y):
		return abs(math.sqrt(((obj2_x - obj1_x)**2)+((obj2_y - obj1_y)**2)))

	#Populate the screen with enemies
	def populateEnemies(self, amount):
		#Populate enemy list
		self.allEnemies = [Enemy(0, 0) for i in range(amount)]

		#Redefine random positions for enemies making sure they don't spawn too close to the goal or the player.
		for enemy in range(len(self.allEnemies)):
			self.allEnemies[enemy].x = random.randint(10, SCREENWIDTH - 10)
			self.allEnemies[enemy].y = random.randint(10, SCREENHEIGHT - 10)
			if self.distance(self.goal.x, self.goal.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) <= 20:
				while self.distance(self.goal.x, self.goal.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) < 20:
					self.allEnemies[enemy].x = random.randint(10, SCREENWIDTH - 10)
					self.allEnemies[enemy].y = random.randint(10, SCREENHEIGHT - 10)

			if self.distance(self.player.x, self.player.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) <= 20:
				while self.distance(self.player.x, self.player.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) < 20:
					self.allEnemies[enemy].x = random.randint(10, SCREENWIDTH - 10)
					self.allEnemies[enemy].y = random.randint(10, SCREENHEIGHT - 10)

	#Populate the screen with three money pickups
	def populateMoney(self):
		#Populate a money list
		self.allMoney = [Money(0,0) for i in range(3)]

		#Randomize their spawn
		for money in range(len(self.allMoney)):
			self.allMoney[money].x = random.randint(20, SCREENWIDTH - 10)
			self.allMoney[money].y = random.randint(60, SCREENHEIGHT - 20)

	#Define how to respawn a goal
	def respawnGoal(self):
		self.goal.x = random.randint(20, SCREENWIDTH - 10)
		self.goal.y = random.randint(60, SCREENHEIGHT - 20)


	#Define what happens when death occurs
	def die(self):
		#Lose a life
		self.lives -= 1
		#Reset energy
		self.energy = 10
		if self.lives <= 0:
			self.gameOver = True
		#Reset the position of player
		self.player.x = (SCREENWIDTH / 2)
		self.player.y = (SCREENHEIGHT / 2)

	#Define what happens when you pickup money
	def addMoney(self):
		self.money += random.choice([100,200,300,400,500,1000,2000,5000,10000])
		self.score += 100

	#Define what happens when you achieve the goal
	def getGoal(self):
		#Resawn the goal
		self.respawnGoal()
		#Respawn new and more enemies
		self.allEnemies = []
		self.amountEnemies += 5
		self.populateEnemies(self.amountEnemies)
		#Respawn new money
		self.allMoney = []
		self.populateMoney()

		#Give merit
		self.money += 1000
		#Reset energy
		self.energy = 10
		#Increase wave:
		self.wave += 1
		#Give score
		self.score += 3000
		#Unfreeze the enemies if they are freezed
		if self.freezed:
			self.freezed = False

	#Define how to move
	def _movement(self):

		#Condition for speed multiplier
		if pyxel.btn(pyxel.KEY_SPACE):
			self.speed = 1 * self.speedMultiplier
		else:
			self.speed = 1

		#Define the controls for movement
		if pyxel.btn(pyxel.KEY_UP):
			self.player.y -= self.speed
		if pyxel.btn(pyxel.KEY_DOWN):
			self.player.y += self.speed
		if pyxel.btn(pyxel.KEY_RIGHT):
			self.player.x += self.speed
		if pyxel.btn(pyxel.KEY_LEFT):
			self.player.x -= self.speed

		#Create map wrapping
		self.player.x = self.player.x % SCREENWIDTH
		self.player.y = self.player.y % SCREENHEIGHT

	#Define useing a bomb
	def doBomb(self):
		if self.bombs >= 1:
			amountKilled = 0
			for enemy in range(len(self.allEnemies)):
				if self.distance(self.player.x, self.player.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) < 75:
					while self.distance(self.player.x, self.player.y, self.allEnemies[enemy].x, self.allEnemies[enemy].y) < 75:
						self.allEnemies[enemy].x = random.randint(0, SCREENWIDTH)
						self.allEnemies[enemy].y = random.randint(0, SCREENHEIGHT)
						amountKilled += 1

			self.bombs -= 1
			self.score += (250 * amountKilled)

	#Define using a freeze
	def doFreeze(self):
		if self.freeze >= 1:
			self.freezed = True
			self.freeze -= 1

	#Define using energy
	def doEnergy(self):
		if self.energies >= 1:
			if round(self.energy) < 10:
				self.energy += 1
				self.energies -= 1

	#Define how and when to depleat energy
	def useEnergy(self):

		if not self.shopOn:
			if self.energy > 0:
				if pyxel.btn(pyxel.KEY_UP):
					self.energy -= (self.speed / 100) * (self.wave / self.energies)
				if pyxel.btn(pyxel.KEY_DOWN):
					self.energy -= (self.speed / 100) * (self.wave / self.energies)
				if pyxel.btn(pyxel.KEY_RIGHT):
					self.energy -= (self.speed / 100) * (self.wave / self.energies)
				if pyxel.btn(pyxel.KEY_LEFT):
					self.energy -= (self.speed / 100) * (self.wave / self.energies)
		#Kill the player if no energy
		if self.energy <= 0:
			self.die()

	def _control(self):
		#Make a shop control button
		if pyxel.btn(pyxel.KEY_ENTER):
			self.shopOn = True
		else:
			self.shopOn = False

		#Shop controls
		if self.shopOn:
			if pyxel.btnp(pyxel.KEY_B):
				if self.money >= 6000:
					self.money -= 6000
					self.bombs += 1
			if pyxel.btnp(pyxel.KEY_L):
				if self.money >= 1000000:
					self.money -= 1000000
					self.lives += 1
			if pyxel.btnp(pyxel.KEY_F):
				if self.money >= 7000:
					self.money -= 7000
					self.freeze += 1
			if pyxel.btnp(pyxel.KEY_E):
				if self.money >= 1000:
					self.money -= 1000
					self.energies += 1

		#Out of shop controls
		if not self.shopOn:
			if pyxel.btnp(pyxel.KEY_B):
				self.doBomb()
			if pyxel.btnp(pyxel.KEY_F):
				self.doFreeze()
			if pyxel.btnp(pyxel.KEY_E):
				self.doEnergy()

	#Define how to move an enemy
	def _enemyMovement(self):
		#Some enemies will move left to right
		for enemy in range(0, len(self.allEnemies), 2):
			self.allEnemies[enemy].x += random.randint(-1,1)
		#Some enemies will move up and down
		for enemy in range(1, len(self.allEnemies), 2):
			self.allEnemies[enemy].y += random.randint(-1,1)

		#Create map wrapping
		for enemy in range(len(self.allEnemies)):
			self.allEnemies[enemy].x = self.allEnemies[enemy].x % SCREENWIDTH
			self.allEnemies[enemy].y = self.allEnemies[enemy].y % SCREENHEIGHT

	def collisionValues(self):
		#Values for player
		self.collisionValuesPlayerx = [(self.player.x + i) for i in range(3)]
		self.collisionValuesPlayery = [(self.player.y + i) for i in range(3)]

		#Update the collision values for the money
		for money in range(len(self.allMoney)):
			self.allMoney[money].setCollisionValues()

		#Change the possible x and y values for collisions and save them to a list
		self.collisionValuesGoalx = [(self.goal.x + i) for i in range(8)]
		self.collisionValuesGoaly = [(self.goal.y + i) for i in range(8)]


	#Handle collisions
	def collisionHandler(self):
		#Between any enemy and the player
		for enemy in range(len(self.allEnemies)):
			for x in self.collisionValuesPlayerx:
				for y in self.collisionValuesPlayery:
					if (x == self.allEnemies[enemy].x) and (y == self.allEnemies[enemy].y):
						self.die()

		#Between any money and the player
		for x_player in self.collisionValuesPlayerx:
			for y_player in self.collisionValuesPlayery:
				for money in range(len(self.allMoney)):
					for x_money in self.allMoney[money].collisionValuesMoneyx:
						for y_money in self.allMoney[money].collisionValuesMoneyy:
							if self.allMoney[money].isActive:
								if (x_money == x_player) and (y_money == y_player):
									#Add money
									self.addMoney()
									#Kill the object
									self.allMoney[money].isActive = False

		#Between a player and goal
		for x_player in self.collisionValuesPlayerx:
			for y_player in self.collisionValuesPlayery:
				for x_goal in self.collisionValuesGoalx:
					for y_goal in self.collisionValuesGoaly:
						if (x_player == x_goal) and (y_player == y_goal):
							#Increase wave:
							self.wave += 1
							self.getGoal()
				
	#Create a safe space for the player so that enemys cannot infultrate the spawn area
	def safeSpace(self):
		for enemy in range(len(self.allEnemies)):
			while self.distance(self.allEnemies[enemy].x, self.allEnemies[enemy].y, (SCREENWIDTH / 2), (SCREENHEIGHT / 2)) < 10:
				self.allEnemies[enemy].x = random.randint(0, SCREENWIDTH)
				self.allEnemies[enemy].y = random.randint(0, SCREENHEIGHT)

	#Control for Main Menu
	def mainMenuControl(self):
		if pyxel.btn(pyxel.KEY_ENTER):
			self.mainMenu = False

	#Control for Game over state
	def gameOverControl(self):
		if pyxel.btn(pyxel.KEY_ENTER):
			self.lives = 3 #Reset lives
			self.bombs = 3 #Reset bombs
			self.freeze = 1 #Reset freeze
			self.money = 0 #Reset money
			self.energy = 10 #Reset energy
			self.wave = 1 #Reset wave
			self.energies = 5 #Reset stored energy
			self.score = 0

			self.allMoney = []
			self.populateMoney()
			for money in range(len(self.allMoney)):
				self.allMoney[money].setCollisionValues()

			self.amountEnemies = 20 #Reset enemies
			self.allEnemies = []
			self.populateEnemies(self.amountEnemies)

			self.gameOver = False #Start again

	#Update gamestate
	def update(self):

		#Control for game
		if not self.mainMenu and not self.gameOver:
			self._control()

			if not self.shopOn and not self.freezed:
				self._enemyMovement()
			
			if not self.shopOn:
				self._movement()

			self.useEnergy()
			self.collisionHandler()
			self.collisionValues()
			self.safeSpace()
		#Control for mainmenu
		elif self.mainMenu:
			self.mainMenuControl()
		#Control for gameover
		elif self.gameOver:
			self.gameOverControl()

	#################
	# DRAWING LOGIC #
	#################

	#Define how to draw a player
	def drawPlayer(self, player):
		pyxel.rect(player.x, player.y, player.width, player.height, player._color)

	#Define how to draw an enemy
	def drawEnemy(self, enemy):
		pyxel.pix(enemy.x, enemy.y, enemy.color)

	#Define how to draw money
	def drawMoney(self, money):
		if money.isActive: #If the object is active, draw it
			pyxel.blt(money.x, money.y, 0, 16, 0, 23, 7, 0)

	def drawShop(self):
		pyxel.blt(((SCREENWIDTH - 112) / 2), ((SCREENHEIGHT - 128) / 2), 1, 0, 0, 112, 128, 0) #Box

		shopx = ((SCREENWIDTH - 112) / 2)
		shopy = ((SCREENHEIGHT - 128) / 2)

		pyxel.text((shopx + (112 / 2) - 9), shopy + 10, "Shop", 5) #Shop title

		pyxel.blt(shopx + 8, shopy + 25, 2, 0, 0, 8, 8, 0) #Bomb sprite
		pyxel.text(shopx + 19, shopy + 27, "Bombs: $6000.00 ea.", 9) #Buy bomb text
		pyxel.text(shopx + 29, shopy + 35, "Press [b] to buy x1", 4) #Instruction

		pyxel.blt(shopx + 8, shopy + 48,0,0,0,8,7,0) #Heart sprite
		pyxel.text(shopx + 19, shopy + 49, "Lives: $1 Million ea.", 9) #Buy lives text
		pyxel.text(shopx + 29, shopy + 57, "Press [l] to buy x1", 4) #Instruction

		pyxel.blt(shopx + 8, shopy + 69,2,8,8,16,16,0) #Clock sprite
		pyxel.text(shopx + 19, shopy + 70, "Freeze: $7000.00 ea.", 9) #Buy freeze text
		pyxel.text(shopx + 29, shopy + 78, "Press [f] to buy x1", 4) #Instruction

		pyxel.blt(shopx + 8, shopy + 89,2,16,56,23,65,0) #Energy sprite
		pyxel.text(shopx + 19, shopy + 90, "Energy: $1000.00 ea.", 9) #Buy energy text
		pyxel.text(shopx + 29, shopy + 98, "Press [e] to buy x1*", 4) #Instruction

	#How to draw the goal
	def drawGoal(self, goal):
		pyxel.blt(self.goal.x, self.goal.y, 0, 16, 8, 23, 15, 0)

	#How to draw the GUI
	def drawGUI(self):
		#Sprites
		pyxel.blt(2,5,0,0,0,8,7,0) #Heart sprite
		pyxel.blt(2,5,0,0,0,7,16,0) #Money sprite
		pyxel.blt(2,23, 2, 0, 0, 8, 8, 0) #Bomb sprite
		pyxel.blt(2, 32,2,8,8,16,16,0) #Clock sprite
		pyxel.blt(2, 42,2,16,56,23,65,0) #Energy sprite
		pyxel.blt((SCREENWIDTH / 2) - 55, SCREENHEIGHT - 12, 2, 16,56,23,65,0) #Energy Meter sprite

		#Texts
		pyxel.text(13, 5, str(self.lives), 8) #Display lives value
		pyxel.text(13, 15, "$" + str(self.money) + ".00", 11) #Display money value
		pyxel.text(13, 24, "x" + str(self.bombs) + "[b]", 7) #Display bomb value
		pyxel.text(13, 33, "x" + str(self.freeze) + "[f]", 7) #Display freeze value
		pyxel.text(13, 43, "x" + str(self.energies) + "[e]", 9) #Display energy value
		pyxel.text((SCREENWIDTH / 2) - 45, 5, "Shop: [enter]", 9) #Display energy value
		pyxel.text((SCREENWIDTH / 2) + 20, 5, "Score: " + str(self.score), 12) #Display energy value


		#Energy Bar
		if round(self.energy) >= 10:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "********** x energy stored", 11) #Display freeze value
		elif round(self.energy) == 9:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "*********- x energy stored", 11)
		elif round(self.energy) == 8:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "********-- x energy stored", 11)
		elif round(self.energy) == 7:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "*******--- x energy stored", 11)
		elif round(self.energy) == 6:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "******---- x energy stored", 10)
		elif round(self.energy) == 5:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "*****----- x energy stored", 10)
		elif round(self.energy) == 4:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "****------ x energy stored", 10)
		elif round(self.energy) == 3:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "***------- x energy stored", 10)
		elif round(self.energy) == 2:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "**-------- x energy stored", 8)
		elif round(self.energy) == 1:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "*--------- x energy stored", 8)
		elif round(self.energy) == 0:
			pyxel.text((SCREENWIDTH / 2) - 43, SCREENHEIGHT - 10, "---------- x energy stored", 8)

	def drawMainMenu(self):
		pyxel.cls(1) #Clear screen with [colkey] background
		pyxel.text(78, 100, "Dodge-A-Dot", 3) #Title!
		pyxel.text(58, 110, "Press [enter] to play!", 6) #Instructions

	def drawGameOver(self):
		pyxel.cls(1) #Clear screen
		pyxel.text(82, 100, "Game Over!", 8) #Display game over
		pyxel.text(48, 110, "Press [enter] to play again!", 6) #Instructions

	#Draw it to the screen
	def draw(self):

		#Draw the game, not the main menu or game over screen
		if not self.mainMenu and not self.gameOver:
			#Clear the screen each frame, background colkey=1
			pyxel.cls(1)
			#Draw the player
			self.drawPlayer(self.player)
			#Draw all enemies
			for enemy in self.allEnemies:
				self.drawEnemy(enemy)

			#Draw all money
			for money in self.allMoney:
				self.drawMoney(money)

			#Draw GUI
			self.drawGUI()

			#Draw goal
			self.drawGoal(self.goal)

			if self.shopOn:
				self.drawShop()

		#Draw main menu state
		elif self.mainMenu:
			self.drawMainMenu()
		#Draw game over state
		elif self.gameOver:
			self.drawGameOver()

#Run the defined application
Dodge_A_Dot_Core(SCREENWIDTH, SCREENHEIGHT, "Dodge-A-Dot!", 20)