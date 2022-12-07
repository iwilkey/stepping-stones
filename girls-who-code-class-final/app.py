import arcade
import random

HEIGHT = 500
WIDTH = 500
TITLE = "Toasty Visits Area 51!"

BACKDROP = "assets/backdrop.png"
TOASTY = "assets/toasty.png"
ALIEN = "assets/alien.png"
PROJECTILE = "assets/projectile.png"
LIGHTNING = "assets/lightning.png"
COFFEE = "assets/coffee.png"
PUG = "assets/pug.png"
PUG2 = "assets/pug2.png"
STORYSLATE = "assets/storyslate.png"
CREDITS = "assets/credits.png"
MUSIC = "assets/jimmyjohns.wav"
MAIN = "assets/Mainmenumusic.wav"


FONT = "assets/slkscr.ttf"

class StorySlate(arcade.Sprite):
	def __init__(self, WIDTH, HEIGHT):
		super().__init__(STORYSLATE, 1)
		self.center_x = WIDTH / 2
		self.center_y = HEIGHT / 2

class Credits(arcade.Sprite):
	def __init__(self, WIDTH, HEIGHT):
		super().__init__(CREDITS, 1)
		self.center_x = (WIDTH / 2) + 15
		self.center_y = HEIGHT / 2

class SpinningPug(arcade.Sprite):
	def __init__(self, WIDTH, y):
		super().__init__(TOASTY, 1)
		self.center_x = WIDTH / 2
		self.center_y = y
		self.angle = 0

	def spin(self):
		self.angle += 3
		self.change_angle = self.angle

class SpinningAlien(arcade.Sprite):
	def __init__(self, WIDTH, y):
		super().__init__(ALIEN, 1)
		self.center_x = WIDTH / 2
		self.center_y = y
		self.angle = 0

	def spin(self):
		self.scale = 2
		self.angle += 3
		self.change_angle = self.angle

class Backdrop(arcade.Sprite):
	def __init__(self, WIDTH, HEIGHT):
		super().__init__(BACKDROP, 1)
		self.center_x = WIDTH / 2
		self.center_y = HEIGHT / 2

class Toasty(arcade.Sprite):
	def __init__(self, x, y):
		super().__init__(TOASTY, 1)
		self.center_x = x
		self.center_y = y
		self.change_x = 0

	def update(self):
		self.center_x += self.change_x

		if self.center_x < 40:
			self.change_x = -self.change_x
		if self.center_x > WIDTH - 40:
			self.change_x = -self.change_x


class Alien(arcade.Sprite):
	def __init__(self, x, y):
		super().__init__(ALIEN, 1)
		self.center_x = x
		self.center_y = y
		self.change_y = 4

	def update(self):
		self.center_y -= self.change_y

class Projectile(arcade.Sprite):
	def __init__(self, x, y, PROJECTILE1):
		super().__init__(PROJECTILE1, 1)
		self.center_x = x
		self.center_y = y
		self.change_y = 6

	def update(self):

		"""
		CONGRATS! You found this weeks problem. Now... can you fix it?

		You've noticed that Toasty is shooting SIDE TO SIDE! 
		That's not our game! Toasty should shoot UP!

		Fixing this is easy as pie, you just have to know what to change in the one
		line of code below that looks like "self.center_x += self.change_y"

		Think of coordinates here:

			HINT

			center_x is RIGHT
			-center_x is LEFT
			
			center_y is UP
			-center_y is DOWN
		"""
		self.center_y += self.change_y

class Lightning(arcade.Sprite):
	def __init__(self, x, y):
		super().__init__(LIGHTNING, 1)
		self.center_x = x
		self.center_y = y
		self.change_y = 6
		self.name = "Lightning"

	def update(self):
		self.center_y -= self.change_y

