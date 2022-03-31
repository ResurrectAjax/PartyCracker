package PartyCracker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import General.GeneralMethods;
import Main.Main;

public class Cracker extends ItemStack{
	private Main main;
	private String name;
	Random random = new Random();
	
	private ItemStack reward;
	private boolean isShiny;
	private int explodeDelay;
	
	private List<Reward> possibleRewards = new ArrayList<Reward>();
	private List<Sound> possibleSounds = new ArrayList<Sound>();
	private List<String> possibleParticles = new ArrayList<String>();

	private Particle particle;
	private Sound sound;
	
	public Cracker(Main main, String name) {
		super(Material.valueOf(main.getConfig().getString("PartyCrackers." + name + ".material")));
		this.main = main;
		this.name = name;
		
		loadFromConfig();
	}
	
	private void loadFromConfig() {
		FileConfiguration config = main.getConfig();
		ConfigurationSection cracker = config.getConfigurationSection("PartyCrackers." + name);
		
		ConfigurationSection soundSection = config.getConfigurationSection("SoundEffects");
		for(String confSound : soundSection.getKeys(false)) {
			org.bukkit.Sound bukkitSound = org.bukkit.Sound.valueOf(soundSection.getString(confSound + ".sound"));
			float volume = (float)soundSection.getDouble(confSound + ".volume");
			float pitch = (float)soundSection.getDouble(confSound + ".pitch");
			
			possibleSounds.add(new Sound(bukkitSound, volume, pitch));
		}
		
		ConfigurationSection particleSection = config.getConfigurationSection("ParticleEffects");
		for(String confParticle : particleSection.getKeys(false)) {
			possibleParticles.add(confParticle);
		}
		
		ConfigurationSection rewards = cracker.getConfigurationSection("rewards");
		for(String reward : rewards.getKeys(false)) {
			String amountStr = rewards.getString(reward + ".amount");
			String chanceStr = rewards.getString(reward + ".chance");
			
			int amount;
			if(amountStr.contains("-")) amount = chooseAmountFromInterval(amountStr);
			else amount = Integer.parseInt(amountStr);
			
			double percentage = (double)(GeneralMethods.getIntFromString(chanceStr)[0])/100;
			
			ItemStack item = new ItemStack(Material.valueOf(reward), amount);
			possibleRewards.add(new Reward(item, percentage));
		}
		Collections.sort(possibleRewards, Comparator.comparing(rew -> rew.getPercentage()));
		
		
		this.isShiny = cracker.getBoolean("shinyeffect");
		this.explodeDelay = cracker.getInt("explodedelay");
		
		List<String> lore = new ArrayList<String>();
		for(String line : cracker.getStringList("lore")) {
			lore.add(GeneralMethods.format(line));
		}
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName(GeneralMethods.format(cracker.getString("name")));
		meta.setLore(lore);
		
		if(isShiny) meta.addEnchant(Enchantment.MENDING, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		this.setItemMeta(meta);
	
		
		String particle = cracker.getString("particle");
		if(particle != null) this.particle = getParticleByName(particle);
		else this.particle = chooseParticle();
		
		String sound = cracker.getString("sound");
		if(sound != null) this.sound = getSoundByName(sound);
		else this.sound = chooseSound();
		
		this.reward = chooseReward();
	}
	
	public ItemStack getReward() {
		return reward;
	}

	public Sound getSound() {
		return sound;
	}

	public String getName() {
		return name;
	}

	public boolean isShiny() {
		return isShiny;
	}

	public int getExplodeDelay() {
		return explodeDelay;
	}

	public Particle getParticle() {
		return particle;
	}
	
	private Particle chooseParticle() {
		int randomNum = random.nextInt(possibleParticles.size());
		String name = possibleParticles.get(randomNum);
		
		return getParticleByName(name);
	}
	
	private Sound chooseSound() {
		int randomNum = random.nextInt(possibleSounds.size());
		
		return possibleSounds.get(randomNum);
	}

	private Particle getParticleByName(String name) {
		ConfigurationSection particleSection = main.getConfig().getConfigurationSection("ParticleEffects." + name);
		String particleName = particleSection.getString("particle");
		String color = particleSection.getString("color");
		int amount = particleSection.getInt("amount");
		double offset = particleSection.getDouble("offset"), velocity = particleSection.getDouble("velocity");
		
		return new Particle(org.bukkit.Particle.valueOf(particleName), color, amount, offset, velocity);
	}
	
	private Sound getSoundByName(String name) {
		ConfigurationSection soundSection = main.getConfig().getConfigurationSection("SoundEffects." + name);
		
		org.bukkit.Sound bukkitSound = org.bukkit.Sound.valueOf(soundSection.getString("sound"));
		float volume = (float)soundSection.getDouble("volume");
		float pitch = (float)soundSection.getDouble("pitch");
		
		return new Sound(bukkitSound, volume, pitch);
	}
	
	private int chooseAmountFromInterval(String interval) {
		String[] split = interval.split("-");
		int lower = 0, upper = 0;
		
		if(GeneralMethods.isInteger(split[0]) && GeneralMethods.isInteger(split[1])) {
			lower = Integer.parseInt(split[0]);
			upper = Integer.parseInt(split[1]);
		}
		
		return random.nextInt((upper-lower) + 1) + lower;
	}
	
	public ItemStack chooseReward() {
		double randomNum = random.nextDouble();
		ItemStack rewardStack = null;
		for(Reward reward : possibleRewards) {
			if(randomNum > reward.getPercentage()) continue;
			rewardStack = reward.getItem();
			break;
		}
		if(rewardStack == null) {
			rewardStack = possibleRewards.get(possibleRewards.size()-1).getItem();
		}
		return rewardStack;
	}
	
	public class Particle {
		private org.bukkit.Color color;
		private org.bukkit.Particle particle;
		private int amount;
		private double offset, velocity;
		
		public double getVelocity() {
			return velocity;
		}

		public int getAmount() {
			return amount;
		}

		public Particle(org.bukkit.Particle particle, String color, int amount, double offset, double velocity) {
			this.particle = particle;
			
			Color javaColor = Color.decode(color);
			this.color = org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
			this.amount = amount;
			this.offset = offset;
			this.velocity = velocity;
		}

		public double getOffset() {
			return offset;
		}

		public org.bukkit.Color getColor() {
			return color;
		}

		public org.bukkit.Particle getParticle() {
			return particle;
		}
	}
	
	public class Sound {
		private org.bukkit.Sound sound;
		private float volume;
		private float pitch;
		
		public org.bukkit.Sound getSound() {
			return sound;
		}

		public float getVolume() {
			return volume;
		}

		public float getPitch() {
			return pitch;
		}

		public Sound(org.bukkit.Sound sound, float volume, float pitch) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
		}
	}
	
	public class Reward {
		private double percentage;
		private ItemStack item;
		
		public Reward(ItemStack item, double percentage) {
			this.item = item;
			this.percentage = percentage;
		}

		public double getPercentage() {
			return percentage;
		}

		public ItemStack getItem() {
			return item;
		}
	}
}