class Coffee(arcade.Sprite):
	def __init__(self, x, y):
		super().__init__(COFFEE, 1)
		self.center_x = x
		self.center_y = y
		self.change_y = 5.5
		self.name = "Coffee"

	def update(self):
		self.center_y -= self.change_y

	def HoldAboveHead(self, x, y):
		self.center_y = y + 30
		self.center_x = x
		self.scale = 1.5

class App(arcade.Window):
	def __init__(self, width, height, title):
		super().__init__(width, height, title)
		self.backdrop = []
		self.toasty = []
		self.aliens = []
		self.projectiles = []
		self.powerups = []
		self.coffee = []
		self.storyslate = []
		self.credits = []
		self.spinningAlien = []

		#Game vars!
		self.gameState = "StorySlate"
		self.speed = 4
		self.level = 1
		self.score = 0
		self.lives = 5
		self.isPugShooting = False
		self.isInvincible = False
		self.powerupTimer = 8
		#End of gameVars!

		self.spawnClock = 0
		self.cooldown = 0.5
		self.levelCounter = 0

	def setup(self):
		self.backdrop = arcade.SpriteList()
		self.toasty = arcade.SpriteList()
		self.aliens = arcade.SpriteList()
		self.projectiles = arcade.SpriteList()
		self.powerups = arcade.SpriteList()
		self.coffee = arcade.SpriteList()
		self.storyslate = arcade.SpriteList()
		self.credits = arcade.SpriteList()
		self.spinningAlien = arcade.SpriteList()

		self.backdrop.append(Backdrop(WIDTH, HEIGHT))
		self.toasty.append(Toasty(WIDTH / 2, 70))
		self.storyslate.append(StorySlate(WIDTH, HEIGHT))
		self.storyslate.append(SpinningPug(WIDTH, HEIGHT - 50))
		self.credits.append(Credits(WIDTH, HEIGHT))
		self.spinningAlien.append(SpinningAlien(WIDTH, HEIGHT - 100))
		self.music = arcade.load_sound(MUSIC)
		self.mainmenumusic = arcade.load_sound(MAIN)

		#arcade.play_sound(self.mainmenumusic)

	def Reset(self):
		self.level = 1
		self.score = 0
		self.lives = 5
		self.spawnClock = 0
		self.cooldown = 0.5
		self.levelCounter = 0
		self.isInvincible = False
		self.isPugShooting = False

		for a in range(len(self.aliens)):
			self.aliens[0].remove_from_sprite_lists()

		for p in range(len(self.projectiles)):
			self.projectiles[0].remove_from_sprite_lists()

		for po in range(len(self.powerups)):
			self.powerups[0].remove_from_sprite_lists()

		self.toasty[0].center_x = WIDTH / 2
		self.toasty[0].center_y = 70
		self.toasty[0].change_x = 0

		self.gameState = "Playing"

		arcade.play_sound(self.music)

	def Shoot(self):
		if not self.isPugShooting:
			if self.cooldown == 0.5:
				self.projectiles.append(Projectile(self.toasty[0].center_x, self.toasty[0].center_y, PROJECTILE))
				self.cooldown = 0

		if self.isPugShooting:
			choice = random.randint(0,1)
			
			if choice == 0:
				self.projectiles.append(Projectile(self.toasty[0].center_x, self.toasty[0].center_y, PUG))
			if choice == 1:
				self.projectiles.append(Projectile(self.toasty[0].center_x, self.toasty[0].center_y, PUG2))

	def on_key_press(self, key, mod):
		if self.gameState == "Playing":
			if self.toasty[0].center_x < WIDTH - 60:
				if key == arcade.key.RIGHT:
					self.toasty[0].change_x = self.speed

			if self.toasty[0].center_x > 60:		
				if key == arcade.key.LEFT:
					self.toasty[0].change_x = -self.speed

			if key == arcade.key.SPACE:
				self.Shoot()
		elif self.gameState == "Game Over":
			if key == arcade.key.R:
				self.Reset()
			elif key == arcade.key.M:
				self.gameState = "StorySlate"
		elif self.gameState == "StorySlate":
			if key == arcade.key.ENTER:
				self.Reset()
			elif key == arcade.key.C:
				self.gameState = "Credits"
		elif self.gameState == "Credits":
			if key == arcade.key.ENTER:
				self.gameState = "StorySlate"

	def DrawSprites(self):
		self.backdrop.draw()
		self.projectiles.draw()
		self.toasty.draw()
		self.powerups.draw()
		self.aliens.draw()
		self.coffee.draw()

	def DrawText(self, state):
		if state == "Playing":
			arcade.draw_text("Score: " + str(self.score), 30, HEIGHT - 20, arcade.color.WHITE, 25, 
				align="center", anchor_x="left", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("Lives: " + str(self.lives), 30, HEIGHT - 50, arcade.color.RED, 25, 
				align="center", anchor_x="left", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("Level: " + str(self.level), 30, HEIGHT - 80, arcade.color.GREEN, 25, 
				align="center", anchor_x="left", anchor_y="center",
				font_name=FONT)

			if self.isPugShooting:
				arcade.draw_text("PUG TIME! ", WIDTH - 30, HEIGHT - 20, arcade.color.GOLD, 25, 
				align="center", anchor_x="right", anchor_y="center",
				font_name=FONT)

				arcade.draw_text(str(int(9 - self.powerupTimer)), WIDTH - 30, HEIGHT - 75, arcade.color.GOLD, 54, 
				align="center", anchor_x="right", anchor_y="center",
				font_name=FONT)

			if self.isInvincible:
				arcade.draw_text("COFFEE TIME! ", WIDTH - 30, HEIGHT - 20, arcade.color.GOLD, 20, 
				align="center", anchor_x="right", anchor_y="center",
				font_name=FONT)

				arcade.draw_text(str(int(9 - self.powerupTimer)), WIDTH - 30, HEIGHT - 75, arcade.color.GOLD, 54, 
				align="center", anchor_x="right", anchor_y="center",
				font_name=FONT)

		elif state == "StorySlate":
			arcade.draw_text("[ENTER] to start!", WIDTH / 2, HEIGHT / 2 - 70, arcade.color.RED, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("[C] for credits!", WIDTH / 2, HEIGHT / 2 - 100, arcade.color.GREEN, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)

		elif state == "Credits":
			arcade.draw_text("[ENTER] to return\n to Main menu!", WIDTH / 2, HEIGHT / 2 - 180, arcade.color.RED, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)

		elif state == "Game Over":
			arcade.draw_text("Game Over!", WIDTH / 2, HEIGHT / 2 + 30, arcade.color.RED, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("Score: " + str(self.score), WIDTH / 2, HEIGHT / 2, arcade.color.WHITE, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("Press [r] to try again!", WIDTH / 2, HEIGHT / 2 - 30, arcade.color.GREEN, 25, 
				align="center", anchor_x="center", anchor_y="center",
				font_name=FONT)
			arcade.draw_text("Press [m] to return\n to main menu!", WIDTH / 2, HEIGHT / 2 - 100, arcade.color.ORANGE, 25, 
					align="center", anchor_x="center", anchor_y="center",
					font_name=FONT)

	def on_draw(self):
		arcade.start_render()

		if self.gameState == "Playing":
			self.DrawSprites()
		
		if self.gameState == "StorySlate":
			self.storyslate.draw()

		if self.gameState == "Credits":
			self.credits.draw()

		if self.gameState == "Game Over":
			self.spinningAlien.draw()
		
		self.DrawText(self.gameState)


	def UpdateSprites(self):
		self.toasty[0].update()

		for i in range(len(self.aliens)):
			self.aliens[i].update()

		for p in range(len(self.projectiles)):
			self.projectiles[p].update()

		for po in range(len(self.powerups)):
			self.powerups[po].update()

		if self.isInvincible:
			self.coffee[0].HoldAboveHead(self.toasty[0].center_x, self.toasty[0].center_y)

	def SpawnObjects(self, delta_time):
		self.spawnClock += delta_time
		if (self.spawnClock > (1.5 * (1/self.level))):

			if not self.isPugShooting and not self.isInvincible:
				choice = random.randint(0,100)
				x = random.randint(30, WIDTH - 30)

				if choice <= 90:
					self.aliens.append(Alien(x, HEIGHT + 30))
				elif 90 < choice <= 95:
					self.powerups.append(Coffee(x, HEIGHT + 30))
				else:
					self.powerups.append(Lightning(x, HEIGHT + 30))

				self.spawnClock = 0
			else:
				choice = random.randint(0,100)
				x = random.randint(30, WIDTH - 30)
				self.aliens.append(Alien(x, HEIGHT + 30))
				self.spawnClock = 0

	def HandleCollisions(self):
		#Alien and floor
		if len(self.aliens) > 0:
			if self.aliens[0].center_y < 30:
				self.aliens[0].remove_from_sprite_lists()
				self.levelCounter += 1

		#Powerup and floor
		if len(self.powerups) > 0:
			if self.powerups[0].center_y < 30:
				self.powerups[0].remove_from_sprite_lists()

		#Powerups and toasty!
		for to in range(len(self.toasty)):
			for po in range(len(self.powerups)):
				if arcade.check_for_collision(self.powerups[po], self.toasty[to]):
					if self.powerups[po].alpha > 0:
						#Lightning:
						if self.powerups[po].name == "Lightning":
							self.powerups[po].alpha = 0
							print("Pug time!")
							self.powerupTimer = 0
							self.isInvincible = False
							self.isPugShooting = True
						#Coffee
						elif self.powerups[po].name == "Coffee":
							self.powerups[po].alpha = 0
							print("Coffee time!")
							self.powerupTimer = 0
							self.isPugShooting = False
							self.isInvincible = True
							self.coffee.append(Coffee(0,0))

		#Projectile and an alien
		for p in range(len(self.projectiles)):
			for a2 in range(len(self.aliens)):
				if arcade.check_for_collision(self.projectiles[p], self.aliens[a2]):
					if self.projectiles[p].alpha > 0 and self.aliens[a2].alpha > 0:
						self.aliens[a2].alpha = 0
						self.projectiles[p].alpha = 0
						self.score += 50
						self.levelCounter += 1

		#Toasty and an alien
		for t in range(len(self.toasty)):
			for a3 in range(len(self.aliens)):
				if arcade.check_for_collision(self.toasty[0], self.aliens[a3]):
					if self.aliens[a3].alpha > 0:
						if not self.isInvincible:
							self.aliens[a3].alpha = 0
							self.lives -= 1
						else:
							self.aliens[a3].alpha = 0
							self.score += 50

	def on_update(self, delta_time):
		if self.gameState == "Playing":

			if self.lives <= 0:
				self.gameState = "Game Over"

			self.UpdateSprites()
			self.SpawnObjects(delta_time)
			self.HandleCollisions()

			#Misc Game Logic

			if self.cooldown < 0.5:
				self.cooldown += delta_time
			elif self.cooldown > 0.5:
				self.cooldown = 0.5

			if self.isPugShooting or self.isInvincible:
				if self.powerupTimer < 8:
					self.powerupTimer += delta_time
				else:
					self.powerupTimer = 8
					self.isPugShooting = False
					self.isInvincible = False

					if len(self.coffee) > 0:
						self.coffee[0].remove_from_sprite_lists()

			if self.levelCounter > (10 * (self.level)):
				self.level += 1
				self.levelCounter = 0
				self.score += 100 * self.level

			if self.isInvincible or self.isPugShooting:
				for i in range(len(self.powerups)):
					self.powerups[0].remove_from_sprite_lists()

		elif self.gameState == "StorySlate":
			self.storyslate[1].spin()
		elif self.gameState == "Game Over":
			self.spinningAlien[0].spin()

app = App(WIDTH, HEIGHT, TITLE)
app.setup()
arcade.run()